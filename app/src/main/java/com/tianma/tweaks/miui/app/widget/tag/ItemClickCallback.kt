package com.tianma.tweaks.miui.app.widget.tag

import android.view.View

interface ItemClickCallback<E> {

    fun onItemClicked(itemView: View?, item: E?, position: Int)

    fun onItemLongClicked(itemView: View?, item: E?, position: Int): Boolean
}