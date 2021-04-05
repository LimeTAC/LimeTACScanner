package com.limetac.scanner.ui.view.tagEntity.helperTag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.limetac.scanner.R
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.utils.Constants
import kotlinx.android.synthetic.main.activity_helper_tag.*
import kotlinx.android.synthetic.main.activity_tag_scan.*

class HelperTagActivity : AppCompatActivity() {

    lateinit var binResponse: BinResponse
    var scannedTag = ""
    lateinit var adapter: HelperTagAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_tag)
        setSupportActionBar(activityHelperTag_toolbar)
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
            activityHelperTag_title.text = scannedTag
            activityHelperTag_count.text = binResponse.tagDetails.size.toString()
            adapter =
                HelperTagAdapter(this, scannedTag, binResponse.tagDetails as ArrayList<BinTag>)
            activityHelperTag_scanList.adapter = adapter
            activityHelperTag_scanList.layoutManager = LinearLayoutManager(this)
        }

        activityHelperTag_scanTagBtn.setOnClickListener {
            finish()
        }
    }
}