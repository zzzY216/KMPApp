package org.example.kmpapp.core.ble

data class BleDevice(
    val id: String,
    val name: String,
    val rssi: Int
)

sealed class BleConnectionState {
    object Disconnected : BleConnectionState()
    object Connecting : BleConnectionState()
    object Connected : BleConnectionState()
    data class Failed(val message: String) : BleConnectionState()
}

sealed class BleResult<out T> {
    data class Success<T>(val data: T) : BleResult<T>()
    data class Error(val message: String) : BleResult<Nothing>()
}