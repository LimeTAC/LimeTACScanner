package com.limetac.scanner.ui.view.scanAntenna

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.limetac.scanner.R
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.utils.ScreenUtils
import kotlinx.android.synthetic.main.rfid_row.view.*

class AntennaAdapter(var context: Context, foodsList: ArrayList<BinTag>) : BaseAdapter() {
    var tagList = foodsList
    var previousView: View? = null


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
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val foodView = inflator.inflate(R.layout.rfid_row, null)
        foodView.layout.layoutParams.width = (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
        foodView.layout.layoutParams.height = (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
        foodView.layout.setOnClickListener(GridItemClick(position));
        /* foodView.layout.setOnClickListener {
             if (previousView != null) {
                 previousView?.layout?.layoutParams?.width =
                     (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
                 previousView?.layout?.layoutParams?.height =
                     (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
             }

             val params = foodView.layout.layoutParams
             params.height = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()
             params.width = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()
             foodView.layout.layoutParams = params
             previousView = foodView
             if (context is AntennaActivity) {
                 (context as AntennaActivity).setSelectViewIndex(position)
             }
         }*/
        foodView.txt.text = tag.tagIndex.toString()
        if (tag.tagCode != null && !tag.tagCode.isNullOrEmpty()) {
            context?.resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                foodView.layout.background = it
                foodView.txt.text = tag.tagCode!!.takeLast(3)
            }
        }
        return foodView
    }


    inner class GridItemClick(private val position: Int) : View.OnClickListener {
        override fun onClick(v: View?) {
            previousView?.let {
                it.layoutParams?.width = (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
                it.layoutParams?.height = (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
                it.invalidate()
                it.requestLayout()
            }
            previousView = v
            val params = v?.layoutParams
            params?.height = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()
            params?.width = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()
            v?.layout?.layoutParams = params
            if (context is AntennaActivity) {
                (context as AntennaActivity).setSelectViewIndex(position)
            }
        }

    }
}
