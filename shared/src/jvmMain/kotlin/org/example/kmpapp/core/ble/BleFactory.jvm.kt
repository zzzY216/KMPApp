package org.example.kmpapp.core.ble

import kotlinx.coroutines.flow.MutableStateFlow

actual fun getBleManager(): IBle = object : IBle {
    override val scanResults = MutableStateFlow<List<BleDevice>>(emptyList())
    override val connectionState = MutableStateFlow(BleConnectionState.Disconnected)

    override fun startScan() {}
    override fun stopScan() {}
    override suspend fun connect(deviceId: String) = BleResult.Success(Unit)
    override fun disconnect() {}
    override suspend fun writeCharacteristic(s: String, c: String, d: ByteArray) =
        BleResult.Success(Unit)

    override suspend fun readCharacteristic(s: String, c: String) = BleResult.Success(ByteArray(0))
}