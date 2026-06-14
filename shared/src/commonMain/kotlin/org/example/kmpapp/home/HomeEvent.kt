package org.example.kmpapp.home

sealed class HomeEvent {
    object ToggleScan : HomeEvent()
    data class ConnectToDevice(val deviceId: String) : HomeEvent()
    object Disconnect : HomeEvent()
}