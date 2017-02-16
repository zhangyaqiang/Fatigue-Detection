package com.martin.ads.omoshiroilib.filter.helper;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.effect.SphereReflector;
import com.martin.ads.omoshiroilib.filter.effect.mx.BlackWhiteFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.BrightnessFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.FillLightFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.GreenHouseFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.MoonLightFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.MultiplyFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.MxFaceBeautyFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.MxLomoFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.MxProFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.PastTimeFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.PrintingFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.ReminiscenceFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.ShiftColorFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.SunnyFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.ToyFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.VignetteFilter;
import com.martin.ads.omoshiroilib.filter.ext.BlurredFrameEffect;
import com.martin.ads.omoshiroilib.filter.ext.BraSizeTestLeftFilter;
import com.martin.ads.omoshiroilib.filter.ext.BraSizeTestRightFilter;
import com.martin.ads.omoshiroilib.filter.ext.ScalingFilter;
import com.martin.ads.omoshiroilib.filter.ext.shadertoy.FastBlurFilter;
import com.martin.ads.omoshiroilib.filter.ext.shadertoy.RandomBlurFilter;
import com.martin.ads.omoshiroilib.filter.imgproc.GaussianBlurFilter;
import com.martin.ads.omoshiroilib.filter.imgproc.GrayScaleShaderFilter;
import com.martin.ads.omoshiroilib.filter.imgproc.InvertColorFilter;

/**
 * Created by Ads on 2017/2/13.
 */

public class FilterFactory {

    public static AbsFilter createFilter(FilterType filterType, Context context){
        switch (filterType){
            //Image Processing
            case GRAY_SCALE:
                return new GrayScaleShaderFilter(context);
            case INVERT_COLOR:
                return new InvertColorFilter(context);

            //Effects
            case SCALING:
                return new ScalingFilter(context);
            case GAUSSIAN_BLUR:
                return new GaussianBlurFilter(context);
            case BLURRED_FRAME:
                return new BlurredFrameEffect(context);
            case BOX_BLUR:
            case FAST_BLUR:
                return new FastBlurFilter(context);
            case RANDOM_BLUR:
                return new RandomBlurFilter(context);
            case SPHERE_REFLECTOR:
                return new SphereReflector(context);
            case FILL_LIGHT_FILTER:
                return new FillLightFilter(context);
            case GREEN_HOUSE_FILTER:
                return new GreenHouseFilter(context);
            case BLACK_WHITE_FILTER:
                return new BlackWhiteFilter(context);
            case PAST_TIME_FILTER:
                return new PastTimeFilter(context);
            case MOON_LIGHT_FILTER:
                return new MoonLightFilter(context);
            case PRINTING_FILTER:
                return new PrintingFilter(context);
            case TOY_FILTER:
                return new ToyFilter(context);
            case BRIGHTNESS_FILTER:
                return new BrightnessFilter(context);
            case VIGNETTE_FILTER:
                return new VignetteFilter(context);
            case MULTIPLY_FILTER:
                return new MultiplyFilter(context);
            case REMINISCENCE_FILTER:
                return new ReminiscenceFilter(context);
            case SUNNY_FILTER:
                return new SunnyFilter(context);
            case MX_LOMO_FILTER:
                return new MxLomoFilter(context);
            case SHIFT_COLOR_FILTER:
                return new ShiftColorFilter(context);
            case MX_FACE_BEAUTY_FILTER:
                return new MxFaceBeautyFilter(context);
            case MX_PRO_FILTER:
                return new MxProFilter(context);

            //Extended
            case BRA_SIZE_TEST_LEFT:
                return new BraSizeTestLeftFilter(context);
            case BRA_SIZE_TEST_RIGHT:
                return new BraSizeTestRightFilter(context);
            default:
                return new PassThroughFilter(context);
        }
    }
}
