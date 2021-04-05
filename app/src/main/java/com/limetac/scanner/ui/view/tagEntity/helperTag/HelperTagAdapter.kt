package com.limetac.scanner.ui.view.tagEntity.helperTag

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.limetac.scanner.R
import com.limetac.scanner.data.model.BinTag

class HelperTagAdapter(
    private val context: Context,
    private val scannedTag: String,
    private var tagList: ArrayList<BinTag>
) :
    RecyclerView.Adapter<HelperTagAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById<View>(R.id.itemHelperScan_tagName) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_helper_scan, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false);
        val tag = tagList[position]
        holder.title.text = tag.tagCode
        if (tag.tagCode == scannedTag) {
            holder.title.setTextColor(context.resources.getColor(R.color.primaryColor))
            holder.title.textSize = 28f
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    fun updateTagList(tagList: ArrayList<BinTag>) {
        this.tagList = tagList
        notifyDataSetChanged()
    }

    fun removeAllItems() {
        this.tagList.clear()
        notifyDataSetChanged()
    }
}