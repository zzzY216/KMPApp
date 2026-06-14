package org.example.kmpapp.core.ble

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IBle {
    // 扫描
    val scanResults: Flow<List<BleDevice>>
    fun startScan()
    fun stopScan()

    val connectionState: StateFlow<BleConnectionState>

    suspend fun connect(deviceId: String): BleResult<Unit>

    suspend fun writeCharacteristic(
        serviceUuid: String,
        characteristicUuid: String,
        data: ByteArray
    ): BleResult<Unit>

    suspend fun readCharacteristic(
        serviceUuid: String,
        characteristicUuid: String
    ): BleResult<ByteArray>

    fun disconnect()
}