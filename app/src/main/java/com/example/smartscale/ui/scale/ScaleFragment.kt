package com.example.smartscale.ui.scale

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartscale.databinding.FragmentScaleBinding

class ScaleFragment : Fragment() {

    private var _binding: FragmentScaleBinding? = null
    private val binding get() = _binding!!

    private val REQUIRED_SSID = "ScaleParing"
    private lateinit var scaleViewModel: ScaleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scaleViewModel = ViewModelProvider(this)[ScaleViewModel::class.java]
        _binding = FragmentScaleBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val clientIdTextView: TextView = binding.clientIdText
        scaleViewModel.clientId.observe(viewLifecycleOwner) {
            clientIdTextView.text = it
        }


        val textView: TextView = binding.textScale
        scaleViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        checkLocationPermission()


        textView.setOnClickListener {
            checkAndOpenScalePage()
        }

        return root
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun checkAndOpenScalePage() {
        val wifiManager = requireContext().applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        val currentSSID = wifiManager.connectionInfo.ssid.replace("\"", "")

        if (currentSSID == REQUIRED_SSID) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Połączenie z wagą")
            builder.setMessage("Czy chcesz otworzyć stronę konfiguracji wagi?")
            builder.setPositiveButton("Tak") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.4.1"))
                startActivity(intent)
            }
            builder.setNegativeButton("Anuluj", null)
            builder.show()
        } else {
            Toast.makeText(
                requireContext(),
                "Połącz się z siecią WiFi: $REQUIRED_SSID",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
