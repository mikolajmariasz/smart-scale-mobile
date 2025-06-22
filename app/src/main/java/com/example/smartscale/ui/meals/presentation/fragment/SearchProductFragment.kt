package com.example.smartscale.ui.meals.presentation.fragment

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
import com.example.smartscale.ui.meals.presentation.adapter.ProductsAdapter
import com.example.smartscale.ui.meals.presentation.viewmodel.SearchProductViewModel
import com.example.smartscale.ui.meals.presentation.dialog.CustomProductDialogFragment
import android.util.Log

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
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.productsRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
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
                val weight = input.text.toString().toFloatOrNull() ?: 0f
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selectedProduct", product)
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selectedWeight", weight)
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
