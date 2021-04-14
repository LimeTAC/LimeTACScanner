package com.limetac.scanner.ui.view.tagEntity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.ui.view.tagEntity.antennaTag.AntennaTagActivity
import com.limetac.scanner.ui.view.tagEntity.binTag.BinTagActivity
import com.limetac.scanner.ui.view.tagEntity.helperTag.HelperTagActivity
import com.limetac.scanner.ui.view.tagEntity.multiEntity.MultiEntityActivity
import com.limetac.scanner.ui.view.tagEntity.packageTag.PackageTagActivity
import com.limetac.scanner.utils.Constants
import com.limetac.scanner.utils.Constants.Entity.FORKLIFT
import com.limetac.scanner.utils.Constants.Entity.LOCATION
import com.limetac.scanner.utils.Constants.Entity.PACKAGE
import com.limetac.scanner.utils.Status
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_tag_scan.*


class TagScanningActivity : AppCompatActivity(), IAsynchronousMessage {

    var upDataTime = 0
    var tagDetailList = ArrayList<BinTag>()
    var isScanned = false
    lateinit var viewModel: TagScanningViewModel
    var scannedTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_scan)
        setUI()
        setupViewModel()
        observeTagDetails()
    }

    private fun setUI() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(TagScanningViewModel::class.java)
    }

    private fun observeTagDetails() {
        viewModel.getTagDetails().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    activityTagScanningProgress.hide()
                    it.data?.let { binResponse ->
                        isScanned = false
                        if (binResponse.size > 1) {
                            val intent = Intent(this, MultiEntityActivity::class.java)
                            intent.putExtra("EntityList", Gson().toJson(binResponse))
                            intent.putExtra(Constants.TagScanning.SCANNED_TAG_KEY, scannedTag)
                            startActivity(intent)
                        } else {
                            if (binResponse[0].isBin) {
                                val intent = Intent(this, BinTagActivity::class.java)
                                intent.putExtra(
                                    Constants.TagScanning.BIN_RESPONSE_KEY,
                                    binResponse[0]
                                )
                                intent.putExtra(
                                    Constants.TagScanning.SCANNED_TAG_KEY,
                                    scannedTag
                                )
                                startActivity(intent)
                            } else {
                                if (binResponse.isNotEmpty()) {
                                    when (binResponse[0].type) {
                                        PACKAGE -> {
                                            val intent =
                                                Intent(this, PackageTagActivity::class.java)
                                            intent.putExtra(
                                                Constants.TagScanning.BIN_RESPONSE_KEY,
                                                binResponse[0]
                                            )
                                            intent.putExtra(
                                                Constants.TagScanning.SCANNED_TAG_KEY,
                                                scannedTag
                                            )
                                            startActivity(intent)
                                        }
                                        FORKLIFT -> {
                                            val intent =
                                                Intent(this, AntennaTagActivity::class.java)
                                            intent.putExtra(
                                                Constants.TagScanning.BIN_RESPONSE_KEY,
                                                binResponse[0]
                                            )
                                            intent.putExtra(
                                                Constants.TagScanning.SCANNED_TAG_KEY,
                                                scannedTag
                                            )
                                            startActivity(intent)
                                        }
                                        LOCATION -> {
                                            val intent = Intent(this, HelperTagActivity::class.java)
                                            intent.putExtra(
                                                Constants.TagScanning.BIN_RESPONSE_KEY,
                                                binResponse[0]
                                            )
                                            intent.putExtra(
                                                Constants.TagScanning.SCANNED_TAG_KEY,
                                                scannedTag
                                            )
                                            startActivity(intent)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    activityTagScanningProgress.show()
                }
                Status.ERROR -> {
                    isScanned = false
                    activityTagScanningProgress.hide()
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    //initialization
    fun init() {
        try {
            Thread.sleep(20)
            try {
                RFIDReader._Config.Stop(UHFBaseActivity.ConnID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Thread.sleep(20)
            UHF_SetTagUpdateParam() // Set the upload time of duplicate tag to 20ms
        } catch (ee: Exception) {
            Log.e("Scanner", ee.toString())
        }
        RFIDReader.HP_CONNECT.get(UHFBaseActivity.ConnID)?.myLog = this
    }

    fun UHF_SetTagUpdateParam() {
        // Check if the current settings are the same, if not, then set it.
        var searchRT = ""
        try {
            searchRT = RFIDReader._Config.GetTagUpdateParam(UHFBaseActivity.ConnID)
            val arrRT = searchRT.split("\\|".toRegex()).toTypedArray()
            if (arrRT.size >= 2) {
                val nowUpDataTime = arrRT[0].toInt()
                val rssiFilter = arrRT[1].toInt()
                Log.d("Debug", "Check the label to upload time:$nowUpDataTime")
                if (upDataTime != nowUpDataTime) {
                    val param = "1," + upDataTime
                    RFIDReader._Config.SetTagUpdateParam(
                        UHFBaseActivity.ConnID,
                        upDataTime,
                        rssiFilter
                    ) // Set repeat tag upload time to 20ms
                    Log.d("Debug", "Sets the label upload time...")
                } else {
                }
            } else {
                Log.d("Debug", "Query tags while uploading failure...")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun WriteLog(p0: String?) {
        var s = p0
    }

    override fun OutPutTags(tag_model: Tag_Model?) {
        try {
            runOnUiThread(object : Runnable {
                var currenttagModel: Tag_Model? = tag_model
                override fun run() {
                    if (!isScanned) {
                        val tagId = currenttagModel?._EPC + currenttagModel?._TID
                        viewModel.getTagEntity(tagId)
                        isScanned = true
                        scannedTag = tagId
                    }
                }
            })
        } catch (ex: java.lang.Exception) {
            Log.d("Debug", "Tags output exceptions:" + ex.message)
        }
    }

    override fun OutPutTagsOver() {

    }

    override fun GPIControlMsg(p0: Int, p1: Int, p2: Int) {
        var s = p0
    }

    override fun OutPutScanData(p0: ByteArray?) {

    }

    override fun WriteDebugMsg(p0: String?) {
        var s = p0
    }

    override fun PortConnecting(p0: String?) {

    }

    override fun PortClosing(p0: String?) {

    }
}