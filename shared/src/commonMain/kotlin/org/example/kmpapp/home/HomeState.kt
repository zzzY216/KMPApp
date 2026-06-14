package org.example.kmpapp.home

import org.example.kmpapp.core.ble.BleConnectionState
import org.example.kmpapp.core.ble.BleDevice

data class HomeState(
    val devices: List<BleDevice> = emptyList(),
    val currentDevice: BleDevice? = null,
    val isLoading: Boolean = false,
    val connectionState: BleConnectionState = BleConnectionState.Disconnected,
    val errorMessage: String? = null,
    val isScanning: Boolean = false
)