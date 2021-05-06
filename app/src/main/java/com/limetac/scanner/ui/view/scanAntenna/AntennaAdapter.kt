package com.limetac.scanner.ui.view.scanAntenna

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.limetac.scanner.R
import com.limetac.scanner.data.model.BinTag
import com.limetac.scanner.utils.ScreenUtils
import com.limetac.scanner.utils.ToastUtil
import kotlinx.android.synthetic.main.rfid_row.view.*

class AntennaAdapter(
    var context: Context,
    private val scanAntennaNotifier: ScanAntennaNotifier,
    var tagList: ArrayList<BinTag>
) : BaseAdapter() {
    var previousView: View? = null


    override fun getCount(): Int {
        return tagList.size
    }

    override fun getItem(position: Int): Any {
        return if (position >= tagList.size)
            tagList[0]
        else
            tagList[position]
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
        foodView.layout.setOnClickListener(GridItemClick(position))
        foodView.txt.text = tag.tagIndex.toString()
        if (tag.tagCode != null && !tag.tagCode.isNullOrEmpty()) {
            context?.resources?.getDrawable(R.drawable.cd_bg_colored)?.let {
                foodView.layout.background = it
                foodView.txt.text = tag.tagCode?.takeLast(3)
            }
        }
        return foodView
    }

    fun updateList(tagList: ArrayList<BinTag>) {
        this.tagList = tagList
        notifyDataSetChanged()
    }


    inner class GridItemClick(private val position: Int) : View.OnClickListener {
        override fun onClick(v: View?) {
            val textView = (v as LinearLayoutCompat).getChildAt(0) as TextView
            if (hasNoEntry(textView)) {
                previousView?.let {
                    it.layoutParams?.width = (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
                    it.layoutParams?.height = (ScreenUtils.getScreenWidth(context) * 0.25).toInt()
                    it.invalidate()
                    it.requestLayout()
                }
                previousView = v
                val params = v.layoutParams
                params?.height = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()
                params?.width = (ScreenUtils.getScreenWidth(context) * 0.3).toInt()
                v.layout?.layoutParams = params
                if (context is AntennaActivity) {
                    (context as AntennaActivity).setSelectViewIndex(position)
                }
            } else {
                tagList[position].tagCode?.let { confirmDeleteDialog(position, it, v) }
            }
        }

        private fun hasNoEntry(textView: TextView): Boolean {
            return when (textView.text) {
                "7", "8", "3", "6", "1", "2" -> {
                    true
                }
                else -> false
            }
        }

        private fun confirmDeleteDialog(position: Int, tagName: String, view: View) {
            AlertDialog.Builder(context)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to remove tag: $tagName ?")
                .setPositiveButton(
                    android.R.string.yes
                ) { dialog, _ ->
                    scanAntennaNotifier.removeItemFromList(position,convertIndex(position))
                    ((view as LinearLayoutCompat).getChildAt(0) as TextView).text = convertIndex(position).toString()
                    context?.resources?.getDrawable(R.drawable.cd_bg)?.let {
                        view.background = it
                    }
                    dialog.dismiss()
                }.setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        private fun convertIndex(index: Int): Int {
            return when (index) {
                0 -> 7
                1 -> 8
                2 -> 3
                3 -> 6
                4 -> 1
                5 -> 2
                else -> { // Note the block
                    1
                }
            }
        }
    }
}
