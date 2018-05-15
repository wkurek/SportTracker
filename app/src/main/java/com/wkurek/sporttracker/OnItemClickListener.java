package com.wkurek.sporttracker;

import android.view.View;

/**
 * Created by Wojtek on 15.05.2018.
 */

public interface OnItemClickListener {
    void onClick(View item, int position);
    void onLongClick(View item, int position);
}
