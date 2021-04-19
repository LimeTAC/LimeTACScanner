package com.limetac.scanner.ui.view.tagEntity.packageTag

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.ui.view.tagEntity.TagScanningViewModel
import com.limetac.scanner.utils.Constants
import com.limetac.scanner.utils.Constants.TagScanning.BIN_RESPONSE_KEY
import com.limetac.scanner.utils.Constants.TagScanning.SCANNED_TAG_KEY
import com.limetac.scanner.utils.DialogUtil
import com.limetac.scanner.utils.DialogUtil.showTagDialog
import com.limetac.scanner.utils.Status
import com.limetac.scanner.utils.ToastUtil
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_antenna_tag.*
import kotlinx.android.synthetic.main.activity_bin.*
import kotlinx.android.synthetic.main.activity_package_tag.*
import kotlinx.android.synthetic.main.activity_tag_scan.*
import kotlinx.android.synthetic.main.activity_tag_scan.toolbar


class PackageTagActivity : AppCompatActivity(), IAsynchronousMessage {

    lateinit var binResponse: BinResponse
    lateinit var adapter: PackageTagAdapter
    var receivedScannedTag = ""
    var isScanned = false
    var scannedTag = ""
    var upDataTime = 0
    lateinit var viewModel: TagScanningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_tag)
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
        setSupportActionBar(activityPackageTag_toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        activityPackageTag_scanTagBtn.setOnClickListener {
            finish()
        }
        intent?.extras?.let {
            binResponse = intent.getSerializableExtra(BIN_RESPONSE_KEY) as BinResponse
            receivedScannedTag = intent.getStringExtra(SCANNED_TAG_KEY) as String
            activityPackageTag_title.text = binResponse.code
            binResponse.tagDetails?.let { tagDetails ->
                for (i in tagDetails.indices) {
                    if (i == 0) {
                        if (tagDetails[i].tagCode == receivedScannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityPackageTag_rowOneLeft.background = it
                            }
                        }
                        activityPackageTagRowOneLeft_txt.text = tagDetails[i].tagCode?.takeLast(3)
                        activityPackageTag_rowOneLeft.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 1) {
                        if (tagDetails[i].tagCode == receivedScannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityPackageTag_rowOneRight.background = it
                            }
                        }
                        activityPackageTagRowOneRight_txt.text = tagDetails[i].tagCode?.takeLast(3)
                        activityPackageTag_rowOneRight.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 2) {
                        if (tagDetails[i].tagCode == receivedScannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityPackageTag_rowTwoLeft.background = it
                            }
                        }
                        activityPackageTagRowTwoLeft_txt.text = tagDetails[i].tagCode?.takeLast(3)
                        activityPackageTag_rowTwoLeft.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 3) {
                        if (tagDetails[i].tagCode == receivedScannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityPackageTag_rowTwoRight.background = it
                            }
                        }
                        activityPackageTagRowTwoRight_txt.text = tagDetails[i].tagCode?.takeLast(3)
                        activityPackageTag_rowTwoRight.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
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
                    activityPackageTag_Progress.hide()
                    it.data?.let { binResponse ->
                        if (binResponse.size > 1) {
                            ToastUtil.createLongToast(
                                this,
                                getString(R.string.tag_associated_with_other_entity_msg)
                            )
                        } else {
                            val response = binResponse[0]
                            if (binResponse.isNotEmpty()) {
                                when (response.type) {
                                    Constants.Entity.PACKAGE -> {
                                        response.tagDetails?.let { tags ->
                                            if (tags.size <= 4) {
                                                if (response.code == activityPackageTag_title.text) {
                                                    findAndHighlightCode()
                                                } else {
                                                    ToastUtil.createLongToast(
                                                        this,
                                                        "This Tag is associated with some other Package. Please press scan next tag!"
                                                    )
                                                }
                                            } else
                                                DialogUtil.showOKDialog(
                                                    this,
                                                    "Alert",
                                                    "Something went wrong. Please notify LimeTAC with package id"
                                                )
                                        }
                                    }
                                    Constants.Entity.FORKLIFT -> {
                                        ToastUtil.createLongToast(
                                            this,
                                            getString(R.string.tag_associated_with_other_entity_msg)
                                        )

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
                Status.LOADING -> {
                    activityPackageTag_Progress.show()
                }
                Status.ERROR -> {
                    isScanned = false
                    activityPackageTag_Progress.hide()
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
            scannedTag.takeLast(3) == activityPackageTagRowOneLeft_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityPackageTag_rowOneLeft.background = it
                }
                activityPackageTag_rowOneLeft.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityPackageTagRowOneRight_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityPackageTag_rowOneRight.background = it
                }
                activityPackageTag_rowOneRight.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityPackageTagRowTwoRight_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityPackageTag_rowTwoRight.background = it
                }
                activityPackageTag_rowTwoRight.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
            scannedTag.takeLast(3) == activityPackageTagRowTwoLeft_txt.text -> {
                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    activityPackageTag_rowTwoLeft.background = it
                }
                activityPackageTag_rowTwoLeft.setOnClickListener {
                    showTagDialog(this, scannedTag)
                }
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun refreshViews() {
        resources?.getDrawable(R.drawable.cd_bg)?.let {
            activityPackageTag_rowOneLeft.background = it
            activityPackageTag_rowOneRight.background = it
            activityPackageTag_rowTwoLeft.background = it
            activityPackageTag_rowTwoRight.background = it
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