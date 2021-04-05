package com.limetac.scanner.ui.view.tagEntity.antennaTag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.limetac.scanner.R
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.utils.Constants
import com.limetac.scanner.utils.DialogUtil.showTagDialog
import kotlinx.android.synthetic.main.activity_antenna_tag.*
import kotlinx.android.synthetic.main.activity_tag_scan.*

class AntennaTagActivity : AppCompatActivity() {

    lateinit var binResponse: BinResponse
    var scannedTag = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antenna_tag)
        setSupportActionBar(activityPackageTag_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun init() {
        activityAntennaTag_scanTagBtn.setOnClickListener {
            finish()
        }
        intent?.extras?.let {
            binResponse = intent.getSerializableExtra(Constants.TagScanning.BIN_RESPONSE_KEY) as BinResponse
            scannedTag = intent.getStringExtra(Constants.TagScanning.SCANNED_TAG_KEY) as String
            activityAntennaTag_title.text = scannedTag
            binResponse.tagDetails?.let { tagDetails ->
                for (i in tagDetails.indices) {
                    if (i == 0) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityAntennaTag_rowOneLeft.background = it
                            }
                        }
                        activityAntennaTagRowOneLeft_txt.text = tagDetails[i].tagCode.takeLast(3)
                        activityAntennaTag_rowOneLeft.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 1) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityAntennaTag_rowOneRight.background = it
                            }
                        }
                        activityAntennaTagRowOneRight_txt.text = tagDetails[i].tagCode.takeLast(3)
                        activityAntennaTagRowOneRight_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 2) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityAntennaTag_rowTwoLeft.background = it
                            }
                        }
                        activityAntennaTagRowTwoLeft_txt.text = tagDetails[i].tagCode.takeLast(3)
                        activityAntennaTagRowTwoLeft_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                    if (i == 3) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityAntennaTag_rowTwoRight.background = it
                            }
                        }
                        activityAntennaTagRowTwoRight_txt.text = tagDetails[i].tagCode.takeLast(3)
                        activityAntennaTagRowTwoRight_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }

                    if (i == 4) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityAntennaTag_rowTwoRight.background = it
                            }
                        }
                        activityAntennaTagRowThreeLeft_txt.text = tagDetails[i].tagCode.takeLast(3)
                        activityAntennaTagRowThreeLeft_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }

                    if (i == 5) {
                        if (tagDetails[i].tagCode == scannedTag) {
                            resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                activityAntennaTag_rowTwoRight.background = it
                            }
                        }
                        activityAntennaTagRowThreeRight_txt.text = tagDetails[i].tagCode.takeLast(3)
                        activityAntennaTagRowThreeRight_txt.setOnClickListener {
                            showTagDialog(this, tagDetails[i].tagCode)
                        }
                    }
                }
            }
        }
    }

}