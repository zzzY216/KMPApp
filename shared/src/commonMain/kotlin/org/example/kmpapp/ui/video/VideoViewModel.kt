package org.example.kmpapp.ui.video

import androidx.lifecycle.ViewModel
import org.example.kmpapp.core.network.NetworkClient

class VideoViewModel: ViewModel() {
    private val networkClient = NetworkClient()
    private val apiService = networkClient.apiService
}