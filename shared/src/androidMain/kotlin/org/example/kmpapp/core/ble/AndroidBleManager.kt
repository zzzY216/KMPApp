package org.example.kmpapp.core.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume


class AndroidBleManager(
    private val context: Context
) : IBle {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bleScanner = bluetoothAdapter?.bluetoothLeScanner

    private var bluetoothGatt: BluetoothGatt? = null

    private val _scanResults = MutableStateFlow<List<BleDevice>>(emptyList())

    override val scanResults: Flow<List<BleDevice>> = _scanResults.asStateFlow()

    private val _connectionState =
        MutableStateFlow<BleConnectionState>(BleConnectionState.Disconnected)
    override val connectionState: StateFlow<BleConnectionState> = _connectionState.asStateFlow()

    private var connectContinuation: CancellableContinuation<BleResult<Unit>>? = null
    private var readContinuation: CancellableContinuation<BleResult<ByteArray>>? = null
    private var writeContinuation: CancellableContinuation<BleResult<Unit>>? = null

    private val scanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val device = BleDevice(
                id = result?.device?.address ?: "",
                name = result?.device?.name ?: "Unknown",
                rssi = result?.rssi ?: 0
            )
            Log.d("scanCallback", device.toString())
            val currentList = _scanResults.value.toMutableList()
            if (currentList.none {
                    it.id == device.id
                }) {
                currentList.add(device)
                _scanResults.value = currentList
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startScan() {
        _scanResults.value = emptyList()
        bleScanner?.startScan(scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        bleScanner?.stopScan(scanCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        _connectionState.value = BleConnectionState.Connected
                        gatt?.discoverServices()
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        _connectionState.value = BleConnectionState.Disconnected
                        gatt?.close()
                    }
                }
            } else {
                _connectionState.value = BleConnectionState.Failed("GATT Status:$status")
                connectContinuation?.resume(BleResult.Error("Connection failed with status $status"))
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                connectContinuation?.resume(BleResult.Success(Unit))
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                readContinuation?.resume(BleResult.Success(characteristic.value))
            } else {
                readContinuation?.resume(BleResult.Error("Read failed: $status"))
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeContinuation?.resume(BleResult.Success(Unit))
            } else {
                writeContinuation?.resume(BleResult.Error("Write failed: $status"))
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun connect(deviceId: String): BleResult<Unit> =
        suspendCancellableCoroutine { continuation ->
            val device = bluetoothAdapter?.getRemoteDevice(deviceId)
            if (device == null) {
                continuation.resume(BleResult.Error("Device not found"))
                return@suspendCancellableCoroutine
            }
            connectContinuation = continuation
            _connectionState.value = BleConnectionState.Connecting
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            continuation.invokeOnCancellation {
                disconnect()
            }
        }

    override suspend fun writeCharacteristic(
        serviceUuid: String,
        characteristicUuid: String,
        data: ByteArray
    ): BleResult<Unit> = suspendCancellableCoroutine { continuation ->
        val gatt = bluetoothGatt ?: run {
            continuation.resume(BleResult.Error("GATT not initialized"))
            return@suspendCancellableCoroutine
        }
        val service = gatt.getService(UUID.fromString(serviceUuid))
        val characteristic = service.getCharacteristic(UUID.fromString(characteristicUuid))
        if (characteristic != null) {
            writeContinuation = continuation
            characteristic.value = data
            gatt.writeCharacteristic(characteristic)
        } else {
            continuation.resume(BleResult.Error("Characteristic not found"))
        }
    }

    override suspend fun readCharacteristic(
        serviceUuid: String,
        characteristicUuid: String
    ): BleResult<ByteArray> = suspendCancellableCoroutine { continuation ->
        val gatt = bluetoothGatt ?: run {
            continuation.resume(BleResult.Error("GATT not initialized"))
            return@suspendCancellableCoroutine
        }

        val service = gatt.getService(UUID.fromString(serviceUuid))
        val characteristic = service?.getCharacteristic(UUID.fromString(characteristicUuid))

        if (characteristic != null) {
            readContinuation = continuation
            gatt.readCharacteristic(characteristic)
        } else {
            continuation.resume(BleResult.Error("Characteristic not found"))
        }
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.value = BleConnectionState.Disconnected
    }
}