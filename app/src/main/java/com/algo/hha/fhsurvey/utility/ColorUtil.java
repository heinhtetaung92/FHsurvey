package com.algo.hha.fhsurvey.utility;

import com.algo.hha.fhsurvey.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * Created by heinhtetaung on 5/21/15.
 */
public class ColorUtil {

    public static List<Integer> colorlist = Arrays.asList(new Integer[]{R.color.red_500,
            R.color.pink_500,
            R.color.purple_500,
            R.color.deep_purple_500,
            R.color.indigo_500,
            R.color.blue_500,
            R.color.light_blue_500,
            R.color.cyan_500,
            R.color.teal_500,
            R.color.green_500,
            R.color.light_green_500,
            /*R.color.lime_500,
            R.color.yellow_500,
            R.color.amber_500,*/
            R.color.orange_500,
            R.color.deep_orange_500,
            R.color.brown_500,
            R.color.grey_500,
            R.color.blue_grey_500});

    public static int prevPosition = 0;

    public static Integer getRandomColor(){
        Random random = new Random();
        int position = 0;
        while(prevPosition != (position=random.nextInt(colorlist.size()))){
            prevPosition = position;
            return colorlist.get(position);
        }

        return colorlist.get(random.nextInt(colorlist.size()));

    }



}
