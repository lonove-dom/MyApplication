package com.example.note.justdo.TActivityTools;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Choz on 2018/4/5.
 */

public class MviewitemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int pos = parent.getChildAdapterPosition(view);
        outRect.top = 62;
        if (pos % 2 == 0)
            outRect.left = 60;
        else
            outRect.right = 60;

    }

}

