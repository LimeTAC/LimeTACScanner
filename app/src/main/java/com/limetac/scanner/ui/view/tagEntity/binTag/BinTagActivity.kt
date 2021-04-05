package com.limetac.scanner.ui.view.tagEntity.binTag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.limetac.scanner.R
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.ui.view.tagEntity.helperTag.HelperTagAdapter
import com.limetac.scanner.utils.Constants
import kotlinx.android.synthetic.main.activity_bin_tag.*
import kotlinx.android.synthetic.main.activity_helper_tag.*
import kotlinx.android.synthetic.main.activity_tag_scan.*

class BinTagActivity : AppCompatActivity() {

    lateinit var binResponse: BinResponse
    var scannedTag = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bin_tag)
        setSupportActionBar(activityBinTag_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun init() {
        intent?.extras?.let {
            binResponse =
                intent.getSerializableExtra(Constants.TagScanning.BIN_RESPONSE_KEY) as BinResponse
            scannedTag = intent.getStringExtra(Constants.TagScanning.SCANNED_TAG_KEY) as String
            activityBinTag_title.text = scannedTag
            val rightTagAdapter = BinTagAdapter(
                this,
                scannedTag,
                binResponse.tagDetails.filter { it.tagType == 1 } as ArrayList<BinTag>)
            val leftTagAdapter = BinTagAdapter(
                this,
                scannedTag,
                binResponse.tagDetails.filter { it.tagType == 2 } as ArrayList<BinTag>)
            activityHelperTag_leftTagList.adapter = leftTagAdapter
            activityBinTag_rightTagList.adapter = rightTagAdapter
            activityHelperTag_leftTagList.layoutManager = LinearLayoutManager(this)
            activityBinTag_rightTagList.layoutManager = LinearLayoutManager(this)
        }

        activityBinTag_scanTagBtn.setOnClickListener {
            finish()
        }
    }

}