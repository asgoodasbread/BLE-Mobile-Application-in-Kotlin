package com.example.bluetooth_le_appv2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import java.io.Serializable



class LeDeviceListAdapter(private val context: Context): Serializable {

    private var devices = mutableStateListOf<BluetoothDevice>()
    private var uniqueMacAddresses: HashSet<String> = HashSet()
    private var selectedDeviceIndex: Int = -1

    fun addDevice(device: BluetoothDevice) {
        if(uniqueMacAddresses.add(device.address)){
            devices.add(device)
        }
    }

    fun getSize():Int{
        return devices.size
    }

    fun clearList(){
        devices.clear()
        uniqueMacAddresses.clear()
    }

    @SuppressLint("MissingPermission")
    fun sortDevices(){
        var index: Int = 0
        var moved = 0
        val length: Int = devices.size

        for (i in 0 until length){
            val device = devices[i]
            if(device.name != null){
                moved++
                devices.removeAt(index)
                devices.add(0,device)
            }
            index++
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun DeviceList() {
        LazyColumn {
            items(devices) { device ->
                DeviceItem(device = device)
            }
        }
    }


    @SuppressLint("MissingPermission")
    @Composable
    private fun DeviceItem(device: BluetoothDevice) {
        // Compose UI for each device item
        var expanded by rememberSaveable{ mutableStateOf(false)}

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    expanded = true
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Text(
                text = device.name ?: "Unknown Device",
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {

                Button(onClick = {
                    selectedDeviceIndex = devices.indexOf(device)

                    if (selectedDeviceIndex != -1) {
                        //Log.d("Devices", "Selected device at index: $selectedDeviceIndex")
                        val intent = Intent(context, DeviceControlActivity::class.java)
                        //navigate.putExtra("DeviceIndex", selectedDeviceIndex)
                        DataStorage.put("SelectedDevice",device)
                        DataStorage.put("LeDevice",this@LeDeviceListAdapter)

                        startActivity(context, intent, null)
                    }
                    else{
                        Log.e("Devices", "Selected device not found in the list")
                    }
                }) {
                    Text("Connect")
                }
                Button(onClick = {
                    val intent = Intent(context,InfoActivity::class.java)
                    if(device.address == null){
                        DataStorage.put("MacAddress","Null")
                    }
                    else {
                        DataStorage.put("MacAddress", device.address)
                    }
                    startActivity(context,intent,null)
                }) {

                    Text("Info")
                }

            }
        }

    }
}