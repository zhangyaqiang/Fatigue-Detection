package com.martin.ads.omoshiroilib.filter.helper;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.beautify.BeautifyFilterA;
import com.martin.ads.omoshiroilib.filter.beautify.BeautifyFilterFUB;
import com.martin.ads.omoshiroilib.filter.beautify.BeautifyFilterFUC;
import com.martin.ads.omoshiroilib.filter.beautify.BeautifyFilterFUD;
import com.martin.ads.omoshiroilib.filter.beautify.BeautifyFilterFUE;
import com.martin.ads.omoshiroilib.filter.beautify.BeautifyFilterFUF;
import com.martin.ads.omoshiroilib.filter.effect.SphereReflector;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsAmaroFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsAntiqueFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsBlackCatFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsBrooklynFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsCalmFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsCoolFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsCrayonFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsEarlyBirdFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsEmeraldFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsEvergreenFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsFairyTaleFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsFreudFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsHealthyFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsHefeFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsHudsonFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsKevinFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsLatteFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsLomoFilter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsN1977Filter;
import com.martin.ads.omoshiroilib.filter.effect.insta.InsNashvilleFilter;
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
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.BeachFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.BrannanFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.CleanFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.CoralFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.CrispFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.FUOriginFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.FreshFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.GrassFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.InkwellFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.LolitaFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.NatureFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.PinkFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.RococoFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.RosyFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.SunsetFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.SweetyFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.UrbanFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.ValenciaFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.VintageFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.VividFilter;
import com.martin.ads.omoshiroilib.filter.effect.xiuxiuxiu.WaldenFilter;
import com.martin.ads.omoshiroilib.filter.ext.BlurredFrameEffect;
import com.martin.ads.omoshiroilib.filter.ext.BraSizeTestLeftFilter;
import com.martin.ads.omoshiroilib.filter.ext.BraSizeTestRightFilter;
import com.martin.ads.omoshiroilib.filter.ext.ScalingFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.AscIIArtFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.BasicDeformFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.BlueorangeFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.ChromaticAberrationFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.ContrastFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.CrackedFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.CrosshatchFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.EMInterferenceFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.EdgeDetectionFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.FastBlurFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.LegofiedFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.LichtensteinEsqueFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.MappingFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.MoneyFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.NoiseWarpFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.PixelizeFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.PolygonizationFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.RandomBlurFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.RefractionFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.TileMosaicFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.TrianglesMosaicFilter;
import com.martin.ads.omoshiroilib.filter.imgproc.CustomizedBoxBlurFilter;
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

            //ShaderToy : 20
            case EDGE_DETECTION_FILTER:
                return new EdgeDetectionFilter(context);
            case PIXELIZE_FILTER:
                return new PixelizeFilter(context);
            case EM_INTERFERENCE_FILTER:
                return new EMInterferenceFilter(context);
            case TRIANGLES_MOSAIC_FILTER:
                return new TrianglesMosaicFilter(context);
            case LEGOFIED_FILTER:
                return new LegofiedFilter(context);
            case TILE_MOSAIC_FILTER:
                return new TileMosaicFilter(context);
            case BLUEORANGE_FILTER:
                return new BlueorangeFilter(context);
            case CHROMATIC_ABERRATION_FILTER:
                return new ChromaticAberrationFilter(context);
            case BASICDEFORM_FILTER:
                return new BasicDeformFilter(context);
            case CONTRAST_FILTER:
                return new ContrastFilter(context);
            case NOISE_WARP_FILTER:
                return new NoiseWarpFilter(context);
            case REFRACTION_FILTER:
                return new RefractionFilter(context);
            case MAPPING_FILTER:
                return new MappingFilter(context);
            case CROSSHATCH_FILTER:
                return new CrosshatchFilter(context);
            case LICHTENSTEINESQUE_FILTER:
                return new LichtensteinEsqueFilter(context);
            case ASCII_ART_FILTER:
                return new AscIIArtFilter(context);
            case MONEY_FILTER:
                return new MoneyFilter(context);
            case CRACKED_FILTER:
                return new CrackedFilter(context);
            case POLYGONIZATION_FILTER:
                return new PolygonizationFilter(context);
            case FAST_BLUR_FILTER:
                return new FastBlurFilter(context);

            //XiuXiuXiu
            case NATURE:
                return new NatureFilter(context);
            case CLEAN:
                return new CleanFilter(context);
            case VIVID:
                return new VividFilter(context);
            case FRESH:
                return new FreshFilter(context);
            case SWEETY:
                return new SweetyFilter(context);
            case ROSY:
                return new RosyFilter(context);
            case LOLITA:
                return new LolitaFilter(context);
            case SUNSET:
                return new SunsetFilter(context);
            case GRASS:
                return new GrassFilter(context);
            case CORAL:
                return new CoralFilter(context);
            case PINK:
                return new PinkFilter(context);
            case URBAN:
                return new UrbanFilter(context);
            case CRISP:
                return new CrispFilter(context);
            case VALENCIA:
                return new ValenciaFilter(context);
            case BEACH:
                return new BeachFilter(context);
            case VINTAGE:
                return new VintageFilter(context);
            case ROCOCO:
                return new RococoFilter(context);
            case WALDEN:
                return new WaldenFilter(context);
            case BRANNAN:
                return new BrannanFilter(context);
            case INKWELL:
                return new InkwellFilter(context);
            case FUORIGIN:
                return new FUOriginFilter(context);

            //Inst
            case AMARO:
                return new InsAmaroFilter(context);
            case ANTIQUE:
                return new InsAntiqueFilter(context);
            case BLACK_CAT:
                return new InsBlackCatFilter(context);
            case BROOKLYN:
                return new InsBrooklynFilter(context);
            case CALM:
                return new InsCalmFilter(context);
            case COOL:
                return new InsCoolFilter(context);
            case CRAYON:
                return new InsCrayonFilter(context);
            case EARLY_BIRD:
                return new InsEarlyBirdFilter(context);
            case EMERALD:
                return new InsEmeraldFilter(context);
            case EVERGREEN:
                return new InsEvergreenFilter(context);
            case FAIRY_TALE:
                return new InsFairyTaleFilter(context);
            case FREUD:
                return new InsFreudFilter(context);
            case HEALTHY:
                return new InsHealthyFilter(context);
            case HEFE:
                return new InsHefeFilter(context);
            case HUDSON:
                return new InsHudsonFilter(context);
            case KEVIN:
                return new InsKevinFilter(context);
            case LATTE:
                return new InsLatteFilter(context);
            case LOMO:
                return new InsLomoFilter(context);
            case N1977:
                return new InsN1977Filter(context);
            case NASHVILLE:
                return new InsNashvilleFilter(context);

            //Beautify
            case BEAUTIFY_A:
                return new BeautifyFilterA(context);
            case BEAUTIFY_FU_B:
                return new BeautifyFilterFUB(context);
            case BEAUTIFY_FU_C:
                return new BeautifyFilterFUC(context);
            case BEAUTIFY_FU_D:
                return new BeautifyFilterFUD(context);
            case BEAUTIFY_FU_E:
                return new BeautifyFilterFUE(context);
            case BEAUTIFY_FU_F:
                return new BeautifyFilterFUF(context);
            default:
                return new PassThroughFilter(context);
        }
    }

    public static AbsFilter createFilterExt(FilterTypeExt filterTypeExt, Context context){
        switch (filterTypeExt){
            //Effects
            case SCALING:
                return new ScalingFilter(context);
            case GAUSSIAN_BLUR:
                return new GaussianBlurFilter(context);
            case BLURRED_FRAME:
                return new BlurredFrameEffect(context);
            case BOX_BLUR:
                return new CustomizedBoxBlurFilter(4);
            case FAST_BLUR:
                return new FastBlurFilter(context);
            case RANDOM_BLUR:
                return new RandomBlurFilter(context);
            default:
                return null;
        }
    }
}
