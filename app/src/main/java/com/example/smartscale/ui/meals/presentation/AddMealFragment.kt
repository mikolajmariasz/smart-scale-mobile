package com.example.smartscale.ui.meals.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.smartscale.databinding.FragmentAddMealBinding
import com.example.smartscale.ui.meals.presentation.dialog.FoodEmojiPickerDialog
import java.text.SimpleDateFormat
import java.util.*

class AddMealFragment : Fragment() {

    private var _binding: FragmentAddMealBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMealBinding.inflate(inflater, container, false)

        binding.editMealDateTime.setOnClickListener {
            showDateTimePicker()
        }

        binding.emojiPicker.setOnClickListener {
            showCustomEmojiPicker()
        }

        binding.saveMealButton.setOnClickListener {
            // TODO: zapisz dane posiłku
        }

        return binding.root
    }

    private fun showDateTimePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                calendar.set(year, month, day, hour, minute)
                val sdf = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
                binding.editMealDateTime.setText(sdf.format(calendar.time))
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showCustomEmojiPicker() {
        val dialog = FoodEmojiPickerDialog { selectedEmoji ->
            binding.emojiPicker.text = selectedEmoji
        }
        dialog.show(parentFragmentManager, "FoodEmojiPicker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
