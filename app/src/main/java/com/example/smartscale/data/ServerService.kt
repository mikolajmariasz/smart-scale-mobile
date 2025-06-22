package com.example.smartscale.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smartscale.data.local.AppDatabase
import com.example.smartscale.data.local.entity.IngredientEntity
import com.example.smartscale.data.remote.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import android.net.wifi.WifiManager

data class ProductData(val barcode: String, val weight: Float)

class ServerService : Service() {
    private val channelId = "udp_channel"
    private lateinit var notificationManager: NotificationManager

    private var multicastLock: WifiManager.MulticastLock? = null
    private val gson = Gson()
    private val api = RetrofitClient.api
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        showForegroundNotification()
        database = AppDatabase.getInstance(applicationContext)
        enableMulticast()
        Log.d("UDP", "✅ ServerService.onCreate()")
        startUdpReceiver()
    }

    private fun startUdpReceiver() {
        Thread {
            try {
                val socket = DatagramSocket(null)
                socket.reuseAddress = true
                socket.bind(InetSocketAddress(8000))
                val buffer = ByteArray(1024)
                Log.d("UDP", "🟢 Czekam na dane UDP na porcie 8000...")

                while (!Thread.currentThread().isInterrupted) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    Log.d("UDP", "✅ Odebrano: $message")
                    handleReceivedMessage(message)
                }

            } catch (e: Exception) {
                Log.e("UDP", "❌ Błąd UDP: ${e.message}")
            }
        }.start()
    }

    private fun handleReceivedMessage(message: String) {
        try {
            val productData = gson.fromJson(message, ProductData::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val ingredientDao = database.ingredientDao()
                    val existingIngredient = ingredientDao.getIngredientByBarcode(productData.barcode)

                    if (existingIngredient == null) {
                        Log.d("UDP", "🔎 Produkt nie znaleziony lokalnie, pobieram z API...")

                        val response = api.getProductByCode(productData.barcode)
                        val product = response.body()?.product

                        if (product != null) {
                            val newEntity = IngredientEntity(
                                name = product.productName ?: "Nieznany produkt",
                                barcode = productData.barcode,
                                weight = productData.weight,
                                caloriesPer100g = product.nutriments?.energyKcal100g?.toFloat() ?: 0f,
                                carbsPer100g = product.nutriments?.carbohydrates100g?.toFloat() ?: 0f,
                                proteinPer100g = product.nutriments?.proteins100g?.toFloat() ?: 0f,
                                fatPer100g = product.nutriments?.fat100g?.toFloat() ?: 0f,
                                mealLocalId = null,
                                syncStatus = com.example.smartscale.data.local.entity.SyncStatus.TO_SYNC
                            )
                            ingredientDao.insertIngredient(newEntity)
                            Log.d("UDP", "💾 Produkt zapisany w bazie danych Room")
                            updateForegroundNotification("Dodano produkt: ${newEntity.name} (${newEntity.weight} g)")
                        } else {
                            Log.w("UDP", "⚠ Nie udało się pobrać produktu z API")
                        }
                    } else {
                        Log.d("UDP", "📦 Produkt już istnieje w lokalnej bazie danych")
                    }
                } catch (e: Exception) {
                    Log.e("UDP", "❌ Błąd wewnątrz coroutine: ${e.message}", e)
                }
            }

        } catch (e: Exception) {
            Log.e("UDP", "❌ Błąd podczas przetwarzania wiadomości: ${e.message}", e)
        }
    }

    private fun enableMulticast() {
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifi.createMulticastLock("udp_lock")
        multicastLock?.setReferenceCounted(true)
        multicastLock?.acquire()
        Log.d("UDP", "✅ MulticastLock aktywowany")
    }

    private fun showForegroundNotification() {
        val channelId = "udp_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "UDP Serwer", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
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


    override fun onBind(intent: Intent?): IBinder? = null
}
