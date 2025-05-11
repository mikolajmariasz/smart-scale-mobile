package com.example.smartscale.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SearchResponse(
    @SerializedName("products")
    val products: List<Product>
)

data class SingleSearchResponse(
    @SerializedName("product")
    val product: Product?
)
@Parcelize
data class Product(
    @SerializedName("code")
    val code: String?,

    @SerializedName("product_name")
    val productName: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("nutriments")
    val nutriments: Nutriments?
) : Parcelable

@Parcelize
data class Nutriments(
    @SerializedName("energy-kcal_100g")
    val energyKcal100g: Float?,

    @SerializedName("proteins_100g")
    val proteins100g: Float?,

    @SerializedName("carbohydrates_100g")
    val carbohydrates100g: Float?,

    @SerializedName("fat_100g")
    val fat100g: Float?
) : Parcelable
