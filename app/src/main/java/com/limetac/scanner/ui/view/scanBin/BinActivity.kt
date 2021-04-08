package com.limetac.scanner.ui.view.scanBin

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.limetac.scanner.R
import com.limetac.scanner.data.api.ApiHelper
import com.limetac.scanner.data.api.ApiServiceImpl
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.reader.UHFBaseActivity.ConnID
import com.limetac.scanner.ui.base.ViewModelFactory
import com.limetac.scanner.ui.adapter.BinAdapter
import com.limetac.scanner.utils.ScreenUtils
import com.limetac.scanner.utils.Status
import com.rfidread.Interface.IAsynchronousMessage
import com.rfidread.Models.Tag_Model
import com.rfidread.RFIDReader
import kotlinx.android.synthetic.main.activity_bin.*
import java.util.*

class BinActivity : AppCompatActivity(), IAsynchronousMessage {


    private lateinit var viewModel: BinViewModel
    var tag = ArrayList<BinTag>()
    lateinit var adapter: BinAdapter
    var upDataTime = 0
    var view: View? = null
    var previousBinCode: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bin)
        setUI()
        setupViewModel()
        setObserver()
    }

    private fun setObserver() {
        viewModel.getBinDetails().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    it.data?.let { details ->
                        clearScreen()
                        Toast.makeText(
                            this,
                            "Bins have been submitted successfully",
                            Toast.LENGTH_LONG
                        ).show()
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

        viewModel.getEntityDetails().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progress.hide()
                    it.data?.let { details ->
                        details.tagDetails?.let {
                            for (tag in it) {
                                if (tag.isRight)
                                    addTag(tag.tagCode!!, true)
                                else
                                    addTag(tag.tagCode!!, false)
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    progress.show()
                }
                Status.ERROR -> {
                    progress.hide()
                    /*   it?.message?.let {
                           Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                       }*/
                    right.removeAllViews()
                    left.removeAllViews()
                    setDefaultDimension(left)
                    setDefaultDimension(right)
                    right.background = resources.getDrawable(R.drawable.cd_bg)
                    left.background = resources.getDrawable(R.drawable.cd_bg)
                }
            }
        })


        btnSubmit.setOnClickListener {
            if (txtCode.text != null && !txtCode.text.isNullOrEmpty()) {
                if (tag.size >= 2 && validateTags()) {
                    viewModel.submitBin(txtCode.text.toString(), tag)
                } else {
                    Toast.makeText(
                        BinActivity@ this,
                        getString(R.string.bin_tag_validation_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    BinActivity@ this,
                    getString(R.string.enter_bin_code),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnCancel.setOnClickListener {
            clearScreen()
        }

    }

    private fun validateTags(): Boolean {
        var rightTag = 0
        var leftTag = 0

        for (tag in tag) {
            if (tag.isRight) rightTag += 1
            else leftTag += 1
        }

        return rightTag >= 1 && leftTag >= 1
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun clearScreen() {
        txtCode.setText("")
        txtTagCode.setText("")
        right.removeAllViews()
        left.removeAllViews()
        setDefaultDimension(left)
        setDefaultDimension(right)
        right.background = resources.getDrawable(R.drawable.cd_bg)
        left.background = resources.getDrawable(R.drawable.cd_bg)
        tag.clear()
    }

    private fun clearScreenAfterCheckMarkClick() {
        txtTagCode.setText("")
        right.removeAllViews()
        left.removeAllViews()
        setDefaultDimension(left)
        setDefaultDimension(right)
        right.background = resources.getDrawable(R.drawable.cd_bg)
        left.background = resources.getDrawable(R.drawable.cd_bg)
        tag.clear()
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
            txtCode.requestFocus()
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun openTagDetails(
        code: String,
        tagCode: String,
        view: LinearLayoutCompat,
        tv: TextView
    ) {

        Builder(this)
            .setTitle(tagCode)
            .setMessage("Are you sure you want to delete this tag?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    view.removeView(tv)
                    if (view.childCount == 0)
                        view.background = resources.getDrawable(R.drawable.cd_bg)
                    removeTagFromList(code)
                    // Continue with delete operation

                    //viewModel.releaseTag(tagCode)
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog, which -> } //)
            .show()
    }


    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()))
        ).get(BinViewModel::class.java)
    }

    private fun setUI() {
        setSupportActionBar(toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        setDefaultDimension(right)
        setDefaultDimension(left)

        right.setOnClickListener {
            setDefaultDimension(left)
            setActiveDimension(right)
            view = right

        }
        left.setOnClickListener {
            setDefaultDimension(right)
            setActiveDimension(left)
            view = left
        }


        btnCancel.width = (ScreenUtils.getScreenWidth(this) * 0.38).toInt()
        btnSubmit?.width = (ScreenUtils.getScreenWidth(this) * 0.38).toInt()
        txtCode.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearScreenAfterCheckMarkClick()
                ScreenUtils.hideKeyboard(this)
                viewModel.getEntity(txtCode.text.toString())
                return@OnEditorActionListener true
            }
            false
        })


        txtTagCode.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val selectedIndex: Int = getTagSelectedIndex(txtTagCode.text.toString())
                if (selectedIndex == -1) {
                    if (view != null) {
                        if (view == right)
                            addTag(txtTagCode.text.toString(), true)
                        else
                            addTag(txtTagCode.text.toString(), false)
                    } else
                        Toast.makeText(this, getString(R.string.select_view), Toast.LENGTH_LONG)
                            .show()
                } else {
                    // tag[selectedIndex].isChecked = true
                    adapter.notifyDataSetChanged()
                }//
                ScreenUtils.hideKeyboard(this)
                txtTagCode.setText("")
                return@OnEditorActionListener true
            }
            false
        })

    }

    fun addTag(code: String, isRight: Boolean) {
        if (checkTagNumberValidation(isRight)) {
            val binTag = BinTag()
            binTag.tagCode = code
            binTag.isRight = isRight
            tag.add(binTag)
            val valueTV = TextView(this)
            valueTV.text = code.takeLast(3)
            val typeface: Typeface =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    resources.getFont(R.font.neue_black)
                } else {
                    ResourcesCompat.getFont(this, R.font.neue_black)!!
                }

            valueTV.typeface = typeface
            valueTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
            valueTV.setTextColor(resources.getColor(R.color.primaryFontColor))
            valueTV.gravity = Gravity.CENTER
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.setMargins(10, 10, 10, 10)
            valueTV.layoutParams = layoutParams;
            valueTV.setOnClickListener {
                if (isRight)
                    openTagDetails(code, valueTV.text.toString(), right, valueTV)
                else openTagDetails(code, valueTV.text.toString(), left, valueTV)
            }

            if (isRight) {
                (right as LinearLayout).addView(valueTV)
                right.background = resources.getDrawable(R.color.primaryColor)
            } else {
                (left as LinearLayout).addView(valueTV)
                left.background = resources.getDrawable(R.color.primaryColor)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }


    fun checkTagNumberValidation(isRight: Boolean): Boolean {
        var no = 0
        for (element in tag) {
            if (element.isRight && isRight)
                no = +1
            else (!element.isRight && !isRight)
            no = +1
        }
        return (no < 4)
    }

    private fun setActiveDimension(view: View) {
        val params = view.layoutParams
        params.height = (ScreenUtils.getScreenWidth(this) * 0.55).toInt()
        params.width = (ScreenUtils.getScreenWidth(this) * 0.4).toInt()
        view.layoutParams = params
    }

    private fun setDefaultDimension(view: View) {
        val params = view.layoutParams
        params.height = (ScreenUtils.getScreenWidth(this) * 0.5).toInt()
        params.width = (ScreenUtils.getScreenWidth(this) * 0.35).toInt()
        view.layoutParams = params
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
        RFIDReader.HP_CONNECT.get(ConnID)?.myLog = this
    }

    private fun UHF_SetTagUpdateParam() {
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
                    synchronized(tag) {
                        val tagId = currenttagModel?._EPC + currenttagModel?._TID
                        val selectedIndex: Int = getTagSelectedIndex(tagId)
                        if (selectedIndex == -1) {
                            if (view != null) {
                                if (view == right)
                                    addTag(tagId, true)
                                else
                                    addTag(tagId, false)
                            } else {
                                Toast.makeText(
                                    this@BinActivity,
                                    getString(R.string.select_view),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            //  tag[selectedIndex].isChecked = true
                            // adapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        } catch (ex: java.lang.Exception) {
            Log.d("Debug", "Tags output exceptions:" + ex.message)
        }
    }

    private fun getTagSelectedIndex(tagId: String): Int {
        tag.forEachIndexed { index, element ->
            if (element.tagCode == tagId)
                return index
        }
        return -1
    }

    private fun removeTagFromList(tagId: String) {
        var indexExist = -1
        tag.forEachIndexed { index, element ->
            if (element.tagCode == tagId) {
                indexExist = index
            }
        }
        if (indexExist != -1)
            tag.removeAt(indexExist)

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
