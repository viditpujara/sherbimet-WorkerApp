package com.sherbimet.worker.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.LocaleManager;
import com.sherbimet.worker.Utils.ProgressBarHandler;
import com.sherbimet.worker.Utils.SMSReceiver;
import com.sherbimet.worker.Utils.WorkerSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginOtpVerificationActivity extends BaseActivity implements View.OnClickListener, SMSReceiver.OTPReceiveListener {

    String MobileNumber, Otp;
    TextView tvMobileNumber, tvResendOTP, tvTimer, tvAllFieldsAreRequired;
    LinearLayout llResendOtp, llResendOtpTimer;
    TextInputEditText tieOtp1, tieOtp2, tieOtp3, tieOtp4, tieOtp5, tieOtp6;

    Button btnVerifyOTP;
    LinearLayout llVerifyOTP, llNoInternetConnection;
    Button btnRetry;

    String Message;

    AtClass atClass;
    WorkerSessionManager workerSessionManager;
    ProgressBarHandler progressBarHandler;

    SMSReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp_verification);

        workerSessionManager = new WorkerSessionManager(this);
        atClass = new AtClass();
        progressBarHandler = new ProgressBarHandler(this);

        llVerifyOTP = findViewById(R.id.llVerifyOTP);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        Intent i = getIntent();

        if (i.hasExtra("MobileNumber")) {
            MobileNumber = i.getStringExtra("MobileNumber");
        }

        if (i.hasExtra("Otp")) {
            Otp = i.getStringExtra("Otp");
        }

        tvMobileNumber = findViewById(R.id.tvMobileNumber);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        tvTimer = findViewById(R.id.tvTimer);
        tvAllFieldsAreRequired = findViewById(R.id.tvAllFieldsAreRequired);
        tvResendOTP.setOnClickListener(this);


        llResendOtp = findViewById(R.id.llResendOtp);
        llResendOtpTimer = findViewById(R.id.llResendOtpTimer);

        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        btnVerifyOTP.setOnClickListener(this);

        tieOtp1 = findViewById(R.id.tie_verify_mobile_number_otp1);
        tieOtp2 = findViewById(R.id.tie_verify_mobile_number_otp2);
        tieOtp3 = findViewById(R.id.tie_verify_mobile_number_otp3);
        tieOtp4 = findViewById(R.id.tie_verify_mobile_number_otp4);
        tieOtp5 = findViewById(R.id.tie_verify_mobile_number_otp5);
        tieOtp6 = findViewById(R.id.tie_verify_mobile_number_otp6);

        tvMobileNumber.setText("+91-" + MobileNumber);

        if (LocaleManager.getLanguagePref(this).equals("en")) {
            tvMobileNumber.setText("+91- " + MobileNumber);
        } else if (LocaleManager.getLanguagePref(this).equals("hi")) {
            String hindiMobile = LocaleManager.replaceEnglishToHindiNumbers(MobileNumber);
            tvMobileNumber.setText("+९१- " + hindiMobile);
        } else if (LocaleManager.getLanguagePref(this).equals("gu")) {
            String gujaratiMobile = LocaleManager.replaceEnglishToGujaratiNumbers(MobileNumber);
            tvMobileNumber.setText("+૯૧- " + gujaratiMobile);
        }
        setupTextChangeListerners();
        SetUpTimer();
        startSMSListener();
        showOtp();
    }

    private void showOtp() {
        if (LocaleManager.getLanguagePref(this).equals("en")) {
            atClass.showToast(this, Otp);
        } else if (LocaleManager.getLanguagePref(this).equals("hi")) {
            String hindiotp = LocaleManager.replaceEnglishToHindiNumbers(Otp);
            atClass.showToast(this, hindiotp);
        } else if (LocaleManager.getLanguagePref(this).equals("gu")) {
            String gujaratiotp = LocaleManager.replaceEnglishToGujaratiNumbers(Otp);
            atClass.showToast(this, gujaratiotp);
        }
    }

    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPReceived(String otp) {
        //showToast("OTP Received: " + otp);

        tieOtp1.setText("" + otp.charAt(0));
        tieOtp2.setText("" + otp.charAt(1));
        tieOtp3.setText("" + otp.charAt(2));
        tieOtp4.setText("" + otp.charAt(3));
        tieOtp5.setText("" + otp.charAt(4));
        tieOtp6.setText("" + otp.charAt(5));

        verifyMobileOTP();


        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onOTPTimeOut() {
        //showToast("OTP Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        //showToast(error);
    }

    private void SetUpTimer() {
        llResendOtp.setVisibility(View.GONE);
        llResendOtpTimer.setVisibility(View.VISIBLE);

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 <= 9) {
                    tvTimer.setText("00:" + "0" + millisUntilFinished / 1000);
                } else {
                    tvTimer.setText("00:" + millisUntilFinished / 1000);
                }

                if (millisUntilFinished / 1000 <= 9) {
                    if (LocaleManager.getLanguagePref(LoginOtpVerificationActivity.this).equals("en")) {
                        tvTimer.setText("00" + ":" + "0" + millisUntilFinished / 1000);

                    } else if (LocaleManager.getLanguagePref(LoginOtpVerificationActivity.this).equals("hi")) {
                        String hindiTimerString = LocaleManager.replaceEnglishToHindiNumbers(String.valueOf(millisUntilFinished / 1000));
                        tvTimer.setText("००" + ":" + "०" + hindiTimerString);
                    } else if (LocaleManager.getLanguagePref(LoginOtpVerificationActivity.this).equals("gu")) {
                        String gujaratiTimerString = LocaleManager.replaceEnglishToGujaratiNumbers(String.valueOf(millisUntilFinished / 1000));
                        tvTimer.setText("૦૦" + ":" + "૦" + gujaratiTimerString);
                    }
                } else {
                    if (LocaleManager.getLanguagePref(LoginOtpVerificationActivity.this).equals("en")) {
                        tvTimer.setText("00" + ":" + millisUntilFinished / 1000);
                    } else if (LocaleManager.getLanguagePref(LoginOtpVerificationActivity.this).equals("hi")) {
                        String hindiTimerString = LocaleManager.replaceEnglishToHindiNumbers(String.valueOf(millisUntilFinished / 1000));
                        tvTimer.setText("००:" + hindiTimerString);
                    } else if (LocaleManager.getLanguagePref(LoginOtpVerificationActivity.this).equals("gu")) {
                        String gujaratiTimerString = LocaleManager.replaceEnglishToGujaratiNumbers(String.valueOf(millisUntilFinished / 1000).toString());
                        tvTimer.setText("૦૦:" + gujaratiTimerString);
                    }
                }
            }

            public void onFinish() {
                Log.d("Timer", "Finished");
                llResendOtp.setVisibility(View.VISIBLE);
                llResendOtpTimer.setVisibility(View.GONE);
            }
        }.start();
    }

    private void setupTextChangeListerners() {
        tieOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 1) {
                    tieOtp2.requestFocus();
                    tieOtp1.setBackgroundResource(R.drawable.selected_edittext_bg);
                    if (tvAllFieldsAreRequired.getVisibility() == View.VISIBLE) {
                        tvAllFieldsAreRequired.setVisibility(View.GONE);
                    }
                } else {
                    tieOtp1.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                }
            }
        });

        tieOtp1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d("TAG", "OnKeyListener, premuto BACKSPACE");
                    tieOtp1.setText("");
                }
                return false;
            }
        });

        tieOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 1) {
                    tieOtp3.requestFocus();
                    tieOtp2.setBackgroundResource(R.drawable.selected_edittext_bg);
                    if (tvAllFieldsAreRequired.getVisibility() == View.VISIBLE) {
                        tvAllFieldsAreRequired.setVisibility(View.GONE);
                    }
                } else {
                    tieOtp2.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                }
            }
        });

        tieOtp2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d("TAG", "OnKeyListener, premuto BACKSPACE");
                    tieOtp2.setText("");
                    tieOtp2.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                    tieOtp1.requestFocus();
                }
                return false;
            }
        });


        tieOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 1) {
                    tieOtp4.requestFocus();
                    tieOtp3.setBackgroundResource(R.drawable.selected_edittext_bg);
                    if (tvAllFieldsAreRequired.getVisibility() == View.VISIBLE) {
                        tvAllFieldsAreRequired.setVisibility(View.GONE);
                    }
                } else {

                    tieOtp3.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                }

            }
        });


        tieOtp3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d("TAG", "OnKeyListener, premuto BACKSPACE");
                    tieOtp3.setText("");
                    tieOtp3.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                    tieOtp2.requestFocus();
                }
                return false;
            }
        });


        tieOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 1) {
                    tieOtp5.requestFocus();
                    tieOtp4.setBackgroundResource(R.drawable.selected_edittext_bg);
                    if (tvAllFieldsAreRequired.getVisibility() == View.VISIBLE) {
                        tvAllFieldsAreRequired.setVisibility(View.GONE);
                    }
                } else {

                    tieOtp4.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                }

            }
        });

        tieOtp4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d("TAG", "OnKeyListener, premuto BACKSPACE");
                    tieOtp4.setText("");
                    tieOtp4.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                    tieOtp3.requestFocus();
                }
                return false;
            }
        });

        tieOtp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tieOtp5.setBackgroundResource(R.drawable.selected_edittext_bg);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 1) {
                    tieOtp6.requestFocus();
                    tieOtp5.setBackgroundResource(R.drawable.selected_edittext_bg);
                    if (tvAllFieldsAreRequired.getVisibility() == View.VISIBLE) {
                        tvAllFieldsAreRequired.setVisibility(View.GONE);
                    }
                } else {
                    tieOtp5.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                }
            }
        });

        tieOtp5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d("TAG", "OnKeyListener, premuto BACKSPACE");
                    tieOtp5.setText("");
                    tieOtp5.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                    tieOtp4.requestFocus();

                }
                return false;
            }
        });

        tieOtp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 1) {
                    tieOtp6.setBackgroundResource(R.drawable.selected_edittext_bg);
                    if (tvAllFieldsAreRequired.getVisibility() == View.VISIBLE) {
                        tvAllFieldsAreRequired.setVisibility(View.GONE);
                    }
                } else {
                    tieOtp6.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                }
            }
        });

        tieOtp6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    Log.d("TAG", "OnKeyListener, premuto BACKSPACE");
                    tieOtp6.setText("");
                    tieOtp6.setBackgroundResource(R.drawable.not_selected_edittext_bg);
                    tieOtp5.requestFocus();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerifyOTP:
                boolean isOTP1Valid = checkOTP(tieOtp1);
                boolean isOTP2Valid = checkOTP(tieOtp2);
                boolean isOTP3Valid = checkOTP(tieOtp3);
                boolean isOTP4Valid = checkOTP(tieOtp4);
                boolean isOTP5Valid = checkOTP(tieOtp5);
                boolean isOTP6Valid = checkOTP(tieOtp6);

                if (isOTP1Valid && isOTP2Valid && isOTP3Valid && isOTP4Valid && isOTP5Valid && isOTP6Valid) {
                    if (atClass.isNetworkAvailable(this)) {
                        verifyMobileOTP();
                    } else {
                        llVerifyOTP.setVisibility(View.GONE);
                        llNoInternetConnection.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case R.id.tvResendOTP:
                if (atClass.isNetworkAvailable(this)) {
                    ResendOtp();
                } else {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llVerifyOTP.setVisibility(View.GONE);
                }
                break;

            case R.id.btnRetry:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, LoginOtpVerificationActivity.class);
                    i.putExtra("MobileNumber", MobileNumber);
                    i.putExtra("Otp", Otp);
                    startActivity(i);
                    finish();
                } else {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llVerifyOTP.setVisibility(View.GONE);
                }
                break;

        }
    }

    private void verifyMobileOTP() {
        progressBarHandler.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.VERIFY_OTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHandler.hide();
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llVerifyOTP.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.VERIFY_OTP_REQUEST_PARAM_WORKER_MOBILE, MobileNumber);
                String strOTP = tieOtp1.getText().toString() + tieOtp2.getText().toString() + tieOtp3.getText().toString() + tieOtp4.getText().toString() + tieOtp5.getText().toString() + tieOtp6.getText().toString();
                params.put(JsonFields.VERIFY_OTP_REQUEST_PARAM_WORKER_OTP, strOTP);
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(LoginOtpVerificationActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(LoginOtpVerificationActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void parseJSON(String response) {
        Log.d("RESPONSE", response.toString());
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            Message = jsonObject.optString(JsonFields.MESSAGE);

            if (flag == 1) {
                progressBarHandler.hide();
                String WorkerID = jsonObject.optString(JsonFields.VERIFY_OTP_RESPONSE_WORKER_ID);
                String WorkerName = jsonObject.optString(JsonFields.VERIFY_OTP_RESPONSE_WORKER_NAME);
                String WorkerMobile = jsonObject.optString(JsonFields.VERIFY_OTP_RESPONSE_WORKER_MOBILE);
                String WorkerImageURL = jsonObject.optString(JsonFields.VERIFY_OTP_RESPONSE_WORKER_IMAGE);

                workerSessionManager.setLoginStatus();
                workerSessionManager.setUserDetails(WorkerID, WorkerName, WorkerMobile, WorkerImageURL);

                Intent i = new Intent(this, SuccessMessageActivity.class);
                i.putExtra("Message", Message);
                i.putExtra("FromScreenName", "LoginOtpVerificationActivity");
                i.putExtra("ToScreenName", "HomeActivity");
                startActivity(i);
                finish();
            } else {
                progressBarHandler.hide();
                ClearTextOfEditTexts();
                Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ResendOtp() {
        progressBarHandler.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.RESEND_OTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseResendJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHandler.hide();
                if (error instanceof NetworkError) {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llVerifyOTP.setVisibility(View.GONE);
                } else if (error instanceof NoConnectionError) {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llVerifyOTP.setVisibility(View.GONE);
                } else if (error instanceof ServerError) {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llVerifyOTP.setVisibility(View.GONE);
                } else if (error instanceof TimeoutError) {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llVerifyOTP.setVisibility(View.GONE);
                } else {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llVerifyOTP.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.RESEND_OTP_REQUEST_PARAM_WORKER_MOBILE, MobileNumber);
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(LoginOtpVerificationActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(LoginOtpVerificationActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parseResendJSON(String response) {
        Log.d("RESPONSE", response.toString());
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            Message = jsonObject.optString(JsonFields.MESSAGE);

            if (flag == 1) {
                Otp = jsonObject.optString(JsonFields.COMMON_LOGIN_RESPONSE_OTP);

                progressBarHandler.hide();
                Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
                SetUpTimer();
                startSMSListener();
                showOtp();
            } else {
                progressBarHandler.hide();
                Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkOTP(TextInputEditText tieOtp) {
        boolean isOTPValid = false;
        if (tieOtp.getText().toString().trim().length() != 1) {
            tieOtp.setBackgroundResource(R.drawable.edittext_error_bg);
            tvAllFieldsAreRequired.setVisibility(View.VISIBLE);
        } else {
            tieOtp.setBackgroundResource(R.drawable.selected_edittext_bg);
            isOTPValid = true;
            tvAllFieldsAreRequired.setVisibility(View.GONE);
        }
        return isOTPValid;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }


    private void ClearTextOfEditTexts() {
        tieOtp1.setText("");
        tieOtp2.setText("");
        tieOtp3.setText("");
        tieOtp4.setText("");
        tieOtp5.setText("");
        tieOtp6.setText("");

        tieOtp1.setError(null);
        tieOtp2.setError(null);
        tieOtp3.setError(null);
        tieOtp4.setError(null);
        tieOtp5.setError(null);
        tieOtp6.setError(null);

    }
}
