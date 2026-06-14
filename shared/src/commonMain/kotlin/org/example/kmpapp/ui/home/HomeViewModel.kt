package org.example.kmpapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kmpapp.core.ble.BleResult
import org.example.kmpapp.core.ble.getBleManager

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()
    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    private val bleManager = getBleManager()

    init {
        observeBleData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ConnectToDevice -> connectToDevice(event.deviceId)
            HomeEvent.Disconnect -> disconnect()
            HomeEvent.ToggleScan -> toggleScan()
        }
    }

    private fun observeBleData() {
        viewModelScope.launch {
            bleManager.scanResults.collect { deviceList ->
                _uiState.update {
                    it.copy(devices = deviceList)
                }
            }
        }
        viewModelScope.launch {
            bleManager.connectionState.collect { state ->
                _uiState.update {
                    it.copy(
                        connectionState = state
                    )
                }
            }
        }
    }

    private fun toggleScan() {
        val currentlyScanning = _uiState.value.isScanning
        if (currentlyScanning) {
            bleManager.stopScan()
            _uiState.update {
                it.copy(
                    isScanning = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isScanning = true,
                    errorMessage = ""
                )
            }
            bleManager.startScan()
        }
    }

    private fun connectToDevice(deviceId: String) {
        viewModelScope.launch {
            if (_uiState.value.isScanning) {
                toggleScan()
            }
            _effect.send(HomeEffect.ShowToast("开始尝试连接"))
            val result = bleManager.connect(deviceId)
            when (result) {
                is BleResult.Error -> {
                    _effect.send(HomeEffect.ShowToast(result.message))
                    _uiState.update {
                        it.copy(
                            errorMessage = result.message
                        )
                    }
                }

                is BleResult.Success<*> -> {
                    _effect.send(HomeEffect.ShowToast("连接成功"))
                }
            }
        }
    }

    private fun disconnect() {
        bleManager.disconnect()
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}