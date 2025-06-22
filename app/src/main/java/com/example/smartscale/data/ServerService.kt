package com.example.smartscale.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.room.withTransaction
import com.example.smartscale.data.local.AppDatabase
import com.example.smartscale.data.local.entity.IngredientEntity
import com.example.smartscale.data.local.entity.MealEntity
import com.example.smartscale.data.local.entity.SyncStatus
import com.example.smartscale.data.remote.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

/**
 * Struktura pojedynczego elementu w ramce UDP wysyłanej z Raspberry Pi.
 * Przykładowy JSON z wagi:
 * [
 *   { "barcode": "5901234123457", "weight": 30.0 },
 *   { "barcode": "5909876543210", "weight": 15.5 }
 * ]
 */
data class ProductData(val barcode: String, val weight: Float)

/**
 * Serwis foreground – nasłuchuje UDP na porcie 8000,
 * parsuje listę produktów, tworzy nowy posiłek i przypisuje do niego
 * wszystkie odebrane składniki (IngredientEntity).
 */
class ServerService : Service() {

    private val channelId = "udp_channel"
    private lateinit var notificationManager: NotificationManager

    private var multicastLock: WifiManager.MulticastLock? = null
    private val gson      = Gson()
    private val api       = RetrofitClient.api
    private lateinit var database: AppDatabase

    /* --------------------------------------------------------------------- */
    /*  Service lifecycle                                                    */
    /* --------------------------------------------------------------------- */

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        showForegroundNotification()
        database = AppDatabase.getInstance(applicationContext)
        enableMulticast()
        Log.d("UDP", "✅ ServerService.onCreate()")
        startUdpReceiver()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /* --------------------------------------------------------------------- */
    /*  UDP-receiver                                                         */
    /* --------------------------------------------------------------------- */

    private fun startUdpReceiver() {
        Thread {
            try {
                val socket = DatagramSocket(null).apply {
                    reuseAddress = true
                    bind(InetSocketAddress(8000))
                }
                val buffer = ByteArray(4096)  // większy bufor na listę

                Log.d("UDP", "🟢 Czekam na dane UDP na porcie 8000...")

                while (!Thread.currentThread().isInterrupted) {
                    val packet  = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    Log.d("UDP", "✅ Odebrano: $message")
                    handleReceivedMessage(message)
                }

            } catch (e: Exception) {
                Log.e("UDP", "❌ Błąd UDP: ${e.message}", e)
            }
        }.start()
    }

    /* --------------------------------------------------------------------- */
    /*  Message processing                                                   */
    /* --------------------------------------------------------------------- */

    private fun handleReceivedMessage(message: String) {
        try {
            // 1) Zamieniamy JSON-ową tablicę na listę ProductData
            val listType = object : TypeToken<List<ProductData>>() {}.type
            val products: List<ProductData> = gson.fromJson(message, listType)
            if (products.isEmpty()) return

            // 2) Praca I/O w coroutine na Dispatchers.IO
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val ingredientDao = database.ingredientDao()
                    val mealDao       = database.mealDao()

                    // 3) Jedna transakcja – suspending API room-ktx
                    database.withTransaction {

                        // 3a) Tworzymy nowy posiłek
                        val mealId = mealDao.insertMeal(
                            MealEntity(
                                name     = "Automatyczny posiłek",
                                emoji    = "🍽",
                                dateTime = System.currentTimeMillis()
                            )
                        )

                        // 3b) Każdy wpis z listy -> IngredientEntity
                        products.forEach { pd ->
                            var entity = ingredientDao.getIngredientByBarcode(pd.barcode)

                            if (entity == null) {
                                // Brak w bazie – pobieramy opis z OpenFoodFacts
                                val product = api.getProductByCode(pd.barcode)
                                    .body()
                                    ?.product

                                entity = IngredientEntity(
                                    name            = product?.productName ?: "Nieznany produkt",
                                    barcode         = pd.barcode,
                                    weight          = pd.weight,
                                    caloriesPer100g = product?.nutriments?.energyKcal100g?.toFloat() ?: 0f,
                                    carbsPer100g    = product?.nutriments?.carbohydrates100g?.toFloat() ?: 0f,
                                    proteinPer100g  = product?.nutriments?.proteins100g?.toFloat() ?: 0f,
                                    fatPer100g      = product?.nutriments?.fat100g?.toFloat() ?: 0f,
                                    mealLocalId     = mealId.toString(),          // Long?  ←→  Long
                                    syncStatus      = SyncStatus.TO_SYNC
                                )
                            } else {
                                // Barcode był lokalnie – podpinamy pod posiłek i aktualizujemy wagę
                                entity = entity.copy(
                                    weight      = pd.weight,
                                    mealLocalId = mealId.toString(),
                                    syncStatus  = SyncStatus.TO_SYNC
                                )
                            }

                            ingredientDao.insertIngredient(entity)
                        }
                    } // koniec transakcji

                    updateForegroundNotification(
                        "Dodano posiłek • ${products.size} składników"
                    )

                } catch (e: Exception) {
                    Log.e("UDP", "❌ Błąd wewnątrz coroutine: ${e.message}", e)
                }
            }

        } catch (e: Exception) {
            Log.e("UDP", "❌ Błąd podczas parsowania ramki: ${e.message}", e)
        }
    }

    /* --------------------------------------------------------------------- */
    /*  Multicast                                                            */
    /* --------------------------------------------------------------------- */

    private fun enableMulticast() {
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifi.createMulticastLock("udp_lock").apply {
            setReferenceCounted(true)
            acquire()
        }
        Log.d("UDP", "✅ MulticastLock aktywowany")
    }

    /* --------------------------------------------------------------------- */
    /*  Foreground notification                                              */
    /* --------------------------------------------------------------------- */

    private fun showForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "UDP Serwer",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Waga: nasłuchiwanie UDP")
            .setContentText("Oczekuję danych z Raspberry Pi")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }

    private fun updateForegroundNotification(contentText: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Waga: nasłuchiwanie UDP")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(1, notification)
    }
}
