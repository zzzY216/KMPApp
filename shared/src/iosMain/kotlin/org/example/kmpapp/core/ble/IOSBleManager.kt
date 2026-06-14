package org.example.kmpapp.core.ble

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.kmpapp.toNSData
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBCentralManagerStatePoweredOn
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBCharacteristicWriteWithResponse
import platform.CoreBluetooth.CBPeripheral
import platform.CoreBluetooth.CBPeripheralDelegateProtocol
import platform.CoreBluetooth.CBService
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IOSBleManager : NSObject(), IBle, CBCentralManagerDelegateProtocol,
    CBPeripheralDelegateProtocol {
    // 用于保存读操作的挂起状态
    private var readContinuation: ((BleResult<ByteArray>) -> Unit)? = null

    // 用于保存写操作的挂起状态
    private var writeContinuation: ((BleResult<Unit>) -> Unit)? = null
    private val centralManager = CBCentralManager(this, null)
    private val discoveredPeripherals = mutableMapOf<String, CBPeripheral>()
    private var activePeripheral: CBPeripheral? = null
    private var connectContinuation: ((BleResult<Unit>) -> Unit)? = null

    override val scanResults = MutableStateFlow<List<BleDevice>>(emptyList())
    private val _connectionState =
        MutableStateFlow<BleConnectionState>(BleConnectionState.Disconnected)
    override val connectionState = _connectionState.asStateFlow()

    override fun startScan() {
        if (centralManager.state == CBCentralManagerStatePoweredOn) {
            discoveredPeripherals.clear()
            scanResults.value = emptyList()
            centralManager.scanForPeripheralsWithServices(null, null)
        } else {
            println("Bluetooth is not powered on")
        }
    }

    override fun stopScan() {
        centralManager.stopScan()
    }


    override suspend fun connect(deviceId: String): BleResult<Unit> =
        suspendCancellableCoroutine { continuation ->
            val peripheral = discoveredPeripherals[deviceId]
            if (peripheral == null) {
                continuation.resume(BleResult.Error("Device not found"))
                return@suspendCancellableCoroutine
            }
            connectContinuation = {
                continuation.resume(it)
            }
            activePeripheral = peripheral
            peripheral.delegate = this
            centralManager.connectPeripheral(peripheral, null)
        }

    override suspend fun writeCharacteristic(
        serviceUuid: String,
        characteristicUuid: String,
        data: ByteArray
    ): BleResult<Unit> = suspendCancellableCoroutine { continuation ->
        val characteristic = findCharacteristic(serviceUuid, characteristicUuid)
        if (characteristic == null) {
            continuation.resume(BleResult.Error("Characteristic not found"))
            return@suspendCancellableCoroutine
        }
        writeContinuation = {
            continuation.resume(it)
        }
        activePeripheral?.writeValue(
            data.toNSData(),
            characteristic,
            CBCharacteristicWriteWithResponse
        )
        continuation.invokeOnCancellation {
            writeContinuation = null
        }
    }

    override suspend fun readCharacteristic(
        serviceUuid: String,
        characteristicUuid: String
    ): BleResult<ByteArray> = suspendCancellableCoroutine { continuation ->
        val characteristic = findCharacteristic(serviceUuid, characteristicUuid)
        if (characteristic == null) {
            continuation.resume(BleResult.Error("Characteristic not found"))
            return@suspendCancellableCoroutine
        }

        readContinuation = { continuation.resume(it) }

        activePeripheral?.readValueForCharacteristic(characteristic)

        continuation.invokeOnCancellation { readContinuation = null }
    }

    override fun disconnect() {
        activePeripheral?.let {
            centralManager.cancelPeripheralConnection(it)
        }
    }

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        when (central.state) {
            CBCentralManagerStatePoweredOn -> println("BLE On")
            else -> println("BLE Unavailable")
        }
    }

    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>,
        RSSI: NSNumber
    ) {
        val id = didDiscoverPeripheral.identifier.UUIDString
        discoveredPeripherals[id] = didDiscoverPeripheral

        val device = BleDevice(
            id = id,
            name = didDiscoverPeripheral.name ?: "Unknown Device",
            rssi = RSSI.intValue
        )
        val currentList = scanResults.value.toMutableList()
        if (currentList.none { it.id == id }) {
            scanResults.value = currentList + device
        }
    }

    override fun centralManager(central: CBCentralManager, didConnectPeripheral: CBPeripheral) {
        activePeripheral?.discoverServices(null)
    }

    override fun peripheral(peripheral: CBPeripheral, didDiscoverServices: NSError?) {
        if (didDiscoverServices != null) {
            connectContinuation?.invoke(BleResult.Error("Discover services failed"))
            return
        }
        peripheral.services?.forEach { service ->
            val cbService = service as CBService
            peripheral.discoverCharacteristics(null, cbService)
        }
    }

    private fun findCharacteristic(
        serviceUuid: String,
        characteristicUuid: String
    ): CBCharacteristic? {
        val peripheral = activePeripheral ?: return null
        val services = peripheral.services ?: return null
        val service = services.filterIsInstance<CBService>().find {
            it.UUID.UUIDString.equals(serviceUuid, ignoreCase = true)
        }
        return service?.characteristics?.filterIsInstance<CBCharacteristic>()?.find {
            it.UUID.UUIDString.equals(characteristicUuid, ignoreCase = true)
        }
    }
}