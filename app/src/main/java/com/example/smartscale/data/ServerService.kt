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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

/**
 * Data structure for a single product entry received from Raspberry Pi.
 */
data class ProductData(val barcode: String, val weight: Float)

/**
 * Foreground service that listens for UDP packets on port 8000 and creates
 * new meals with the received ingredients.
 */
class ServerService : Service() {

    private val channelId = "udp_channel"
    private lateinit var notificationManager: NotificationManager

    private var multicastLock: WifiManager.MulticastLock? = null
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

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startUdpReceiver() {
        Thread {
            try {
                val socket = DatagramSocket(null).apply {
                    reuseAddress = true
                    bind(InetSocketAddress(8000))
                }
                val buffer = ByteArray(4096)

                Log.d("UDP", "🟢 Listening for UDP data on port 8000...")

                while (!Thread.currentThread().isInterrupted) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    Log.d("UDP", "✅ Received: $message")
                    handleReceivedMessage(message)
                }

            } catch (e: Exception) {
                Log.e("UDP", "❌ UDP error: ${e.message}", e)
            }
        }.start()
    }

    private fun handleReceivedMessage(message: String) {
        try {
            val products = mutableListOf<ProductData>()

            val lines = message.lines().map { it.trim() }.filter { it.isNotEmpty() }

            for (line in lines) {
                if (line.startsWith("CLIENT_ID")) continue

                val parts = line.split("|").map { it.trim() }
                if (parts.size != 2) continue

                val barcode = parts[0]
                val weightRegex = Regex("""Waga:\s*(\d+(?:\.\d+)?)""")
                val weightMatch = weightRegex.find(parts[1])

                val weight = weightMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 0f

                products.add(ProductData(barcode, weight))
            }

            if (products.isEmpty()) {
                Log.w("UDP", "⚠ No valid data in message: $message")
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val ingredientDao = database.ingredientDao()
                    val mealDao = database.mealDao()

                    database.withTransaction {
                        val mealId = java.util.UUID.randomUUID().toString()
                        val meal = MealEntity(
                            localId = mealId,
                            name = "Automatyczny posiłek",
                            emoji = "🍽",
                            dateTime = System.currentTimeMillis()
                        )
                        mealDao.insertMeal(meal)

                        products.forEach { pd ->
                            val product = api.getProductByCode(pd.barcode).body()?.product
                            val entity = IngredientEntity(
                                mealLocalId = mealId,
                                name = product?.productName ?: "Nieznany produkt",
                                weight = pd.weight,
                                caloriesPer100g = product?.nutriments?.energyKcal100g ?: 0f,
                                carbsPer100g = product?.nutriments?.carbohydrates100g ?: 0f,
                                proteinPer100g = product?.nutriments?.proteins100g ?: 0f,
                                fatPer100g = product?.nutriments?.fat100g ?: 0f,
                                syncStatus = SyncStatus.TO_SYNC
                            )
                            ingredientDao.insertIngredient(entity)
                        }
                    }

                    updateForegroundNotification("Dodano posiłek • ${products.size} składników")

                } catch (e: Exception) {
                    Log.e("UDP", "❌ Error inside coroutine: ${e.message}", e)
                }
            }

        } catch (e: Exception) {
            Log.e("UDP", "❌ Error parsing message: ${e.message}", e)
        }
    }

    private fun enableMulticast() {
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifi.createMulticastLock("udp_lock").apply {
            setReferenceCounted(true)
            acquire()
        }
        Log.d("UDP", "✅ MulticastLock acquired")
    }

    private fun showForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "UDP Server",
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

