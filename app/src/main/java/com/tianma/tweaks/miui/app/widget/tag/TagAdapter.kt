package com.tianma.tweaks.miui.app.widget.tag

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tianma.tweaks.miui.R

class TagAdapter(private var context: Context, private var dataList: MutableList<TagBean>) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    var itemClickCallback: ItemClickCallback<TagBean>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.tag_view, parent, false)
        return TagViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tagBean = dataList[position]
        holder.bindData(tagBean, position)
        holder.bindListener(tagBean, position)
    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var tagView: TextView

        init {
            bindViews()
        }

        private fun bindViews() {
            tagView = itemView.findViewById(R.id.tag_text_view)
        }

        fun bindData(tagBean: TagBean, position: Int) {
            tagView.text = tagBean.value

            tagView.isSelected = tagBean.isSelected
            tagView.isEnabled = tagBean.isEnabled
        }

        fun bindListener(tagBean: TagBean, position: Int) {
            itemClickCallback?.let { itemClickCallback ->
                itemView.setOnClickListener { itemView ->
                    itemClickCallback.onItemClicked(itemView, tagBean, position)
                }

                itemView.setOnLongClickListener { itemView ->
                    itemClickCallback.onItemLongClicked(itemView, tagBean, position)
                }
            }
        }
    }

    fun getDataList(): List<TagBean> {
        return dataList
    }

    fun setDataList(list: MutableList<TagBean>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun setAllSelected(selected: Boolean) {
        for (tagBean in dataList) {
            tagBean.isSelected = selected
        }
        notifyDataSetChanged()
    }

    fun setItemSelected(selected: Boolean, position: Int) {
        dataList[position].isSelected = selected
        notifyDataSetChanged()
    }

    fun setAllEnabled(enabled: Boolean) {
        for (tagBean in dataList) {
            tagBean.isEnabled = enabled
        }
        notifyDataSetChanged()
    }


}