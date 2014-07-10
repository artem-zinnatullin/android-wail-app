package com.artemzin.android.bytes.ui;

import android.content.Context;
import android.content.res.Resources;

/**
 * Contains methods to convert dp to px, px to dp, sp to px and so on
 * Awesome!
 *
 * @see <a href="http://stackoverflow.com/questions/4605527/converting-pixels-to-dp">Converting pixels to dp on stackoverflow.com</a>
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class DisplayUnitsConverter {

    private DisplayUnitsConverter() {}

    //region with Context

    /**
     * Converts dp unit to equivalent pixels, depending on device density.
     *
     * @param context
     *          Context to get resources and device specific display metrics
     * @param dp
     *          A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPx(Context context, final float dp) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * Converts device specific pixels to density independent pixels.
     *
     * @param context
     *          Context to get resources and device specific display metrics
     * @param px
     *          A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(Context context, final float px) {
        return px / (context.getResources().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * Converts sp unit to equivalent pixels, depending on device density and user scale options
     *
     * @param context
     *          Context to get resources and device and user specific display metrics
     * @param sp
     *          A value in sp to convert to px
     * @return A float value to represent px equivalent to sp depending on device density and user's text scale options
     */
    public static float spToPx(Context context, final float sp) {
        return sp * (context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * Converts device specific pixels to density independent pixels * user's value of text scale
     *
     * @param context
     *          Context to get resources and device and user specific display metrics
     * @param px
     *          A value in px to convert to sp
     * @return A float value to represent sp equivalent to px depending on device density and user's text scale options
     */
    public static float pxToSp(Context context, final float px) {
        return px / (context.getResources().getDisplayMetrics().scaledDensity);
    }

    //endregion

    //region without Context

    /**
     * Converts dp unit to equivalent pixels, depending on device density.
     * Works without Context object
     *
     * @param dp
     *          A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPx(final float dp) {
        return dp * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * Converts device specific pixels to density independent pixels.
     * Works without Context object
     *
     * @param px
     *          A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(final float px) {
        return px / (Resources.getSystem().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * Converts sp unit to equivalent pixels, depending on device density and user scale options
     * Works without Context object
     *
     * @param sp
     *          A value in sp to convert to px
     * @return A float value to represent px equivalent to sp depending on device density and user's text scale options
     */
    public static float spToPx(final float sp) {
        return sp * (Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    /**
     * Converts device specific pixels to density independent pixels * user's value of text scale
     * Works without Context object
     *
     * @param px
     *          A value in px to convert to sp
     * @return A float value to represent sp equivalent to px depending on device density and user's text scale options
     */
    public static float pxToSp(final float px) {
        return px / (Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    //endregion
}
