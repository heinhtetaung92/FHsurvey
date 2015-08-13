// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.algo.hha.fhsurvey.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView
{

    public RoundedImageView(Context context)
    {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public RoundedImageView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int i)
    {
        if (bitmap.getWidth() != i || bitmap.getHeight() != i)
        {
            bitmap = Bitmap.createScaledBitmap(bitmap, i, i, false);
        }
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle((float)(bitmap.getWidth() / 2) + 0.7F, (float)(bitmap.getHeight() / 2) + 0.7F, (float)(bitmap.getWidth() / 2) + 0.1F, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return bitmap1;
    }

    protected void onDraw(Canvas canvas)
    {
        Object obj = getDrawable();
        if (obj == null)
        {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0)
        {
            return;
        } else
        {
            obj = ((BitmapDrawable)obj).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
            int i = getWidth();
            getHeight();
            canvas.drawBitmap(getCroppedBitmap(((Bitmap) (obj)), i), 0.0F, 0.0F, null);
            return;
        }
    }
}
