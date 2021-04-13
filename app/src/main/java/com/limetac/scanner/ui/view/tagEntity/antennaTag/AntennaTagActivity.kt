package com.limetac.scanner.ui.view.tagEntity.antennaTag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.limetac.scanner.R
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.data.model.Tag
import com.limetac.scanner.utils.Constants
import com.limetac.scanner.utils.DialogUtil.showTagDialog
import kotlinx.android.synthetic.main.activity_antenna_tag.*
import kotlinx.android.synthetic.main.activity_multi_entity.*
import kotlinx.android.synthetic.main.activity_tag_scan.*

class AntennaTagActivity : AppCompatActivity() {

    lateinit var binResponse: BinResponse
    var scannedTag = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antenna_tag)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun init() {
        setSupportActionBar(activityAntennaTag_toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        activityAntennaTag_scanTagBtn.setOnClickListener {
            finish()
        }
        intent?.extras?.let {
            binResponse =
                intent.getSerializableExtra(Constants.TagScanning.BIN_RESPONSE_KEY) as BinResponse
            scannedTag = intent.getStringExtra(Constants.TagScanning.SCANNED_TAG_KEY) as String
            activityAntennaTag_title.text = binResponse.code
            binResponse.tagDetails?.let { tagDetails ->
                for (i in tagDetails.indices) {
                    val tag = tagDetails[i]
                    when (tag.tagIndex) {
                        7 -> {
                            if (tag.tagCode == scannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowOneLeft.background = it
                                }
                            }
                            activityAntennaTagRowOneLeft_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTag_rowOneLeft.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        8 -> {
                            if (tag.tagCode == scannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowOneRight.background = it
                                }
                            }
                            activityAntennaTagRowOneRight_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowOneRight_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        6 -> {
                            if (tag.tagCode == scannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoRight.background = it
                                }
                            }
                            activityAntennaTagRowTwoRight_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowTwoRight_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        3 -> {
                            if (tag.tagCode == scannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoLeft.background = it
                                }
                            }
                            activityAntennaTagRowTwoLeft_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowTwoLeft_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }
                        2 -> {
                            if (tag.tagCode == scannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoRight.background = it
                                }
                            }
                            activityAntennaTagRowThreeRight_txt.text =
                                tag.tagCode?.takeLast(3)
                            activityAntennaTagRowThreeRight_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }

                        1 -> {
                            if (tag.tagCode == scannedTag) {
                                resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                                    activityAntennaTag_rowTwoRight.background = it
                                }
                            }
                            activityAntennaTagRowThreeLeft_txt.text = tag.tagCode?.takeLast(3)
                            activityAntennaTagRowThreeLeft_txt.setOnClickListener {
                                showTagDialog(this, tag.tagCode)
                            }
                        }
                    }

                }
            }
        }
    }

}