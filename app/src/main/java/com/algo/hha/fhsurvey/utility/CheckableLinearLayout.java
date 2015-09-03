// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.algo.hha.fhsurvey.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableLinearLayout extends RelativeLayout
    implements Checkable
{

    private static final int CHECKED_STATE_SET[] = {
        0x10100a0
    };
    private boolean mChecked;

    public CheckableLinearLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mChecked = false;
    }

    public boolean isChecked()
    {
        return mChecked;
    }

    public int[] onCreateDrawableState(int i)
    {
        int ai[] = super.onCreateDrawableState(i + 1);
        if (isChecked())
        {
            mergeDrawableStates(ai, CHECKED_STATE_SET);
        }
        return ai;
    }

    public void setChecked(boolean flag)
    {
        if (flag != mChecked)
        {
            mChecked = flag;
            refreshDrawableState();
        }
    }

    public void toggle()
    {
        boolean flag;
        if (!mChecked)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        setChecked(flag);
    }

}
