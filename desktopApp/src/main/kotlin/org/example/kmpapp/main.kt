package org.example.kmpapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPApp",
    ) {
        App(isDesktop = true)
    }
}