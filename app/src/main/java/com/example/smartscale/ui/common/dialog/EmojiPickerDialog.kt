package com.example.smartscale.ui.common.dialog

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.fragment.app.DialogFragment
import com.example.smartscale.R

class FoodEmojiPickerDialog(
    private val onEmojiSelected: (String) -> Unit
) : DialogFragment() {

    private val foodEmojis = listOf(
        "🍎", "🍏", "🍐", "🍊", "🍋",
        "🍌", "🍉", "🍇", "🍓", "🫐",
        "🍈", "🍒", "🍑", "🥭", "🍍",
        "🥥", "🥝", "🍅", "🍆", "🥑",
        "🥦", "🥬", "🥒", "🌶️", "🫑",
        "🌽", "🥕", "🫒", "🧄", "🧅",
        "🥔", "🍠", "🫘", "🥜", "🌰",
        "🍞", "🥐", "🥖", "🫓", "🥨",
        "🥯", "🥞", "🧇", "🧀", "🍖",
        "🍗", "🥩", "🥓", "🍔", "🍟",
        "🍕", "🌭", "🥪", "🌮", "🌯",
        "🫔", "🥙", "🧆", "🥚", "🍳",
        "🥘", "🍲", "🫕", "🥣", "🥗",
        "🍿", "🧈", "🧂", "🥫", "🍱",
        "🍘", "🍙", "🍚", "🍛", "🍜",
        "🍝", "🍠", "🍢", "🍣", "🍤",
        "🍥", "🥮", "🍡", "🥟", "🥠",
        "🥡", "🦪", "🍦", "🍧", "🍨",
        "🍩", "🍪", "🎂", "🍰", "🧁",
        "🥧", "🍫", "🍬", "🍭", "🍮",
        "🍯", "🥛", "🍼", "🫖", "☕",
        "🍵", "🧃", "🥤", "🍶", "🍾",
        "🍷", "🍸", "🍹", "🍺", "🍻",
        "🥂", "🥃", "🫗", "🧋", "🧊",
        "🥢", "🍽️", "🍴", "🥄", "🧂"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_emoji_picker, container, false)
        val gridView: GridView = view.findViewById(R.id.food_emoji_grid)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, foodEmojis)
        gridView.adapter = adapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            onEmojiSelected(foodEmojis[position])
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
