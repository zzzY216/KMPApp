package org.example.kmpapp.core.ble

import android.content.Context

lateinit var globalContext: Context

actual fun getBleManager(): IBle = AndroidBleManager(globalContext)