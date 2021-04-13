package com.limetac.scanner.ui.view.scanHelper

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.data.model.EntityType
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.utils.ScreenUtils
import com.limetac.scanner.utils.Status
import com.limetac.scanner.utils.ToastUtil
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_bin.*
import kotlinx.android.synthetic.main.activity_package_scanning.*
import kotlinx.android.synthetic.main.activity_package_scanning.toolbar
import kotlinx.android.synthetic.main.activity_scan_helper.*

class ScanHelperActivity : AppCompatActivity(), IAsynchronousMessage {

    var upDataTime = 0
    var tagList = ArrayList<BinTag>()
    private lateinit var lastScanTagId: String
    private lateinit var viewModel: ScanHelperViewModel
    private lateinit var adapter: ScanHelperAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_helper)
        setSupportActionBar(activityScanHelper_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()
        setupListeners()
        observeCreateLocation()
        observeEntityDetail()
        adapter = ScanHelperAdapter(this, ArrayList())
        activityScanHelper_scanList.layoutManager = LinearLayoutManager(this)
        activityScanHelper_scanList.adapter = adapter
    }

    private fun setupListeners() {
        activityScanHelper_txtTagCode.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.getEntityDetail(activityScanHelper_txtTagCode.text.toString())
                ScreenUtils.hideKeyboard(this)
                return@OnEditorActionListener true
            }
            false
        })

        activityScanHelper_btnSubmit.setOnClickListener {
            if (activityScanHelper_txtTagCode.text != null && activityScanHelper_txtTagCode.text.toString()
                    .isNotEmpty()
            ) {
                if (tagList.isNotEmpty())
                    viewModel.createScanHelper(
                        activityScanHelper_txtTagCode.text.toString(),
                        tagList
                    )
                else
                    ToastUtil.createShortToast(this, "Please scan some tags")
            } else {
                ToastUtil.createShortToast(this, "Please enter a Helper code")
            }
        }

        activityScanHelper_btnCancel.setOnClickListener {
            if (tagList.isNotEmpty()) {
                confirmDeleteDialog()
                activityScanHelper_count.text = ""
            }
        }
    }

    private fun observeCreateLocation() {
        viewModel.getScanHelperLiveData().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    activityScanHelper_progress.hide()
                    ToastUtil.createShortToast(this, "Location Helper created successfully!")
                }
                Status.LOADING -> {
                    activityScanHelper_progress.show()
                }
                Status.ERROR -> {
                    activityScanHelper_progress.hide()
                    ToastUtil.createShortToast(this, it.message)
                }
            }
        })
    }

    private fun observeEntityDetail() {
        viewModel.getEntityDetails().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    activityScanHelper_progress.hide()
                    tagList.clear()
                    adapter.removeAllItems()
                    it.data?.tagDetails?.let { it1 -> tagList.addAll(it1) }
                    adapter = ScanHelperAdapter(this, tagList)
                    activityScanHelper_scanList.adapter = adapter
                    activityScanHelper_count.text = (tagList.size).toString()
                }
                Status.LOADING -> {
                    activityScanHelper_progress.show()
                }
                Status.ERROR -> {
                    activityScanHelper_progress.hide()
                  //  ToastUtil.createShortToast(this, it.message)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(ScanHelperViewModel::class.java)
    }

    private fun confirmDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Warning!")
            .setMessage("Are you sure you want to remove all tags?")
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, _ ->
                adapter.removeAllItems()
                dialog.dismiss()
            }.setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    //initialization
    fun init() {
        try {
            Thread.sleep(20)
            try {
                RFIDReader._Config.Stop(UHFBaseActivity.ConnID)
            } catch (e: Exception) {
                // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
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
                    synchronized(tagList) {
                        val tagId = currenttagModel?._EPC + currenttagModel?._TID
                        val selectedIndex: Int = getTagSelectedIndex(tagId)
                        if (selectedIndex == -1) {
                            if (tagList.size < 50)
                                addTag(tagId)
                            else
                                ToastUtil.createShortToast(
                                    this@ScanHelperActivity,
                                    "Maximum Tag Scan limit reached !"
                                )
                        }
                    }
                }
            })
        } catch (ex: java.lang.Exception) {
            Log.d("Debug", "Tags output exceptions:" + ex.message)
        }
    }

    private fun addTag(tagId: String) {
        val binTag = BinTag()
        binTag.tagCode = tagId
        binTag.tagType = EntityType.HELPER.type
        tagList.add(0, binTag)
        lastScanTagId = tagId
        adapter.updateTagList(tagList)
        activityScanHelper_count.text = (tagList.size).toString()
    }

    private fun getTagSelectedIndex(tagId: String): Int {
        tagList.forEachIndexed { index, element ->
            if (element.tagCode == tagId)
                return index
        }
        return -1
    }

    override fun OutPutTagsOver() {

    }

    override fun GPIControlMsg(p0: Int, p1: Int, p2: Int) {
        var s = p0
    }

    override fun OutPutScanData(p0: ByteArray?) {
        try {
            val packageCode = p0?.let { String(it).substring(4) }
            val runnable = Runnable {

            }
            Thread(runnable).start()
        } catch (ex: java.lang.Exception) {
            Log.d("Debug", "Tags output exceptions:" + ex.message)
        }
    }

    override fun WriteDebugMsg(p0: String?) {
        var s = p0
    }

    override fun PortConnecting(p0: String?) {

    }

    override fun PortClosing(p0: String?) {

    }
}