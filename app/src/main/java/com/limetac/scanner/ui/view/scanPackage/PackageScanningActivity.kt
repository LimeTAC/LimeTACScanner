package com.limetac.scanner.ui.view.scanPackage

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView.BufferType
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.model.Tag
import com.limetac.scanner.reader.UHFBaseActivity.ConnID
import com.limetac.scanner.ui.adapter.TagAdapter
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.utils.DialogUtil
import com.limetac.scanner.utils.ScreenUtils
import com.limetac.scanner.utils.Status
import com.limetac.scanner.utils.ToastUtil
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_package_scanning.*


class PackageScanningActivity : AppCompatActivity(), IAsynchronousMessage {

    private lateinit var viewModel: PackageViewModel
    private var lastScanTagId: String = ""
    var tagList = ArrayList<Tag>()
    lateinit var adapter: TagAdapter
    var upDataTime = 0
    lateinit var handler: Handler
    var packagingItems = ArrayList<String>()
    var selectedItem: String = ""
    var previousScanId: String = ""
    var hasScanned = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_scanning)
        setUI()
        setupViewModel()
        setObserver()
    }

    //initialization
    fun init() {
        try {
            Thread.sleep(20)
            try {
                RFIDReader._Config.Stop(ConnID)
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            Thread.sleep(20)
            UHF_SetTagUpdateParam() // Set the upload time of duplicate tag to 20ms
        } catch (ee: Exception) {
            Log.e("Scanner", ee.toString())
        }
        RFIDReader.HP_CONNECT[ConnID]?.myLog = this
    }

    private fun setObserver() {
        viewModel.getPkgDetails().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    hasScanned = false
                    progress.hide()
                    it.data?.let { details ->
                        details.tags?.let { tags ->
                            if (tags.size <= tagList.size) {
                                tags.forEachIndexed { index, element ->
                                    val tag2 = Tag()
                                    tag2.tag = element
                                    if (index < tagList.size)
                                        tagList[index] = tag2
                                }
                                adapter = TagAdapter(this, tagList, lastScanTagId)
                                gd.adapter = adapter
                            } else
                                DialogUtil.showOKDialog(
                                    this,
                                    "Alert",
                                    "Something went wrong. Please notify LimeTAC with package id"
                                )
                        }
                    }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    hasScanned = false
                    progress.hide()
                    //Handle Error
                    initializeEmptyBoxes()
                }
            }
        })
        viewModel.getPackagingItems().observe(this, Observer { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    it.data?.let { details ->
                        val items = details.map { it.id.toString() }
                        packagingItems.add(0, getString(R.string.select_packaging))
                        packagingItems.addAll(items)
                        val adapter = ArrayAdapter(this, R.layout.spinner_item, packagingItems)
                        spnPkgItem.adapter = adapter
                        spnPkgItem.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>, view: View?, position: Int, id: Long
                            ) {
                                selectedItem = parent.selectedItem as String
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    }
                }

                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    progress.hide()
                }
            }
        })
        viewModel.getTagDetails().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    hasScanned = false
                    progress.hide()
                    it.data?.let { binResponse ->
                        if (!binResponse.isNullOrEmpty()) {
                            removeLatestTag()
                        } else {
                            insertTag()
                        }
                    }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    hasScanned = false
                    progress.hide()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        viewModel.getPkgStatus().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    hasScanned = false
                    progress.hide()
                    it.data?.let { details ->
                        Toast.makeText(
                            this,
                            "Package has been added successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        if (areAllTagsScanned())
                            showDialog()
                    }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    hasScanned = false
                    progress.hide()
                    ToastUtil.createShortToast(this, it.message)
                    //  removeLatestTag()
                }
            }
        })
/*        btnSubmit.setOnClickListener {
            if (txtPackageCode.text != null && !txtPackageCode.text.isNullOrEmpty()) {
                if (selectedItem == getString(R.string.select_packaging))
                    viewModel.submitPkg(txtPackageCode.text.toString(), tagList, "")
                else viewModel.submitPkg(txtPackageCode.text.toString(), tagList, selectedItem)
            }
        }*/
        btnClearScreen.setOnClickListener {
            clearScreen()
        }
    }

    private fun insertTag() {
        if (verifyTagListSize()) {
            if (selectedItem == getString(R.string.select_packaging)) {
                viewModel.submitPkg(txtPackageCode.text.toString(), tagList, "")
            } else {
                viewModel.submitPkg(
                    txtPackageCode.text.toString(),
                    tagList,
                    selectedItem
                )
            }
        }
    }

    private fun removeLatestTag() {
        tagList.forEachIndexed { index, tag ->
            if (tag.tag == lastScanTagId) {
                adapter.removeItem(index)
                adapter.notifyDataSetChanged()
                return@forEachIndexed
            }
        }
        showRFIDAlreadyPresentDialog(lastScanTagId)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun clearScreen() {
        txtPackageCode.setText("")
        txtTagCode.setText("")
        adapter.removeAll()
        adapter.notifyDataSetChanged()
        tagList = ArrayList()
        spnPkgItem.setSelection(0)
        repeat(4) {
            val newTag = Tag()
            newTag.tag = ""
            tagList.add(newTag)
        }
    }

    private fun initializeEmptyBoxes() {
        tagList.clear()
        repeat(4) {
            var newTag = Tag()
            newTag.tag = ""
            tagList.add(newTag)
        }
        adapter = TagAdapter(this, tagList, lastScanTagId)
        gd.adapter = adapter
    }

    private fun addTag(tagId: String) {
        var i = 0;
        tagList.forEachIndexed foreach@{ index, element ->
            if (element.tag.isNullOrEmpty() && i == 0) {
                val newTag = Tag()
                newTag.tag = tagId
                tagList[index] = newTag
                adapter.notifyDataSetChanged()
                i = 1
            }
        }
        if (txtPackageCode.text != null && !txtPackageCode.text.isNullOrEmpty()) {
            viewModel.getTagEntity(tagId)
        } else
            hasScanned = false
    }

    private fun verifyTagListSize(): Boolean {
        for (tag in tagList) {
            if (tag.tag.isNullOrEmpty())
                return false
        }
        return true
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.transaction_dialog_alert)
        val width = (this.resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnStatusAction =
            dialog.findViewById<Button>(R.id.btnOk)
        btnStatusAction.setOnClickListener { v: View? ->
            clearScreen()
            txtPackageCode.requestFocus()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openTagDetails(tagCode: String, position: Int) {
        AlertDialog.Builder(this)
            .setTitle(tagCode)
            .setMessage("Are you sure you want to delete this tag?")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                removeTag(position, tagCode)
            }
            .setNegativeButton(android.R.string.no) { dialog, which ->

            }
            .show()
    }

    private fun removeTag(position: Int, tagCode: String) {
        adapter.removeItem(position)
        adapter.notifyDataSetChanged()
        viewModel.releaseTag(tagCode)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(PackageViewModel::class.java)
    }

    private fun setUI() {
        setSupportActionBar(toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        handler = Handler()
        repeat(4) {
            var newTag = Tag()
            newTag.tag = ""
            tagList.add(newTag)
        }
        adapter = TagAdapter(this, tagList, lastScanTagId)
        gd.onItemClickListener = OnItemClickListener { parent, v, position, id ->
            if (!tagList[position].tag.isNullOrEmpty())
                openTagDetails(tagList[position].tag!!, position)
        }
        btnClearScreen.width = (ScreenUtils.getScreenWidth(this) * 0.38).toInt()
        txtPackageCode.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (txtPackageCode.text.toString().trim().isNotEmpty()) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.fetchTagsByPkg(txtPackageCode.text.toString())
                    ScreenUtils.hideKeyboard(this)
                    return@OnEditorActionListener true
                }
            } else {
                ToastUtil.createShortToast(this, "Please enter a correct package name")
            }
            false
        })
        txtTagCode.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                /*   viewModel.fetchTagsByTags(txtTagCode.text.toString())
                   ScreenUtils.hideKeyboard(this)*/
                val selectedIndex: Int = getTagSelectedIndex(txtTagCode.text.toString())
                if (selectedIndex == -1) {
                    addTag(txtTagCode.text.toString())
                } else {
                    tagList[selectedIndex].isChecked = true
                    adapter.notifyDataSetChanged()
                }
                ScreenUtils.hideKeyboard(this)
                txtTagCode.setText("")
                return@OnEditorActionListener true
            }
            false
        })

    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun showRFIDAlreadyPresentDialog(lastScanTagId: String) {
        val builder = SpannableStringBuilder()
        val first = "The Tag $lastScanTagId is associated with another entity."
        builder.append(first)
        val second = " Please scan another tag."
        val redSpannable = SpannableString(second)
        redSpannable.setSpan(ForegroundColorSpan(Color.RED), 0, second.length, 0)
        builder.append(redSpannable)

        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage(builder)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                previousScanId = lastScanTagId
                dialog.dismiss()
            }
            .show()
    }

    fun areAllTagsScanned(): Boolean {
        for (element in tagList) {
            if (element.tag.isNullOrEmpty())
                return false
        }
        return true
    }

    fun UHF_SetTagUpdateParam() {
        // Check if the current settings are the same, if not, then set it.
        var searchRT = ""
        try {
            searchRT = RFIDReader._Config.GetTagUpdateParam(ConnID)
            val arrRT = searchRT.split("\\|".toRegex()).toTypedArray()
            if (arrRT.size >= 2) {
                val nowUpDataTime = arrRT[0].toInt()
                val rssiFilter = arrRT[1].toInt()
                Log.d("Debug", "Check the label to upload time:$nowUpDataTime")
                if (upDataTime != nowUpDataTime) {
                    val param = "1," + upDataTime
                    RFIDReader._Config.SetTagUpdateParam(
                        ConnID,
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
                        if (!hasScanned) {
                            val tagId = currenttagModel?._EPC + currenttagModel?._TID
                            val selectedIndex: Int = getTagSelectedIndex(tagId)
                            if (selectedIndex == -1) {
                                lastScanTagId = tagId
                                addTag(tagId)
                                hasScanned = true
                            } else {
                                tagList[selectedIndex].isChecked = true
                                adapter.notifyDataSetChanged()
                                hasScanned = false
                            }
                        }
                    }
                }
            })
        } catch (ex: java.lang.Exception) {
            Log.d("Debug", "Tags output exceptions:" + ex.message)
        }
    }

    private fun getTagSelectedIndex(tagId: String): Int {
        tagList.forEachIndexed { index, element ->
            if (element.tag == tagId)
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
                handler.post {
                    packageCode?.let { txtPackageCode.setText(it) }

                    if (txtPackageCode.text.toString() !== packageCode) {
                        clearScreen()
                        packageCode?.let {
                            viewModel.fetchTagsByPkg(it)
                            txtPackageCode.setText(it)
                        }
                    }
                }
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