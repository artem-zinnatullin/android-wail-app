package com.artemzin.android.bytes.ui;

import android.view.View;
import android.view.ViewGroup;

/**
 * High effective util methods to control your views
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class ViewUtil {

    private ViewUtil() {}

    /**
     * Sets View.GONE or View.VISIBLE to the view visibility, this method is LAZY.
     * If current View's flag is equals to flag you want to set, it would not set it and View would
     * not be invalidated
     *
     * @param view
     *          object to show/hide, can be null
     * @param isVisible
     *          true to set View.VISIBLE flag, false to set View.GONE flag
     */
    public static void setVisibility(final View view, final boolean isVisible) {
        if (view == null) return;

        final int visibilityFlag = isVisible ? View.VISIBLE : View.GONE;

        if (view.getVisibility() != visibilityFlag) {
            view.setVisibility(visibilityFlag);
        }
    }

    /**
     * Recursively sets enabled flag for all views contained in viewGroup.
     * Very useful for something like settings screens, when you need to enable/disable all controls
     * inside a layout via Switch, RadioButton or CheckBox
     *
     * @param viewGroup
     *          which children will be enabled/disabled
     * @param isEnabled
     *          true or false to set
     */
    public static void setEnabledForAllChildrenRecursively(final ViewGroup viewGroup, final boolean isEnabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View child = viewGroup.getChildAt(i);
            
            if (child == null) continue;
            
            child.setEnabled(isEnabled);

            if (child instanceof ViewGroup) {
                setEnabledForAllChildrenRecursively((ViewGroup) child, isEnabled);
            }
        }
    }

    /**
     * Recursively sets selected flag for all views contained in viewGroup.
     * Very useful for something like settings screens, when you need to select/unselect all controls
     * inside a layout via Switch, RadioButton or CheckBox
     *
     * @param viewGroup
     *          which children will be selected/unselected
     * @param selected
     *          true or false to set
     */
    public static void setSelectedForAllChildrenRecursively(final ViewGroup viewGroup, final boolean selected) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View child = viewGroup.getChildAt(i);

            if (child == null) continue;

            child.setSelected(selected);

            if (child instanceof ViewGroup) {
                setSelectedForAllChildrenRecursively((ViewGroup) child, selected);
            }
        }
    }
}
