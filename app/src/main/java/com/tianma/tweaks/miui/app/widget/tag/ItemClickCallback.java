package com.tianma.tweaks.miui.app.widget.tag;

import android.view.View;

public interface ItemClickCallback<E> {

    void onItemClicked(View itemView, E item, int position);

    boolean onItemLongClicked(View itemView, E item, int position);

}
