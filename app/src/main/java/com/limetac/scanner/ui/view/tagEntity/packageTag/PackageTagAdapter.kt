package com.limetac.scanner.ui.view.tagEntity.packageTag

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.limetac.scanner.R
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.utils.ScreenUtils
import kotlinx.android.synthetic.main.rfid_row.view.*

class PackageTagAdapter(
    context: Context,
    private val scannedTag: String,
    tagDetailList: ArrayList<BinTag>
) :
    BaseAdapter() {
    var tagList = tagDetailList
    var context: Context? = context

    override fun getCount(): Int {
        return tagList.size
    }

    override fun getItem(position: Int): Any {
        return tagList[position]
    }

    fun removeItem(index: Int) {
        val tag = BinTag()
        tag.tagCode = ""
        tagList[index] = tag
    }

    fun removeAll() {
        tagList = ArrayList<BinTag>()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tag = this.tagList[position]
        val inflator = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val foodView = inflator.inflate(R.layout.rfid_row, null)
        foodView.layout.layoutParams.width = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()
        tag.tagCode?.let { code ->
            if (code == scannedTag) {
                context?.resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                    foodView.layout.background = it
                }
            }
            foodView.txt.text = tag.tagCode.takeLast(3)
        }

        return foodView
    }
}