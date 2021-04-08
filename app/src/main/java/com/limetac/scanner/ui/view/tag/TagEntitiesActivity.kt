package com.limetac.scanner.ui.view.tag

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.reader.UHFBaseActivity
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.utils.ScreenUtils
import com.limetac.scanner.utils.Status
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_bin.*
import kotlinx.android.synthetic.main.activity_bin.btnCancel
import kotlinx.android.synthetic.main.activity_bin.progress
import kotlinx.android.synthetic.main.activity_tag_entities.*

class TagEntitiesActivity : AppCompatActivity(), IAsynchronousMessage {


    private lateinit var viewModel: TagEntityViewModel
    var tag = ArrayList<BinTag>()
    lateinit var adapter: TagEntityAdapter
    var upDataTime = 0
    var view: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_entities)

        setUI()
        setupViewModel()
        setObserver()
    }

    private fun setObserver() {
        viewModel.getDetails().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    it.data?.let { details ->
                        header.visibility = View.VISIBLE
                        details.tagDetails?.let {
                            tag = details.tagDetails as ArrayList<BinTag>
                            details.type?.let { details ->
                                adapter = TagEntityAdapter(tag, details)
                                list.layoutManager = LinearLayoutManager(this);
                                list.adapter = adapter
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    progress.hide()
                    it.message?.let {
                        Toast.makeText(TagEntitiesActivity@ this, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        btnCancel.setOnClickListener { clearScreen() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUI() {
        setSupportActionBar(toolbar2);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        btnCancel.width = (ScreenUtils.getScreenWidth(this) * 0.38).toInt()
        //    viewModel.fetchTagsByPkg("C72486")
        txtTagCode2.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                /*   viewModel.fetchTagsByTags(txtTagCode.text.toString())
                   ScreenUtils.hideKeyboard(this)*/

                viewModel.getTagEntity(txtTagCode2.text.toString())

                ScreenUtils.hideKeyboard(this)

                return@OnEditorActionListener true
            }
            false
        })

    }

    private fun clearScreen() {
        header.visibility = View.GONE
        tag = ArrayList()
        adapter = TagEntityAdapter(tag, "")
        adapter.notifyDataSetChanged()
        list.adapter = adapter
        txtTagCode2.setText("")
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(TagEntityViewModel::class.java)
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
                    synchronized(tag) {
                        val tagId = currenttagModel?._EPC + currenttagModel?._TID
                        viewModel.getTagEntity(tagId)
                    }
                }
            })
        } catch (ex: java.lang.Exception) {
            Log.d("Debug", "Tags output exceptions:" + ex.message)
        }
    }

    private fun addTag(tagId: String) {

    }

    private fun getTagSelectedIndex(tagId: String): Int {
        tag.forEachIndexed { index, element ->
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
            synchronized(tag) {
                val packageCode = p0?.let { String(it).substring(4) }
                packageCode?.let { txtCode.setText(it) }
                runOnUiThread {
                    if (txtCode.text.toString() !== packageCode) {
                        // clearScreen()
                        // packageCode?.let { viewModel.fetchTagsByPkg(it)
                        //  txtPackageCode.setText(it)}
                    }
                }
            }
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