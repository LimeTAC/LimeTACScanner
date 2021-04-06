package com.limetac.scanner.utils

import android.hardware.usb.UsbManager
import com.rfidread.usbserial.driver.UsbSerialPort

/**
 *
 * @author RFID_C Public data repository area
 */
object PublicData {
    var _IsCommand6Cor6B =
        "6C" // 6C indicates operation of the 6C tag. 6B indicates operation of the 6B tag.
    var _PingPong_ReadTime = 10000 // default is 100:3
    var _PingPong_StopTime = 300
    @JvmField
    var serialParam = "/dev/ttySAC1:115200" //serial port information
    @JvmField
    var tcpParam = "192.168.1.116:9090" //tcp connection information
    @JvmField
    var usbParam = ""
    @JvmField
    var bt4Param = ""

    //usb devices list
    @JvmField
    var sPortList: List<UsbSerialPort>? = null
    @JvmField
    var usbListStr: List<String>? = null //usb connection parameter list
    @JvmField
    var mUsbManager: UsbManager? = null
}