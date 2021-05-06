package com.limetac.scanner.ui.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.limetac.scanner.R
import com.limetac.scanner.data.model.Tag
import kotlinx.android.synthetic.main.rfid_row.view.layout
import kotlinx.android.synthetic.main.rfid_row.view.txt


class BinAdapter : BaseAdapter {
    var tagList = ArrayList<Tag>()
    var context: Context? = null

    constructor(context: Context, foodsList: ArrayList<Tag>) : super() {
        this.context = context
        this.tagList = foodsList
    }

    override fun getCount(): Int {
        return tagList.size
    }

    override fun getItem(position: Int): Any {
        return tagList[position]
    }


    fun removeItem(index: Int) {
        var tag = Tag()
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
        val inflator = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val foodView = inflator.inflate(R.layout.bin_row, null)
        val dpRatio: Float? = context?.resources?.displayMetrics?.density

        if (tag.tag != null && !tag.tag.isNullOrEmpty()) {
            foodView.txt.text = tag.tag!!.takeLast(3)
        }
        if (tag.isChecked) {
            Handler().postDelayed({

                context?.resources?.getColor(R.color.itemRepeatColor)?.let { it1 ->
                    foodView.layout.setBackgroundColor(
                        it1
                    )

                    tag.isChecked = false

                }
            }, 2000)
            //   setColor(position, foodView.layout)
            foodView.txt.text = tag.tag?.takeLast(3)

        }


        return foodView
    }

    private fun setColor(position: Int, layout: LinearLayout?) {
        when (position) {
            1 -> context?.resources?.getDrawable(R.color.blueColor)?.let {
                layout?.setBackground(it)
            }
            2 -> context?.resources?.getColor(R.color.redColor)?.let {
                layout?.setBackgroundColor(it)
            }
            else -> context?.resources?.getColor(R.color.primaryColor)?.let {
                layout?.setBackgroundColor(it)
            }
        }


    }

}