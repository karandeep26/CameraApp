package com.example.stpl.cameraapp.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by stpl on 11/23/2016.
 */

public class ExpandableHeightGridView extends GridView {
    boolean expanded = false;

    public ExpandableHeightGridView(Context context)
    {
        super(context);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs,
                                    int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded()
    {
        return expanded;
    }



    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (isExpanded())
//        {
//            // Calculate entire height by providing a very large height hint.
//            // But do not use the highest 2 bits of this integer; those are
//            // reserved for the MeasureSpec mode.
//            int expandSpec = MeasureSpec.makeMeasureSpec(
//                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//            super.onMeasure(widthMeasureSpec, expandSpec);
//
//            ViewGroup.LayoutParams params = getLayoutParams();
//            params.height = getMeasuredHeight();
//        }
//        else
//        {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
//    }
}