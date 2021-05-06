package com.limetac.scanner.ui.view.scanAntenna

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.api.request.ReleaseTagRequest
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
import kotlinx.android.synthetic.main.activity_antenna.*
import kotlinx.android.synthetic.main.activity_package_scanning.*
import kotlinx.android.synthetic.main.activity_package_scanning.btnClearScreen
import kotlinx.android.synthetic.main.activity_package_scanning.gd
import kotlinx.android.synthetic.main.activity_package_scanning.progress
import kotlinx.android.synthetic.main.activity_package_scanning.toolbar
import kotlinx.android.synthetic.main.activity_package_scanning.txtPackageCode
import kotlinx.android.synthetic.main.activity_package_scanning.txtTagCode
import kotlinx.android.synthetic.main.activity_scan_helper.*

class AntennaActivity : AppCompatActivity(), IAsynchronousMessage, ScanAntennaNotifier {

    private lateinit var viewModel: AntennaViewModel
    private var selectedIndex = -1
    var tagList = ArrayList<BinTag>()
    lateinit var adapter: AntennaAdapter
    var upDataTime = 0
    lateinit var handler: Handler
    var lastReadTag: String = ""
    var dialogTag: String = ""
    var isViewSelected = false
    var previousView: View? = null
    private var binResponse: BinResponse? = null
    private var displayIndex = -1
    private var position = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antenna)
        setUI()
        setupViewModel()
        setObserver()
        observeVerifyTag()
        observeReleaseTag()
    }

    private fun setObserver() {
        viewModel.getDetails().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    it.data?.let {
                        clearScreen()
                        Toast.makeText(
                            this,
                            "Data has been submitted successfully!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    progress.hide()
                    //Handle Error
                    //  clearScreen()
                    txtPackageCode.setText("")
                    txtTagCode.setText("")
                    showItemNotExistDialog()
                }
            }
        })

        viewModel.getEntityDetails().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    it.data?.let { details ->
                        binResponse = details
                        details.tagDetails?.let { tagDetails ->
                            clearScreen()
                            for (tag in tagDetails) {
                                val tag2 = BinTag()
                                tag2.tagCode = tag.tagCode
                                tag2.tagIndex = tag.tagIndex
                                tagList[convertIndex(tag.tagIndex)] = tag2
                            }
                        }
                        adapter.updateList(tagList)
                    }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    // clearScreen()
                    txtPackageCode.setText("")
                    txtTagCode.setText("")
                    showItemNotExistDialog()
                    progress.hide()
                    //Handle Error
                }
            }
        })


        activityAntenna_btnSubmit.setOnClickListener {
            if (!txtPackageCode.text.isNullOrEmpty()) {
                viewModel.submitAntenna(txtPackageCode.text.toString(), tagList)
            } else
                ToastUtil.createShortToast(this, "Please enter a forklift code")
        }

        btnClearScreen.setOnClickListener {
            txtPackageCode.setText("")
            txtTagCode.setText("")
            clearScreen()
        }
    }

    private fun observeReleaseTag() {
        viewModel.getReleaseLiveData().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    ToastUtil.createShortToast(
                        this@AntennaActivity,
                        "Tag has been released successfully"
                    )
                    val newTag = BinTag()
                    newTag.tagIndex = displayIndex
                    newTag.tagCode = ""
                    tagList[position] = newTag
                    adapter.notifyDataSetChanged()
                    isViewSelected = false
                    selectedIndex = -1
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    val newTag = BinTag()
                    newTag.tagIndex = displayIndex
                    newTag.tagCode = ""
                    tagList[position] = newTag
                    adapter.notifyDataSetChanged()
                    isViewSelected = false
                    selectedIndex = -1
                    progress.hide()
                }
            }
        })
    }

    private fun observeVerifyTag() {
        viewModel.getVerifyTag().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    it.data?.code?.let { it1 -> showTagAlreadyExistDialog(it1) }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    progress.hide()
                    if (it.data == null) {
                        val selectedIndex: Int = getTagSelectedIndex(lastReadTag)
                        if (selectedIndex == -1) {
                            addTag(lastReadTag)
                        } else {
                            //  tag[selectedIndex].isChecked = true
                            adapter.notifyDataSetChanged()
                            isViewSelected = false
                        }
                    }
                }
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun clearScreen() {
        tagList.clear()
        adapter.removeAll()
        adapter.updateList(tagList)
        tagList = ArrayList()
        var i = 1
        repeat(6) {
            val newTag = BinTag()
            assignIndex(newTag, i)
            newTag.tagCode = ""
            tagList.add(newTag)
            i += 1
        }
        initializeEmptyBoxes()
    }

    private fun initializeEmptyBoxes() {
        adapter = AntennaAdapter(this, this, tagList)
        gd.adapter = adapter
    }

    private fun addTag(tagId: String) {
        if (selectedIndex == -1) {
            val temp = ArrayList<BinTag>()
            temp.addAll(tagList)
            temp.sortBy { x -> x.tagIndex }
            val result = temp.filter { x -> x.tagCode.isNullOrEmpty() }
            tagList.filter { x -> x.tagIndex == result[0].tagIndex }[0].tagCode = tagId
        } else {
            if (tagList[selectedIndex].tagCode.isNullOrEmpty())
                tagList[selectedIndex].tagCode = tagId
        }
        adapter.notifyDataSetChanged()
        isViewSelected = false
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.transaction_dialog_alert)
        val width = (this.resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnStatusAction = dialog.findViewById<Button>(R.id.btnOk)
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
            .setMessage("Are you sure you want to delete this tag?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    // Continue with delete operation
                    removeTag(position, tagCode)
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog, which ->

            } //)
            .show()
    }

    private fun showItemNotExistDialog() {
        AlertDialog.Builder(this)
            .setTitle("Item not found!")
            .setMessage("Forklift you entered was not found. Please enter an existing Forklift.")
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showTagAlreadyExistDialog(tag: String) {
        if (dialogTag != tag) {
            AlertDialog.Builder(this)
                .setTitle("Tag already associated!")
                .setMessage("$tag you entered is already associated with an Antenna.")
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            dialogTag = tag
        }
    }

    private fun removeTag(position: Int, tagCode: String) {
        adapter.removeItem(position)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(AntennaViewModel::class.java)
    }

    private fun setUI() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        handler = Handler()

        var i = 1;
        repeat(6) {
            val newTag = BinTag()
            assignIndex(newTag, i)
            newTag.tagCode = ""
            tagList.add(newTag)
            i += 1
        }

        adapter = AntennaAdapter(this, this, tagList)
        gd.adapter = adapter
        gd.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            selectedIndex = position
            val v: View = gd.getChildAt(position)
        }
        btnClearScreen.width = (ScreenUtils.getScreenWidth(this) * 0.38).toInt()
        activityAntenna_btnSubmit?.width = (ScreenUtils.getScreenWidth(this) * 0.38).toInt()

        //    viewModel.fetchTagsByPkg("C72486")
        txtPackageCode.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.getEntity(txtPackageCode.text.toString())
                ScreenUtils.hideKeyboard(this)
                return@OnEditorActionListener true
            }
            false
        })

        txtTagCode.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                /*   viewModel.fetchTagsByTags(txtTagCode.text.toString())
                   ScreenUtils.hideKeyboard(this)*/
                val selectedIndex: Int = getTagSelectedIndex(txtTagCode.text.toString())
                if (selectedIndex == -1) {
                    addTag(txtTagCode.text.toString())
                } else {
                    // tag[selectedIndex].isChecked = true
                    adapter.notifyDataSetChanged()
                }
                ScreenUtils.hideKeyboard(this)
                txtTagCode.setText("")
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun setupPreTagListProcess() {
        var i = 1;
        repeat(6) {
            val newTag = BinTag()
            assignIndex(newTag, i)
            newTag.tagCode = ""
            tagList.add(newTag)
            i += 1
        }
        adapter = AntennaAdapter(this, this, tagList)
        gd.adapter = adapter
        gd.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            selectedIndex = position
            gd.getChildAt(position)
        }
    }

    private fun setSelectionDimension(v: View) {
        val params = v.layoutParams
        params.height = (ScreenUtils.getScreenWidth(this) * 0.3).toInt()
        params.width = (ScreenUtils.getScreenWidth(this) * 0.3).toInt()
        v.layoutParams = params
    }

    private fun assignIndex(newTag: BinTag, index: Int) {
        when (index) {
            1 -> newTag.tagIndex = 7
            2 -> newTag.tagIndex = 8
            3 -> newTag.tagIndex = 3
            4 -> newTag.tagIndex = 6
            5 -> newTag.tagIndex = 1
            6 -> newTag.tagIndex = 2
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }

    private fun convertIndex(index: Int): Int {
        return when (index) {
            7 -> 0
            8 -> 1
            3 -> 2
            6 -> 3
            1 -> 4
            2 -> 5
            else -> { // Note the block
                1
            }
        }
    }

    fun setSelectViewIndex(position: Int) {
        selectedIndex = position
        isViewSelected = true
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    fun areAllTagsScanned(): Boolean {
        for (element in tagList) {
            if (element.tagCode.isNullOrEmpty())
                return false
        }
        return true
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
                        if (isViewSelected) {
                            if (getTagSelectedIndex(tagId) == -1) {
                                viewModel.verifyTag(tagId)
                                lastReadTag = tagId
                            } else {
                                adapter.notifyDataSetChanged()
                                isViewSelected = false
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
                handler.post {
                    packageCode?.let { txtPackageCode.setText(it) }
                    if (txtPackageCode.text.toString() !== packageCode) {
                        clearScreen()
                        packageCode?.let {
                            viewModel.getEntity(it)
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

    override fun onDestroy() {
        super.onDestroy()
        RFIDReader._Config.Stop(UHFBaseActivity.ConnID)
    }

    override fun removeItemFromList(position: Int, displayIndex: Int) {
        this.displayIndex = displayIndex
        this.position = position
        if (binResponse != null) {
            val releaseTagRequest = ReleaseTagRequest()
            releaseTagRequest.entityId = binResponse?.id
            releaseTagRequest.tagCode = tagList[position].tagCode
            releaseTagRequest.entityType = EntityType.ANTENNA.type
            viewModel.releaseRequest(releaseTagRequest)
        } else {
            val newTag = BinTag()
            newTag.tagIndex = displayIndex
            newTag.tagCode = ""
            tagList[position] = newTag
            adapter.notifyDataSetChanged()
            isViewSelected = false
            selectedIndex = -1
        }
    }


}
