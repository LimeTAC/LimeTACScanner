package com.limetac.scanner.ui.view.tagEntity.helperTag

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.ui.view.tagEntity.TagScanningViewModel
import com.limetac.scanner.utils.Constants
import com.limetac.scanner.utils.DialogUtil
import com.limetac.scanner.utils.Status
import com.limetac.scanner.utils.ToastUtil
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_antenna_tag.*
import kotlinx.android.synthetic.main.activity_bin.*
import kotlinx.android.synthetic.main.activity_helper_tag.*
import kotlinx.android.synthetic.main.activity_tag_scan.*
import kotlinx.android.synthetic.main.activity_tag_scan.toolbar

class HelperTagActivity : AppCompatActivity(), IAsynchronousMessage {

    lateinit var binResponse: BinResponse
    var receivedScannedTag = ""
    var isScanned = false
    var scannedTag = ""
    var upDataTime = 0
    lateinit var viewModel: TagScanningViewModel
    var adapter: HelperTagAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_tag)
        init()
        initViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(TagScanningViewModel::class.java)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initViews() {
        setupViewModel()
        observeTagDetails()
        setSupportActionBar(activityHelperTag_toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        activityHelperTag_scanList.layoutManager =
            LinearLayoutManager(this)
        intent?.extras?.let {
            binResponse =
                intent.getSerializableExtra(Constants.TagScanning.BIN_RESPONSE_KEY) as BinResponse
            receivedScannedTag =
                intent.getStringExtra(Constants.TagScanning.SCANNED_TAG_KEY) as String
            activityHelperTag_title.text = binResponse.code
            activityHelperTag_count.text = binResponse.tagDetails?.size.toString()
            adapter = HelperTagAdapter(
                this,
                receivedScannedTag,
                binResponse.tagDetails as ArrayList<BinTag>
            )
            activityHelperTag_scanList.adapter = adapter
        }

        activityHelperTag_scanTagBtn.setOnClickListener {
            finish()
        }
    }


    private fun observeTagDetails() {
        viewModel.getTagDetails().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    isScanned = false
                    activityHelperTag_progress.hide()
                    it.data?.let { binResponse ->
                        if (binResponse.size > 1) {
                            ToastUtil.createLongToast(
                                this,
                                getString(R.string.tag_associated_with_other_entity_msg)
                            )
                        } else {
                            if (binResponse[0].isBin) {
                                ToastUtil.createLongToast(
                                    this,
                                    getString(R.string.tag_associated_with_other_entity_msg)
                                )
                            } else {
                                if (binResponse.isNotEmpty()) {
                                    when (binResponse[0].type) {
                                        Constants.Entity.PACKAGE -> {
                                            ToastUtil.createLongToast(
                                                this,
                                                getString(R.string.tag_associated_with_other_entity_msg)
                                            )
                                        }
                                        Constants.Entity.FORKLIFT -> {
                                            ToastUtil.createLongToast(
                                                this,
                                                getString(R.string.tag_associated_with_other_entity_msg)
                                            )
                                        }
                                        Constants.Entity.LOCATION -> {
                                            val codeExist = doesCodeExist()
                                            if (codeExist) {
                                                this@HelperTagActivity.binResponse.tagDetails?.let { tagList ->
                                                    adapter = HelperTagAdapter(
                                                        this,
                                                        scannedTag,
                                                        tagList as ArrayList<BinTag>
                                                    )
                                                    activityHelperTag_scanList.adapter = adapter
                                                }
                                            } else {
                                                ToastUtil.createLongToast(
                                                    this,
                                                    "This Tag is associated with some other Location. Please press scan next tag!"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    activityHelperTag_progress.show()
                }
                Status.ERROR -> {
                    isScanned = false
                    activityHelperTag_progress.hide()
                    it.message?.let { message ->
                        ToastUtil.createShortToast(this, message)
                    }
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun doesCodeExist(): Boolean {
        binResponse.tagDetails?.let { tagList ->
            for (tag in tagList) {
                if (tag.tagCode == scannedTag) {
                    return true
                }
            }
        }
        return false
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun refreshViews() {
        resources?.getDrawable(R.drawable.cd_bg)?.let {
            activityAntennaTag_rowOneLeft.background = it
            activityAntennaTag_rowOneRight.background = it
            activityAntennaTag_rowTwoRight.background = it
            activityAntennaTag_rowTwoLeft.background = it
            activityAntennaTag_rowThreeLeft.background = it
            activityAntennaTag_rowThreeRight.background = it
        }
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
        RFIDReader.HP_CONNECT[UHFBaseActivity.ConnID]?.myLog = this
    }

    private fun UHF_SetTagUpdateParam() {
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
                    val param = "1,$upDataTime"
                    RFIDReader._Config.SetTagUpdateParam(
                        UHFBaseActivity.ConnID,
                        upDataTime,
                        rssiFilter
                    ) // Set repeat tag upload time to 20ms
                    Log.d("Debug", "Sets the label upload time...")
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

    override fun onDestroy() {
        super.onDestroy()
        RFIDReader._Config.Stop(UHFBaseActivity.ConnID)
    }
}