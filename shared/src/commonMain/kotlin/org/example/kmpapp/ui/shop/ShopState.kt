package org.example.kmpapp.ui.shop

import org.example.kmpapp.domain.Product

data class ShopState(
    val product: List<Product> = emptyList()
)