package com.limetac.scanner.ui.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.limetac.scanner.R
import com.limetac.scanner.data.model.Tag
import com.limetac.scanner.utils.ScreenUtils
import kotlinx.android.synthetic.main.rfid_row.view.*

class TagAdapter(val context: Context, foodsList: ArrayList<Tag>, private val latestScannedTag: String) :
    BaseAdapter() {
    var tagList = foodsList

    override fun getCount(): Int {
        return tagList.size
    }

    override fun getItem(position: Int): Any {
        return tagList[position]
    }


    fun removeItem(index: Int) {
        val tag = Tag()
        tag.tag = ""
        tagList[index] = tag
    }

    fun removeAll() {
        tagList = ArrayList<Tag>()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tag = this.tagList[position]

        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val foodView = inflator.inflate(R.layout.rfid_row, null)
        foodView.layout.layoutParams.width = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()

        for (item in tagList) {
            if (item.tag == latestScannedTag) {
                foodView.txt.setTextColor(context.resources.getColor(R.color.white))
                foodView.txt.textSize = 35f
            } else {
                foodView.txt.setTextColor(context.resources.getColor(R.color.secondaryFontColor))
                foodView.txt.textSize = 30f
            }
        }
        if (tag.tag != null && !tag.tag.isNullOrEmpty()) {
            context?.resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                foodView.layout.background = it
                foodView.txt.text = tag.tag?.takeLast(3)
            }
        }
        if (tag.isChecked) {
            Handler().postDelayed({
                context?.resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    foodView.layout.background = it
                    tag.isChecked = false

                }
            }, 2000)
            context?.resources?.getDrawable(R.drawable.cd_bg_checked)?.let {
                foodView.layout.background = it
                foodView.txt.text = tag.tag?.takeLast(3)
            }
        }


        return foodView
    }
}