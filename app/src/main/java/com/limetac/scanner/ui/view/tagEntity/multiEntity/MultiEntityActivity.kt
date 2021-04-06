package com.limetac.scanner.ui.view.tagEntity.multiEntity

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.limetac.scanner.R
import com.limetac.scanner.data.api.request.BinResponse
import com.limetac.scanner.utils.Constants
import kotlinx.android.synthetic.main.activity_multi_entity.*
import kotlinx.android.synthetic.main.activity_tag_scan.*


class MultiEntityActivity : AppCompatActivity() {

    private lateinit var binResponse: List<BinResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_entity)
        setSupportActionBar(activityMultiEntity_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun init() {
        activityMultiEntity_scanTagBtn.setOnClickListener {
            finish()
        }

        intent?.extras?.let {
            val type = object : TypeToken<List<BinResponse?>?>() {}.type
            binResponse = Gson().fromJson(intent.getStringExtra("EntityList"), type)
            val scannedTag = intent.getStringExtra(Constants.TagScanning.SCANNED_TAG_KEY) as String
            activityMultiEntity_title.text = scannedTag
            for (entity in binResponse) {
                entity.type?.let {
                    addEntityText(it)
                }
            }
        }
    }

    private fun addEntityText(entityName: String) {
        val entityTextView = TextView(this)
        entityTextView.text = entityName
        entityTextView.textSize = 25f
        entityTextView.setPadding(10, 10, 10, 10)
        entityTextView.setTextColor(resources.getColor(R.color.secondaryFontColor))
        entityTextView.gravity = Gravity.CENTER_HORIZONTAL
        activityMultiEntity_entityContainer.addView(entityTextView)
    }
}