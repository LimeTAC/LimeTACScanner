package com.limetac.scanner.ui.view.tagEntity.packageTag

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.limetac.scanner.R
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.utils.Constants.TagScanning.BIN_RESPONSE_KEY
import com.limetac.scanner.utils.Constants.TagScanning.SCANNED_TAG_KEY
import com.limetac.scanner.utils.DialogUtil.showTagDialog
import kotlinx.android.synthetic.main.activity_package_tag.*
import kotlinx.android.synthetic.main.activity_tag_scan.*


class PackageTagActivity : AppCompatActivity() {

    lateinit var binResponse: BinResponse
    var scannedTag = ""
    lateinit var adapter: PackageTagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_tag)
        setSupportActionBar(activityPackageTag_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun init() {
        activityPackageTag_scanTagBtn.setOnClickListener {
            finish()
        }
        intent?.extras?.let {
            binResponse = intent.getSerializableExtra(BIN_RESPONSE_KEY) as BinResponse
            scannedTag = intent.getStringExtra(SCANNED_TAG_KEY) as String
            activityPackageTag_title.text = scannedTag
            binResponse.tagDetails?.let { tagDetails ->
                for (i in tagDetails.indices) {
                    if (i == 0) {
                        if (tagDetails[i].tagCode == scannedTag) {
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
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityPackageTag_rowOneRight.background = it
                            }
                        }
                        activityPackageTagRowOneRight_txt.text = tagDetails[i].tagCode?.takeLast(3)
                        activityPackageTagRowOneRight_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 2) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityPackageTag_rowTwoLeft.background = it
                            }
                        }
                        activityPackageTagRowTwoLeft_txt.text = tagDetails[i].tagCode?.takeLast(3)
                        activityPackageTagRowTwoLeft_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 3) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityPackageTag_rowTwoRight.background = it
                            }
                        }
                        activityPackageTagRowTwoRight_txt.text = tagDetails[i].tagCode?.takeLast(3)
                        activityPackageTagRowTwoRight_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                }
            }
        }
    }


}