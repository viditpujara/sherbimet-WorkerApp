package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.ProgressBarHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    TextInputEditText etMobile;
    TextInputEditText etCountryCode;
    AtClass atClass;
    ProgressBarHandler progressBarHandler;
    LinearLayout llLogin, llError, llNoInternetConnection;
    Button btnRetry, btnLogin;
    TextView tvMessage;
    String Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        llLogin = findViewById(R.id.llLogin);
        llError = findViewById(R.id.llError);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);

        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        tvMessage = findViewById(R.id.tvMessage);

        atClass = new AtClass();
        progressBarHandler = new ProgressBarHandler(this);

        btnLogin = findViewById(R.id.btnLogin);
        etMobile = findViewById(R.id.et_mobile);

        etCountryCode = findViewById(R.id.et_country_code);

        etCountryCode.setFocusableInTouchMode(false);
        etCountryCode.setFocusable(false);
        etCountryCode.setInputType(InputType.TYPE_NULL);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (atClass.isNetworkAvailable(this)) {
                    if (checkMobileNumber()) {
                        LoginUser();
                    }
                } else {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llError.setVisibility(View.GONE);
                    llLogin.setVisibility(View.GONE);
                }
                break;

            case R.id.btnRetry:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                } else {
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                    llError.setVisibility(View.GONE);
                    llLogin.setVisibility(View.GONE);
                }
                break;
        }
    }

    private Boolean checkMobileNumber() {
        boolean isMobileNumberValid = false;
        if (etMobile.getText().toString().trim().equals("")) {
            etMobile.setError(getString(R.string.login_mobile_empty_validation_error));
        } else {
            if (etMobile.getText().toString().trim().length() == 10) {
                isMobileNumberValid = true;
                etMobile.setError(null);
            } else {
                isMobileNumberValid = false;
                etMobile.setError(getString(R.string.login_mobile_invalid_validation_error));
            }
        }
        return isMobileNumberValid;
    }


    private void LoginUser() {
        progressBarHandler.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHandler.hide();
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llError.setVisibility(View.GONE);
                llLogin.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.LOGIN_REQUEST_PARAMS_MOBILE_NUMBER, etMobile.getText().toString().trim());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(LoginActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(LoginActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                Log.d("params", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void parseJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);

            if (flag == 1) {
                progressBarHandler.hide();
                String Mobile = jsonObject.optString(JsonFields.LOGIN_RESPONSE_MOBILE_NUMBER);
                String Otp = jsonObject.optString(JsonFields.COMMON_LOGIN_RESPONSE_OTP);

                if (Mobile != null && !Mobile.isEmpty() && !Mobile.equals("") &&
                        Otp != null && !Otp.isEmpty() && !Otp.equals("")) {
                    Intent i = new Intent(this, LoginOtpVerificationActivity.class);
                    i.putExtra("MobileNumber", Mobile);
                    i.putExtra("Otp", Otp);
                    startActivity(i);
                } else {
                    Toast.makeText(this, getString(R.string.login_something_went_wrong_toast), Toast.LENGTH_LONG).show();
                }
            } else if (flag == 3) {
                //User Is Deactivated By Admin
                progressBarHandler.hide();
                String LogoutTitle = jsonObject.optString(JsonFields.COMMON_LOGOUT_RESPONSE_TITLE);
                String LogoutMessage = jsonObject.optString(JsonFields.COMMON_LOGOUT_RESPONSE_MESSAGE);
                String LogoutIcon = jsonObject.optString(JsonFields.COMMON_LOGOUT_RESPONSE_ICON);

                /*Intent i = new Intent(this, LogoutActivity.class);
                i.putExtra("Title", LogoutTitle);
                i.putExtra("Message", LogoutMessage);
                i.putExtra("ImageURL", LogoutIcon);
                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK | i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();*/
                //ShowUserDisabledDialog(LogoutTitle, LogoutMessage, LogoutIcon);
            } else {
                progressBarHandler.hide();
                Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
                ResetInput();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ResetInput() {
        etMobile.setText("");
        etMobile.setError(null);
    }
}
