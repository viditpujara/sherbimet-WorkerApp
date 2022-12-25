package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.ProgressDialogHandler;
import com.sherbimet.worker.Utils.WorkerSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    ImageView iVBack;
    LinearLayout llAboutUs, llError, llNoInternetConnection;

    Button btnRetry;
    TextView tvMessage, tvAboutUs, tvFooter;

    ImageView iVAboutImage;

    AtClass atClass;
    WorkerSessionManager workerSessionManager;
    ProgressDialogHandler progressDialogHandler;

    String AboutDescription, AboutImage, AboutFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        tvAboutUs = findViewById(R.id.tvAboutUs);
        tvFooter = findViewById(R.id.tvFooter);
        iVAboutImage = findViewById(R.id.iVAboutImage);

        atClass = new AtClass();
        workerSessionManager = new WorkerSessionManager(this);
        progressDialogHandler = new ProgressDialogHandler(this);

        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(this);

        llAboutUs = findViewById(R.id.llAboutUs);
        llError = findViewById(R.id.llError);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        tvMessage = findViewById(R.id.tvMessage);

        if (atClass.isNetworkAvailable(this)) {
            getAboutUsData();
        } else {
            llAboutUs.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
            llNoInternetConnection.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRetry:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, AboutUsActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.about_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }

            case R.id.iVBack:
                onBackPressed();
                break;


        }
    }


    private void getAboutUsData() {
        progressDialogHandler.showPopupProgressSpinner(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.ABOUT_US_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogHandler.showPopupProgressSpinner(false);
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llError.setVisibility(View.GONE);
                llAboutUs.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.COMMON_REQUEST_PARAMS_WORKER_ID, workerSessionManager.getWorkerID());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(AboutUsActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(AboutUsActivity.this));
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
        Log.d("RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);
            if (flag == 1) {
                progressDialogHandler.showPopupProgressSpinner(false);

                AboutDescription = jsonObject.optString(JsonFields.ABOUT_US_RESPONSE_DESCRIPTION);
                AboutFooter = jsonObject.optString(JsonFields.ABOUT_US_RESPONSE_FOOTER);
                AboutImage = jsonObject.optString(JsonFields.ABOUT_US_RESPONSE_IMAGE);

                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
                llAboutUs.setVisibility(View.VISIBLE);

                setAboutUsData();
            } else {
                progressDialogHandler.showPopupProgressSpinner(false);
                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                tvMessage.setText(Message);
                llAboutUs.setVisibility(View.GONE);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setAboutUsData() {

        if (AboutDescription != null && !AboutDescription.isEmpty() && !AboutDescription.equals(""))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvAboutUs.setText(Html.fromHtml(AboutDescription, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvAboutUs.setText(Html.fromHtml(AboutDescription));
            }
        }

        if (AboutFooter != null && !AboutFooter.isEmpty() && !AboutFooter.equals(""))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvFooter.setText(Html.fromHtml(AboutFooter, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvFooter.setText(Html.fromHtml(AboutFooter));
            }
        }

        Glide.with(AboutUsActivity.this).load(AboutImage).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).error(R.drawable.ic_avatar).into(iVAboutImage);

    }
}