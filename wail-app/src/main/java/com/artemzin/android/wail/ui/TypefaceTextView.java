package com.artemzin.android.wail.ui;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.artemzin.android.wail.R.styleable;

import java.util.HashMap;
import java.util.Map;

/**
 * Subclass of {@link TextView} that supports the <code>customTypeface</code> attribute from XML.
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
public class TypefaceTextView extends TextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created
     * every time when they are referenced.
     */
    private static Map<String, Typeface> mTypefaces;

    public TypefaceTextView(final Context context) {
        this(context, null);
    }

    public TypefaceTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypefaceTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, styleable.TypefaceTextView);

        if (array != null) {
            final String typefaceAssetPath = array.getString(styleable.TypefaceTextView_typefaceFromAssets);

            if (typefaceAssetPath != null) {
                setTypefaceFromAssets(typefaceAssetPath);
            } else {
                setTypefaceFromAssets("fonts/Roboto-Regular.ttf");
            }

            array.recycle();
        }
    }

    public void setTypefaceFromAssets(String typefaceAssetPath) {
        final Typeface typeface;

        if (mTypefaces.containsKey(typefaceAssetPath)) {
            typeface = mTypefaces.get(typefaceAssetPath);
        } else {
            AssetManager assets = getContext().getAssets();
            typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
            mTypefaces.put(typefaceAssetPath, typeface);
        }

        setTypeface(typeface);
    }

}