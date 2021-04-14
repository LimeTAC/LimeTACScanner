package com.limetac.scanner.ui.view.tagEntity.antennaTag

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.ui.view.tagEntity.TagScanningViewModel
import com.limetac.scanner.utils.Constants
import com.limetac.scanner.utils.DialogUtil.showTagDialog
import com.limetac.scanner.utils.Status
import com.limetac.scanner.utils.ToastUtil
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_antenna_tag.*

class AntennaTagActivity : AppCompatActivity(), IAsynchronousMessage {

    lateinit var binResponse: BinResponse
    var receivedScannedTag = ""
    var scannedTag = ""
    var isScanned = false
    var upDataTime = 0
    lateinit var viewModel: TagScanningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antenna_tag)
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initViews() {
        setupViewModel()
        observeTagDetails()
        setSupportActionBar(activityAntennaTag_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activityAntennaTag_scanTagBtn.setOnClickListener {
            finish()
        }
        intent?.extras?.let {
            binResponse =
                intent.getSerializableExtra(Constants.TagScanning.BIN_RESPONSE_KEY) as BinResponse
            receivedScannedTag =
                intent.getStringExtra(Constants.TagScanning.SCANNED_TAG_KEY) as String
            activityAntennaTag_title.text = binResponse.code
            binResponse.tagDetails?.let { tagDetails ->
                for (i in tagDetails.indices) {
                    val tag = tagDetails[i]
                    when (tag.tagIndex) {
                        7 -> {
                            if (tag.tagCode == receivedScannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowOneLeft.background = it
                                }
                            }
                            activityAntennaTagRowOneLeft_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTag_rowOneLeft.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        8 -> {
                            if (tag.tagCode == receivedScannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowOneRight.background = it
                                }
                            }
                            activityAntennaTagRowOneRight_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowOneRight_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        6 -> {
                            if (tag.tagCode == receivedScannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoRight.background = it
                                }
                            }
                            activityAntennaTagRowTwoRight_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowTwoRight_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        3 -> {
                            if (tag.tagCode == receivedScannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoLeft.background = it
                                }
                            }
                            activityAntennaTagRowTwoLeft_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowTwoLeft_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }
                        2 -> {
                            if (tag.tagCode == receivedScannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoRight.background = it
                                }
                            }
                            activityAntennaTagRowThreeRight_txt.text =
                                tag.tagCode?.takeLast(3)
                            activityAntennaTagRowThreeRight_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        1 -> {
                            if (tag.tagCode == receivedScannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoRight.background = it
                                }
                            }
                            activityAntennaTagRowThreeLeft_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowThreeLeft_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeTagDetails() {
        viewModel.getTagDetails().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    isScanned = false
                    activityAntennaTag_Progress.hide()
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
                                            if (binResponse[0].code == activityAntennaTag_title.text) {
                                                findAndHighlightCode()
                                            } else {
                                                ToastUtil.createLongToast(
                                                    this,
                                                    "This Tag is associated with some other Antenna. Please press scan next tag!"
                                                )
                                            }
                                        }
                                        Constants.Entity.LOCATION -> {
                                            ToastUtil.createLongToast(
                                                this,
                                                getString(R.string.tag_associated_with_other_entity_msg)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    activityAntennaTag_Progress.show()
                }
                Status.ERROR -> {
                    isScanned = false
                    activityAntennaTag_Progress.hide()
                    it.message?.let { message ->
                        ToastUtil.createShortToast(this, message)
                    }
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun findAndHighlightCode() {
        refreshViews()
        when {
            scannedTag.takeLast(3) == activityAntennaTagRowOneLeft_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityAntennaTag_rowOneLeft.background = it
                }
                activityAntennaTag_rowOneLeft.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityAntennaTagRowOneRight_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityAntennaTag_rowOneRight.background = it
                }
                activityAntennaTag_rowOneRight.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityAntennaTagRowTwoRight_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityAntennaTag_rowTwoRight.background = it
                }
                activityAntennaTag_rowTwoRight.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityAntennaTagRowTwoLeft_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityAntennaTag_rowTwoLeft.background = it
                }
                activityAntennaTag_rowTwoLeft.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityAntennaTagRowThreeRight_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityAntennaTag_rowThreeRight.background = it
                }
                activityAntennaTag_rowThreeRight.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityAntennaTagRowThreeLeft_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityAntennaTag_rowThreeLeft.background = it
                }
                activityAntennaTag_rowThreeLeft.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
        }
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
        RFIDReader.HP_CONNECT.get(UHFBaseActivity.ConnID)?.myLog = this
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



}