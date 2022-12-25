package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.BounceInterPolator;

public class SuccessMessageActivity extends BaseActivity {
    ImageView iVSuccessLogo;
    TextView tvSuccessMessage;
    String SuccessMessage;
    String FromScreenName, ToScreenName;
    String StatusID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_message);

        iVSuccessLogo = findViewById(R.id.iVSuccessLogo);
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);

        Intent i = getIntent();

        if (i.hasExtra("Message")) {
            SuccessMessage = i.getStringExtra("Message");
        }

        if (i.hasExtra("FromScreenName")) {
            FromScreenName = i.getStringExtra("FromScreenName");
        }

        if (i.hasExtra("ToScreenName")) {
            ToScreenName = i.getStringExtra("ToScreenName");
        }

        if (i.hasExtra("StatusID")) {
            StatusID = i.getStringExtra("StatusID");
        }

        setCheckAnimation();

        tvSuccessMessage.setText(SuccessMessage);

        if (FromScreenName != null && !FromScreenName.isEmpty() && !FromScreenName.equals("")
                && ToScreenName != null && !ToScreenName.isEmpty() && !ToScreenName.equals("")) {
            NavigateFromToScreen();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        //Intent intent = new Intent(SuccessMessageActivity.this,SelectAddressActivity.class);
                        //startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 1500);
        }
    }


    private void setCheckAnimation() {
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterPolator interpolator = new BounceInterPolator(0.2, 20.0);
        myAnim.setInterpolator(interpolator);
        myAnim.setDuration(1400);
        iVSuccessLogo.startAnimation(myAnim);
    }

    private void NavigateFromToScreen() {
        if (FromScreenName.equals("LoginOtpVerificationActivity") && ToScreenName.equals("HomeActivity")) {
            HandleClearAllScreenWithoutDataPassingNavigation(this, HomeActivity.class);
        }else if (FromScreenName.equals("ProfileActivity") && ToScreenName.equals("ProfileActivity")) {
            HandleScreenNavigationWithoutDataPassing(this, ProfileActivity.class);
        }else if (FromScreenName.equals("WorkerBookingsAdapter") && ToScreenName.equals("WorkerRequestsServiceActivity")) {
            HandleScreenNavigationWithDataPassing(this, WorkerRequestsServiceActivity.class,"StatusID",StatusID);
        }
    }

    private void HandleClearAllScreenWithoutDataPassingNavigation(final AppCompatActivity ActivityFrom, final Class<? extends AppCompatActivity> ActivityTo) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Intent intent = new Intent(ActivityFrom, ActivityTo);
                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);

    }

    private void HandleClearAllScreenWithDataPassingNavigation(final AppCompatActivity ActivityFrom, final Class<? extends AppCompatActivity> ActivityTo, final String Key, final String Value) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Intent intent = new Intent(ActivityFrom, ActivityTo);
                    intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Key, Value);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);

    }


    private void HandleScreenNavigationWithDataPassing(final AppCompatActivity ActivityFrom, final Class<? extends AppCompatActivity> ActivityTo, final String Key, final String Value) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Intent intent = new Intent(ActivityFrom, ActivityTo);
                    intent.putExtra(Key, Value);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);

    }


    private void HandleScreenNavigationWithThreeDataPassing(final AppCompatActivity ActivityFrom, final Class<? extends AppCompatActivity> ActivityTo, final String Key1, final String Value1, final String Key2, final String Value2, final String Key3, final String Value3) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Intent intent = new Intent(ActivityFrom, ActivityTo);
                    intent.putExtra(Key1, Value1);
                    intent.putExtra(Key2, Value2);
                    intent.putExtra(Key3, Value3);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);
    }


    private void HandleScreenNavigationWithDataPassing(final AppCompatActivity ActivityFrom, final Class<? extends AppCompatActivity> ActivityTo, final String Key1, final String Value1, final String Key2, final String Value2) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Intent intent = new Intent(ActivityFrom, ActivityTo);
                    intent.putExtra(Key1, Value1);
                    intent.putExtra(Key2, Value2);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);
    }

    private void HandleScreenNavigationWithoutDataPassing(final AppCompatActivity ActivityFrom, final Class<? extends AppCompatActivity> ActivityTo) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Intent intent = new Intent(ActivityFrom, ActivityTo);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);
    }


    private void HandleScreenNavigationAfterFinishing() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);

    }
}
