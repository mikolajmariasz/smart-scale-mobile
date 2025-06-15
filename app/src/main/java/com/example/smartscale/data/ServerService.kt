package com.example.smartscale

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import android.net.wifi.WifiManager
import android.content.Context

class ServerService : Service() {

    private var multicastLock: WifiManager.MulticastLock? = null

    override fun onCreate() {
        super.onCreate()
        enableMulticast()
        Log.d("UDP", "✅ ServerService.onCreate()")

        showForegroundNotification()
        startUdpReceiver()
    }

    init {
        Log.d("UDP", "🔄 ServerService KLASA ZAŁADOWANA")
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
                }

            } catch (e: Exception) {
                Log.e("UDP", "❌ Błąd UDP: ${e.message}")
            }
        }.start()
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

    override fun onBind(intent: Intent?): IBinder? = null
}
