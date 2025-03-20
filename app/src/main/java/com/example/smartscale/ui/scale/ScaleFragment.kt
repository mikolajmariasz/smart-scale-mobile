package com.example.smartscale.ui.scale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartscale.databinding.FragmentScaleBinding

class ScaleFragment : Fragment() {

    private var _binding: FragmentScaleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scaleViewModel =
            ViewModelProvider(this).get(ScaleViewModel::class.java)

        _binding = FragmentScaleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textScale
        scaleViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}