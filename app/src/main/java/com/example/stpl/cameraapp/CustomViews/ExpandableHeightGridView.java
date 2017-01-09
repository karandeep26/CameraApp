package com.example.stpl.cameraapp.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
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
}