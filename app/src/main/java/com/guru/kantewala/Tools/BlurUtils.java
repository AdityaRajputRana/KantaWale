package com.guru.kantewala.Tools;


import android.graphics.BlurMaskFilter;
import android.widget.TextView;

public class BlurUtils {

    public enum BlurMode {
        INNER,
        NORMAL,
        OUTSIDE,
        SOLID
    }

    private TextView view;

    public static void blur(TextView textView, float radius){
        BlurUtils transformation = new BlurUtils(textView);
        transformation.blurTextView(BlurMode.NORMAL, radius);
    }

    private BlurUtils(final TextView view) {
        this.view = view;
    }

    private void blurTextView(BlurMode blurMode, float radius) {

        //Check if the view is null
        if(view == null) {
            return;
        }

        BlurMaskFilter.Blur blur = BlurMaskFilter.Blur.NORMAL;
        switch (blurMode) {
            case INNER:
                blur = BlurMaskFilter.Blur.INNER;
                break;

            case OUTSIDE:
                blur = BlurMaskFilter.Blur.OUTER;
                break;

            case SOLID:
                blur = BlurMaskFilter.Blur.SOLID;
                break;
        }

        if (radius == 0f){
            view.getPaint().setMaskFilter(null);
            return;
        }
        BlurMaskFilter filter = new BlurMaskFilter(radius, blur);
        view.getPaint().setMaskFilter(filter);
    }
}