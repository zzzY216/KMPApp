package org.example.kmpapp.core.ble

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class BleRepository(
    private val bleManager: IBle
) {
    val scanResults: Flow<List<BleDevice>> = bleManager.scanResults
    val connectionState: StateFlow<BleConnectionState> = bleManager.connectionState
    fun startScanning() {
        bleManager.startScan()
    }

    fun stopScanning() {
        bleManager.stopScan()
    }

    suspend fun connectToDevice(device: BleDevice): BleResult<Unit> {
        return bleManager.connect(
            device.id
        )
    }

    fun disconnect() {
        bleManager.disconnect()
    }

    suspend fun readBatteryLevel(): BleResult<Int> {
        val result = bleManager.readCharacteristic(
            serviceUuid = "0000180f-",
            characteristicUuid = "00002a19-"
        )
        return when (result) {
            is BleResult.Success -> {
                val level = result.data.getOrNull(0)?.toInt() ?: 0
                BleResult.Success(level)
            }

            is BleResult.Error -> {
                BleResult.Error(result.message)
            }
        }
    }
    suspend fun sendCommandAndRead(command: String): BleResult<ByteArray> {
        val writeResult = bleManager.writeCharacteristic("S_UUID", "C_WRITE_UUID", command.encodeToByteArray())
        if (writeResult is BleResult.Error) return writeResult
        delay(100)

        return bleManager.readCharacteristic("S_UUID", "C_READ_UUID")
    }
}