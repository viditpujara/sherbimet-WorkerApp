package com.sherbimet.worker.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.sherbimet.worker.R;

public class ProgressBarHandler {
    ProgressBar mProgressBar;
    Context mContext;

    public ProgressBarHandler(Context mContext) {
        this.mContext = mContext;
        ViewGroup layout = (ViewGroup) ((Activity) mContext).findViewById(android.R.id.content).getRootView();
        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyle);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progress_bg));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.addView(mProgressBar);
        layout.addView(relativeLayout, layoutParams);
        hide();
    }

    public void show() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mProgressBar.setVisibility(View.GONE);
    }
}