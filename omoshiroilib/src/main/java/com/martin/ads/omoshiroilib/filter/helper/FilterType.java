package com.martin.ads.omoshiroilib.filter.helper;

/**
 * Created by Ads on 2017/2/13.
 */

public enum FilterType {
    //Pass Through
    NONE,

    //Image Processing : 2
    GRAY_SCALE,
    INVERT_COLOR,

    //Effects : 23
    SCALING,
    BOX_BLUR,
    GAUSSIAN_BLUR,
    RANDOM_BLUR,
    FAST_BLUR,
    BLURRED_FRAME,
    SPHERE_REFLECTOR,
    FILL_LIGHT_FILTER,
    GREEN_HOUSE_FILTER,
    BLACK_WHITE_FILTER,
    PAST_TIME_FILTER,
    MOON_LIGHT_FILTER,
    PRINTING_FILTER,
    TOY_FILTER,
    BRIGHTNESS_FILTER,
    VIGNETTE_FILTER,
    MULTIPLY_FILTER,
    REMINISCENCE_FILTER,
    SUNNY_FILTER,
    MX_LOMO_FILTER,
    SHIFT_COLOR_FILTER,
    MX_FACE_BEAUTY_FILTER,
    MX_PRO_FILTER,

    //Extended : 2
    BRA_SIZE_TEST_LEFT,
    BRA_SIZE_TEST_RIGHT
}
