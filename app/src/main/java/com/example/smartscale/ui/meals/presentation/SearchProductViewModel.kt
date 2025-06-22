package com.example.smartscale.ui.meals.presentation.viewmodel

import androidx.lifecycle.*
import com.example.smartscale.data.remote.RetrofitClient
import com.example.smartscale.data.remote.model.Product
import com.example.smartscale.data.remote.model.SingleSearchResponse
import com.example.smartscale.data.remote.model.SearchResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchProductViewModel : ViewModel() {

    private val _products   = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _loading    = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error      = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private var searchJob   : Job? = null
    private var currentPage = 1
    private var lastQuery   = ""



    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        lastQuery = query.trim()
        if (lastQuery.isBlank()) {
            _products.value = emptyList()
            return
        }
        currentPage = 1
        _products.value = emptyList()
        loadNextPage()
    }

    fun loadNextPage() {
        if (_loading.value == true || lastQuery.isBlank()) return

        if (lastQuery.all { it.isDigit() }) {
            if (currentPage > 1) return
            searchJob = viewModelScope.launch {
                _loading.value = true
                _error.value   = null
                try {
                    val resp: Response<SingleSearchResponse> =
                        RetrofitClient.api.getProductByCode(lastQuery)
                    if (resp.isSuccessful) {
                        resp.body()?.product?.let { p ->
                            _products.value = listOf(p)
                        } ?: run {
                            _products.value = emptyList()
                        }
                    } else {
                        _error.value = "Error ${resp.code()}: ${resp.message()}"
                    }
                } catch (t: Throwable) {
                    _error.value = t.localizedMessage
                } finally {
                    _loading.value = false
                }
            }
            return
        }

        searchJob = viewModelScope.launch {
            _loading.value = true
            _error.value   = null
            try {
                val resp: Response<SearchResponse> =
                    RetrofitClient.api.searchProducts(
                        query     = lastQuery,
                        pageSize  = 3,
                        page      = currentPage,
                        countries = "pl",
                        fields    = "code,product_name,image_url,nutriments"
                    )
                if (resp.isSuccessful) {
                    val fetched = resp.body()?.products.orEmpty()
                    _products.value = _products.value!!.plus(fetched)
                    if (fetched.isNotEmpty()) {
                        currentPage++
                    }
                } else {
                    _error.value = "Error ${resp.code()}: ${resp.message()}"
                }
            } catch (t: Throwable) {
                _error.value = t.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

}
