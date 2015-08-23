package com.algo.hha.fhsurvey.utility;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

/**
 * Created by heinhtet on 8/21/15.
 */
public class InputFilterMinMax implements InputFilter {

    private int min, max;
    private Context mContext;

    public InputFilterMinMax(Context con, int min, int max) {
        this.min = min;
        this.max = max;
        mContext = con;
    }

    public InputFilterMinMax(Context con, String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
        mContext = con;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input)) {

                return null;
            }
        } catch (NumberFormatException nfe) { }
        Toast.makeText(mContext, "Input must between " + min + " and " + max, Toast.LENGTH_SHORT).show();
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

}
