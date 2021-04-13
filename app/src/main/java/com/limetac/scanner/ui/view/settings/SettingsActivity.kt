package com.limetac.scanner.ui.view.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.limetac.scanner.R
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.reader.UHFBaseActivity.ConnID
import com.limetac.scanner.utils.DialogUtil
import com.limetac.scanner.utils.Preference
import com.limetac.scanner.utils.ToastUtil
import com.rfidread.RFIDReader
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*


class SettingsActivity : AppCompatActivity() {

    private var adapter: ArrayAdapter<String>? = null
    private var _Max_Power = 30 // Maximum transmitting power of the reader
    private var _Min_Power = 0 // Minimum transmitting power of the reader
    private var _ReaderAntennaCount = 0 // Minimum transmitting power of the reader
    lateinit var powers_G: Array<String>
    private var seekBarProgress = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(activitySettings_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        powers_G = resources.getStringArray(R.array.powerList)
        initListeners()
        if (getScannerPower()) {
            getScannerProperties()
            processPowerValueRange()
        } else {
            ToastUtil.createShortToast(this, "Scanner not working!")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun processPowerValueRange() {
        if (_Max_Power > 0) {
            activitySettings_seekBar.max = _Max_Power.toFloat()
        } else {
            activitySettings_seekBar.max = powers_G[powers_G.size - 1].toFloat()
        }
    }

    private fun setScannerPower(value: Int) {
        val dicPower = HashMap<Int, Int>()
        dicPower[1] = _Min_Power + value
        val rt = RFIDReader._Config.SetANTPowerParam(ConnID, dicPower)
        if (rt == 0) {
            ToastUtil.createShortToast(this, getString(R.string.str_success))
        } else {
            ToastUtil.createShortToast(this, getString(R.string.str_faild))
        }
    }

    private fun getScannerPower(): Boolean {
        var rt = false
        val sPower = RFIDReader._Config.GetANTPowerParam2(ConnID)
        if (sPower == "Parameters error！")
            return rt
        val arrParam = sPower.split("\\&".toRegex()).toTypedArray()
        if (arrParam.isNotEmpty()) {
            for (paramItem in arrParam) {
                val arrItem = paramItem.split(",".toRegex()).toTypedArray()
                if (arrItem.size == 2) {
                    if (arrItem[0] == "1") {
                        activitySettings_seekBar.setProgress((arrItem[1].toInt() - _Min_Power).toFloat())
                    }
                }
            }
            rt = true
        }
        return rt
    }

    private fun initListeners() {
        activitySettings_advanceSettingBtn.setOnClickListener {
            val preference = Preference(this)
            DialogUtil.showEnvironmentChangeDialog(this, preference)
        }

        activitySettings_seekBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                seekBarProgress = seekParams.progress
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                setScannerPower(seekBarProgress)
            }
        }
    }


    /**
     * Get reader property
     */
    @SuppressLint("UseSparseArrays")
    private fun getScannerProperties() {
        val propertyStr = RFIDReader.GetReaderProperty(UHFBaseActivity.ConnID)
        val propertyArr = propertyStr.split("\\|".toRegex()).toTypedArray()
        if (propertyArr.size > 3) {
            try {
                _Min_Power = propertyArr[0].toInt()
                _Max_Power = propertyArr[1].toInt()
                _ReaderAntennaCount = propertyArr[2].toInt()
            } catch (ex: Exception) {
                Log.d("Debug", "Get Reader Property failure and conversion failed!")
            }
        } else {
            Log.d("Debug", "Get Reader Property failure")
        }
    }
}