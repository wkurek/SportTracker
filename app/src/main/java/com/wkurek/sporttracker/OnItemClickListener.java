package com.wkurek.sporttracker;

import android.view.View;

/**
 * Interface which is used to pass the required reactions on click events.
 */

public interface OnItemClickListener {
    void onClick(View item, int position);
    void onLongClick(View item, int position);
}
