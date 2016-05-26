package com.tone.dragandswipe;

import android.view.View;

/**
 * Created by zhaotong on 2016/5/26.
 */
public class ViewUtil {
    public static <T extends View> T getViewById(View view, int id) {
        return (T) view.findViewById(id);
    }
}
