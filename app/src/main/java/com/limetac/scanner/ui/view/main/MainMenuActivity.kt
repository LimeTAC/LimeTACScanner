package com.limetac.scanner.ui.view.main

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.limetac.scanner.R
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.ui.view.scanAntenna.AntennaActivity
import com.limetac.scanner.ui.view.scanBin.BinActivity
import com.limetac.scanner.ui.view.scanPackage.PackageScanningActivity
import com.limetac.scanner.ui.view.scanHelper.ScanHelperActivity
import com.limetac.scanner.ui.view.settings.SettingsActivity
import com.limetac.scanner.ui.view.tag.TagScanActivity
import com.limetac.scanner.ui.view.tagEntity.TagScanningActivity
import com.limetac.scanner.utils.*
import com.limetac.scanner.utils.BluetoothUtil.isBluetoothEnabled
import com.limetac.scanner.utils.IntentUtil.openMainModule
import com.limetac.scanner.utils.ToastUtil.createShortToast
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import java.util.*

class MainMenuActivity : UHFBaseActivity() {
    var layoutPkgScan: CardView? = null
    var layoutBin: CardView? = null
    var layoutAntenna: CardView? = null
    var layoutHelper: CardView? = null
    var layout: CardView? = null
    var layoutBluetooth: CardView? = null
    var layoutTagEntity: CardView? = null
    var layoutSettings: CardView? = null
    var scannerName = ""
    var newScannerName = ""
    var preference: Preference? = null
    private var CURRENT_BLUETOOTH_NAME = "CURRENT_BLUETOOTH_NAME"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preference = Preference(this)
        val bluetoothName = preference?.getStringPrefrence(CURRENT_BLUETOOTH_NAME, "")
        if (!bluetoothName.isNullOrEmpty()) {
            val names = RFIDReader.GetBT4DeviceStrList()
            connectToDevice(bluetoothName)
        } else {
            if (isBluetoothEnabled) showDialog() else createShortToast(
                this,
                getString(R.string.enableBluetoothMessage)
            )
        }
        initializeUI()
        setListeners()
    }

    private fun showDialog() {
        val builderSingle = AlertDialog.Builder(this)
        builderSingle.setTitle("Select Bluetooth:-")
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice)
        arrayAdapter.addAll(bluetoothDeviceNames)
        builderSingle.setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            var strName = arrayAdapter.getItem(which)
            if (strName == newScannerName) strName = scannerName
            connectToDevice(strName)
        }
        builderSingle.show()
    }

    private val bluetoothDeviceNames: List<String>
        get() {
            val names = RFIDReader.GetBT4DeviceStrList()
            val bluetoothName: MutableList<String> = ArrayList()
            bluetoothName.addAll(names)
            for (name in bluetoothName) {
                if (name.startsWith("BTR-")) {
                    newScannerName = name.replace("BTR-", "LimeTAC-")
                    scannerName = name
                }
            }
            if (scannerName.isNotEmpty() && newScannerName.isNotEmpty()) {
                bluetoothName.remove(scannerName)
                bluetoothName.add(newScannerName)
            }
            return bluetoothName
        }

    private fun connectToDevice(bluetoothName: String?) {
        if (Rfid_BT4_Init(bluetoothName, object : IAsynchronousMessage {
                override fun WriteDebugMsg(s: String) {
//                    Toast.makeText(MainMenuActivity.this,"Debug:"+s,Toast.LENGTH_LONG).show();
                }

                override fun WriteLog(s: String) {
                    //  Toast.makeText(MainMenuActivity.this,"lOG:"+s,Toast.LENGTH_LONG).show();
                }

                override fun PortConnecting(s: String) {
                    //   Toast.makeText(MainMenuActivity.this,"cONNETED:"+s,Toast.LENGTH_LONG).show();
                }

                override fun PortClosing(s: String) {}
                override fun OutPutTags(tag_model: Tag_Model) {}
                override fun OutPutTagsOver() {}
                override fun GPIControlMsg(i: Int, i1: Int, i2: Int) {}
                override fun OutPutScanData(bytes: ByteArray) {
                    //   Toast.makeText(MainMenuActivity.this,"sCANNER:",Toast.LENGTH_LONG).show();
                }
            })) {
            preference?.saveStringInPrefrence(CURRENT_BLUETOOTH_NAME, bluetoothName)
            Toast.makeText(this@MainMenuActivity, "Connected", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                this@MainMenuActivity,
                "You are not connected with scanner device",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        UHF_Dispose()
        return true
    }

    /**
     * release uhf rfid module
     */
    override fun UHF_Dispose() {
        if (_UHFSTATE) {
            RFIDReader.CloseConn(ConnID)
            _UHFSTATE = false
        }
    }

    private fun setListeners() {
        layoutPkgScan?.setOnClickListener {
            openMainModule(
                this@MainMenuActivity,
                PackageScanningActivity::class.java
            )
        }
        layoutTagEntity?.setOnClickListener {
            openMainModule(
                this@MainMenuActivity,
                TagScanningActivity::class.java
            )
        }
        layoutAntenna?.setOnClickListener {
            openMainModule(
                this@MainMenuActivity,
                AntennaActivity::class.java
            )
        }
        layoutBin?.setOnClickListener {
            openMainModule(
                this@MainMenuActivity,
                BinActivity::class.java
            )
        }
        layout?.setOnClickListener {
            openMainModule(
                this@MainMenuActivity,
                TagScanActivity::class.java
            )
        }
        layoutSettings?.setOnClickListener {
            openMainModule(
                this@MainMenuActivity,
                SettingsActivity::class.java
            )
        }
        layoutHelper?.setOnClickListener {
            openMainModule(
                this@MainMenuActivity,
                ScanHelperActivity::class.java
            )
        }
        layoutBluetooth?.setOnClickListener {
            if (isBluetoothEnabled) showDialog() else createShortToast(
                this@MainMenuActivity, getString(
                    R.string.enableBluetoothMessage
                )
            )
        }
    }

    private fun initializeUI() {
        layoutPkgScan = findViewById(R.id.layoutPkgScan)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        layoutBin = findViewById(R.id.layout)
        layoutHelper = findViewById(R.id.layoutPkgHelper)
        layoutAntenna = findViewById(R.id.layoutPkgAntenna)
        layout = findViewById(R.id.layoutTagScan)
        layoutBluetooth = findViewById(R.id.layoutBluetooth)
        layoutTagEntity = findViewById(R.id.layoutTagEntity)
        layoutSettings = findViewById(R.id.layoutSettings)
        setSupportActionBar(toolbar)
        setLayoutDimension(layoutAntenna)
        setLayoutDimension(layoutPkgScan)
        setLayoutDimension(layoutHelper)
        setLayoutDimension(layoutBin)
        setLayoutDimension(layoutBluetooth)
        setLayoutDimension(layout)
        setLayoutDimension(layoutTagEntity)
        setLayoutDimension(layoutSettings)


        // add back arrow to toolbar
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    private fun setLayoutDimension(view: CardView?) {
        view?.layoutParams?.width = (ScreenUtils.getScreenWidth(this) * 0.39).toInt()
        view?.layoutParams?.height = (ScreenUtils.getScreenWidth(this) * 0.3).toInt()
    }
}