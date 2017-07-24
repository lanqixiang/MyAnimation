package com.test.toon.util;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by Administrator on 2017/7/6.
 */

public class ViewHolder {
    public ViewHolder() {
    }

    public static <T extends View> T get(View view, int id) {
        SparseArray viewHolder = (SparseArray)view.getTag();
        if(viewHolder == null) {
            viewHolder = new SparseArray();
            view.setTag(viewHolder);
        }

        View childView = (View)viewHolder.get(id);
        if(childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }

        return (T) childView;
    }
}
