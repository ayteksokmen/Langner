package com.ayteksokmen.langner.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import com.ayteksokmen.langner.R;


/**
 * Created by sem0025 on 25.02.2017.
 */

public class TextViewPlus extends AppCompatTextView {
	private static final String TAG = "TextView";

	public TextViewPlus(Context context) {
		super(context);
	}

	public TextViewPlus(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
	}

	public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
		String customFont = a.getString(R.styleable.TextViewPlus_customFont);
		setCustomFont(ctx, customFont);
		a.recycle();
	}

	public boolean setCustomFont(Context ctx, String asset) {
		Typeface typeface = null;
		try {
			typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
		} catch (Exception e) {
			Log.e(TAG, "Unable to load typeface: "+e.getMessage());
			return false;
		}

		setTypeface(typeface);
		return true;
	}
}