package com.example.smartscale.data.remote

import com.example.smartscale.data.remote.model.SearchResponse
import com.example.smartscale.data.remote.model.SingleSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * OpenFoodFacts Search API.
 * Documentation: https://world.openfoodfacts.org/data
 */
interface OpenFoodFactsApi {

    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") simple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int,
        @Query("page") page: Int,
        @Query("fields") fields: String = "code,product_name,image_url,nutriments",
        @Query("countries") countries: String? = null
    ): Response<SearchResponse>

    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByCode(
        @Path("barcode") barcode: String
    ): Response<SingleSearchResponse>
}
