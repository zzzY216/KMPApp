package org.example.kmpapp

import kotlinx.serialization.Serializable

@Serializable
object HomeScreenRoute

@Serializable
object VideoScreenRoute

@Serializable
object ShopScreenRoute

@Serializable
data class ProductDetailRoute(
    val name: String,
    val price: String
)
@Serializable
object ProfileScreenRoute