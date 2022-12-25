package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.WorkerSessionManager;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

    TextView tvRetry;
    LinearLayout llNoInternetConnectionSplash;
    LinearLayout llSplash;

    AtClass atClass;
    WorkerSessionManager workerSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvRetry = findViewById(R.id.tvRetry);
        tvRetry.setOnClickListener(this);

        llNoInternetConnectionSplash = findViewById(R.id.llNoInternetConnectionSplash);

        atClass = new AtClass();
        workerSessionManager = new WorkerSessionManager(this);

        //tvLogo = findViewById(R.id.tvLogo);

        //tvLogo = findViewById(R.id.tvLogo);
        //ivLogo = findViewById(R.id.ivLogo)
        //ivLogoTagLine = findViewById(R.id.ivLogoTagLine)
        llSplash = findViewById(R.id.llSplash);

        AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
        animation.setDuration(2500);
        animation.setStartOffset(200);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        llSplash.startAnimation(animation);

        CheckInternet();
    }

    private void CheckInternet() {
        if (atClass.isNetworkAvailable(this)) {
            llNoInternetConnectionSplash.setVisibility(View.GONE);
            HandleScreenNavigation();
        } else {
            llNoInternetConnectionSplash.setVisibility(View.VISIBLE);
        }
    }

    private void HandleScreenNavigation() {
        if (workerSessionManager.getLoginStatus()) {
            NavigateToHomeScreen();
        } else {
            NavigateToLoginScreen();
        }
    }

    private void NavigateToLoginScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }

    private void NavigateToHomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRetry:
                if (atClass.isNetworkAvailable(this)) {
                    llNoInternetConnectionSplash.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(800);

                                if (workerSessionManager.getLoginStatus()) {
                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 800);
                } else {
                    llNoInternetConnectionSplash.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}