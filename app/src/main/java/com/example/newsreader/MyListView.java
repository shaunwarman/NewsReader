package com.example.newsreader;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyListView extends ListView 
{

    public MyListView(Context context) 
    {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
    }

    @Override public boolean onTouchEvent(MotionEvent ev)
    {
        try { return super.onTouchEvent(ev); }
        catch(ArrayIndexOutOfBoundsException e)
        {
            Log.w("Caught ArrayIndexOutOfBoundsException", e.getMessage());
            return true; // handled
        }       
    }   
}
