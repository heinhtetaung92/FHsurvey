package com.algo.hha.fhsurvey.utility;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by heinhtetaung on 5/23/15.
 */
public class RoundDrawable {

    public static ShapeDrawable createUserDrawable(Activity mAct, int color) {
        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setBounds(0, 0, 10, 20);
        float radius = 80;
        float[] radii = new float[] {radius, radius, radius, radius, radius, radius, radius, radius};
        Shape shape = new RoundRectShape(radii, new RectF(), radii);
        drawable.setShape(shape);
        Paint paint = drawable.getPaint();
        paint.setColor(mAct.getResources().getColor(color));
        return drawable;
    }

}
