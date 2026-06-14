package org.example.kmpapp.ui.shop

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.kmpapp.domain.Product

class ShopViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ShopState())
    val uiState = _uiState.asStateFlow()

    init {
        initData()
    }

    private fun initData() {
        val fakeList = listOf(
            Product(1, "1", "1"),
            Product(1, "1", "1"),
            Product(1, "1", "1"),
            Product(1, "1", "1"),
            Product(1, "1", "1"),
            Product(1, "1", "1")
        )
        _uiState.update {
            it.copy(
                product = fakeList
            )
        }
    }
}