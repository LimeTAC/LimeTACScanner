package com.limetac.scanner.ui.view.tagEntity.sharedBin

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import kotlinx.android.synthetic.main.activity_package_tag.*
import kotlinx.android.synthetic.main.activity_shared_bin.*


class SharedBinActivity : AppCompatActivity(), IAsynchronousMessage {

    private lateinit var binResponse: List<BinResponse>
    var receivedScannedTag = ""
    var isScanned = false
    var scannedTag = ""
    var upDataTime = 0
    lateinit var viewModel: TagScanningViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_bin)
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
        //   observeTagDetails()
        setSupportActionBar(activitySharedBin_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activitySharedBin_scanTagBtn.setOnClickListener {
            finish()
        }

        intent?.extras?.let {
            val type = object : TypeToken<List<BinResponse?>?>() {}.type
            binResponse = Gson().fromJson(intent.getStringExtra("EntityList"), type)
            val scannedTag = intent.getStringExtra(Constants.TagScanning.SCANNED_TAG_KEY) as String
            activitySharedBin_tagID.text = getString(R.string.tagIDLabel).plus(scannedTag)
            if (binResponse.size >= 2) {
                val firstBinTagDetails: List<BinTag>? = binResponse[0].tagDetails
                val secondBinTagDetails: List<BinTag>? = binResponse[1].tagDetails

                if (!firstBinTagDetails.isNullOrEmpty()) {
                    for (tag in firstBinTagDetails) {
                        if (tag.tagCode == scannedTag) {
                            activitySharedBin_bin1.setText(
                                getBinDetailTextBuilder(
                                    binResponse[0].code.plus(" - "),
                                    tag.tagType
                                ), TextView.BufferType.SPANNABLE
                            )
                        }
                    }
                }

                if (!secondBinTagDetails.isNullOrEmpty()) {
                    for (tag in secondBinTagDetails) {
                        if (tag.tagCode == scannedTag) {
                            activitySharedBin_bin2.setText(
                                getBinDetailTextBuilder(
                                    binResponse[1].code.plus(" - "),
                                    tag.tagType
                                ), TextView.BufferType.SPANNABLE
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getBinDetailTextBuilder(binName: String, tagType: Int): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        builder.append(binName)
        var second = ""
        second = if (tagType == 1)
            "Right Tag"
        else
            "Left Tag"
        val redSpannable = SpannableString(second)
        redSpannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.secondaryFontColor)),
            0,
            second.length,
            0
        )
        return builder.append(redSpannable)
    }


    /* private fun observeTagDetails() {
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
                                                         "This Tag is associated with some other Location. Please press scan next tag!"
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
     }*/


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
                        ToastUtil.createShortToast(
                            this@SharedBinActivity,
                            "Please press scan next tag button below"
                        )
                        isScanned = false
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