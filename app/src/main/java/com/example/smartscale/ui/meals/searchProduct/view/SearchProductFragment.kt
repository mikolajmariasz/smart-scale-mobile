package com.example.smartscale.ui.meals.searchProduct.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import com.example.smartscale.R
import com.example.smartscale.data.remote.model.Product
import com.example.smartscale.databinding.FragmentSearchProductBinding
import com.example.smartscale.ui.meals.searchProduct.adapter.ProductsAdapter
import com.example.smartscale.ui.meals.searchProduct.viewModel.SearchProductViewModel
import com.example.smartscale.ui.meals.searchProduct.dialog.CustomProductDialogFragment
import android.util.Log
import com.example.smartscale.domain.model.Ingredient

class SearchProductFragment : Fragment() {

    private var _binding: FragmentSearchProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchProductViewModel by viewModels()
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupInfiniteScroll()
        setupSearchInput()
        setupAddCustomFab()
        observeViewModel()
    }

    private fun setupRecycler() {
        productsAdapter = ProductsAdapter(emptyList()) { product ->
            showWeightDialog(product)
        }
        binding.productsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productsAdapter
        }
    }

    private fun setupInfiniteScroll() {
        binding.productsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val lm = rv.layoutManager as LinearLayoutManager
                val total = lm.itemCount
                val lastVisible = lm.findLastVisibleItemPosition()
                if (lastVisible >= total - 3) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun setupSearchInput() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onQueryChanged(text.toString())
        }
    }

    private fun setupAddCustomFab() {
        binding.addCustomProductFab.setOnClickListener {
            CustomProductDialogFragment { ingredient ->
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("newIngredient", ingredient)
                findNavController().popBackStack()
            }.show(parentFragmentManager, "CustomProductDialog")
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { list ->
            productsAdapter.updateList(list)
        }
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            productsAdapter.setLoadingEnabled(isLoading)
        }
        viewModel.error.observe(viewLifecycleOwner) { err ->
            err?.let { Log.e("SearchProduct", "API error: $it") }
        }
    }

    private fun showWeightDialog(product: Product) {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.enter_weight)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        AlertDialog.Builder(requireContext())
            .setTitle(product.productName ?: product.code)
            .setView(input)
            .setPositiveButton(R.string.ok) { _, _ ->
                Log.d("SearchProduct", "OK clicked, product=$product")
                val weight = input.text.toString().toFloatOrNull() ?: 0f
                Log.d("SearchProduct", "Parsed weight=$weight")
                // Tworzymy gotowy Ingredient
                val newIng = Ingredient(
                    name            = product.productName.orEmpty(),
                    weight          = weight,
                    caloriesPer100g = product.nutriments?.energyKcal100g  ?: 0f,
                    carbsPer100g    = product.nutriments?.carbohydrates100g ?: 0f,
                    proteinPer100g  = product.nutriments?.proteins100g     ?: 0f,
                    fatPer100g      = product.nutriments?.fat100g          ?: 0f
                )
                Log.d("SearchProduct", "New Ingredient: $newIng")
                // Wrzucamy pod jednym kluczem
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("newIngredient", newIng)
                Log.d("SearchProduct", "Saved to stateHandle, popping")
                findNavController().popBackStack()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
