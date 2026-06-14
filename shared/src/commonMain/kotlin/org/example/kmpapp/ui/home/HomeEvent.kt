package org.example.kmpapp.ui.home

sealed class HomeEvent {
    object ToggleScan : HomeEvent()
    data class ConnectToDevice(val deviceId: String) : HomeEvent()
    object Disconnect : HomeEvent()
}

sealed class HomeEffect {
    data class ShowToast(val message: String): HomeEffect()
}