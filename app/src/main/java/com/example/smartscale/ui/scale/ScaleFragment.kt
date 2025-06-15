package com.example.smartscale.ui.scale

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartscale.databinding.FragmentScaleBinding
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder


class ScaleFragment : Fragment() {

    private var _binding: FragmentScaleBinding? = null
    private val binding get() = _binding!!

    private lateinit var scaleViewModel: ScaleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scaleViewModel = ViewModelProvider(this)[ScaleViewModel::class.java]
        _binding = FragmentScaleBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.generateQrButton.setOnClickListener {
            scaleViewModel.updateClientIdInput(binding.clientIdInput.text.toString())
            scaleViewModel.updateWifiNameInput(binding.wifiNameInput.text.toString())
            scaleViewModel.updateWifiPasswordInput(binding.wifiPasswordInput.text.toString())

            val qrContent = scaleViewModel.generateQrContent()
            if (qrContent != null) {
                val qrBitmap = generateQrCode(qrContent)
                if (qrBitmap != null) {
                    binding.qrImageView.setImageBitmap(qrBitmap)
                } else {
                    Toast.makeText(context, "Błąd generowania kodu QR", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun generateQrCode(data: String): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500)
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
