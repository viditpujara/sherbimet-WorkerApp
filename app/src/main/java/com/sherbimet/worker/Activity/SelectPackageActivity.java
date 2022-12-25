package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;
import com.sherbimet.worker.Adapter.PackageAdapter;
import com.sherbimet.worker.Model.Package;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.ItemOffsetDecoration;
import com.sherbimet.worker.Utils.ProgressBarHandler;
import com.sherbimet.worker.Utils.WorkerSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectPackageActivity extends BaseActivity implements View.OnClickListener {

    WorkerSessionManager workerSessionManager;
    AtClass atClass;
    ProgressBarHandler progressBarHandler;

    LinearLayout llPackage, llError, llNoInternetConnection;

    RecyclerView rVPackage;

    TextView tvMessage, tvTitle;

    Button btnRetry;

    ImageView iVBack;

    boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    int currentPageValue = 1;
    LinearLayoutManager linearLayoutManager;

    ArrayList<Package> listPackage = new ArrayList<Package>();
    PackageAdapter packageAdapter = new PackageAdapter(listPackage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_package);

        workerSessionManager = new WorkerSessionManager(this);
        atClass = new AtClass();
        progressBarHandler = new ProgressBarHandler(this);

        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(this);

        rVPackage = findViewById(R.id.rVPackage);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset_large);
        rVPackage.addItemDecoration(itemDecoration);
        linearLayoutManager = new LinearLayoutManager(this);
        rVPackage.setLayoutManager(linearLayoutManager);
        rVPackage.setAdapter(packageAdapter);


        llPackage = findViewById(R.id.llPackage);
        llError = findViewById(R.id.llError);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);

        tvMessage = findViewById(R.id.tvMessage);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRetry:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, SelectPackageActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.sub_category_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iVBack:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        rVPackage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView,int dx,int dy){
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isScrolling && currentItems + scrollOutItems == totalItems){
                    isScrolling = false;
                    if (atClass.isNetworkAvailable(SelectPackageActivity.this)){
                        currentPageValue++;
                        getPackage();
                    } else{
                        llPackage.setVisibility(View.GONE);
                        llError.setVisibility(View.GONE);
                        llNoInternetConnection.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (atClass.isNetworkAvailable(SelectPackageActivity.this)){
            getPackage();
        } else{
            llPackage.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
            llNoInternetConnection.setVisibility(View.VISIBLE);

        }
    }

    private void getPackage() {
        progressBarHandler.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SELECT_PACKAGE_URL, new Response.Listener<String>() {
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
                llPackage.setVisibility(View.GONE);
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
                params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, String.valueOf(currentPageValue));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(SelectPackageActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(SelectPackageActivity.this));
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
            if (currentPageValue == 1) {
                listPackage.clear();
            }

            if (flag == 1) {
                progressBarHandler.hide();
                JSONArray arrPackage = jsonObject.optJSONArray(JsonFields.PACKAGE_RESPONSE_PACKAGE_ARRAY);
                if (arrPackage.length() > 0) {
                    for (int i = 0;i<arrPackage.length(); i++){
                        JSONObject packageObject = arrPackage.optJSONObject(i);
                        String packageID = packageObject.optString(JsonFields.PACKAGE_RESPONSE_PACKAGE_ID);
                        String packageName = packageObject.optString(JsonFields.PACKAGE_RESPONSE_PACKAGE_NAME);
                        String packageDescription = packageObject.optString(JsonFields.PACKAGE_RESPONSE_PACKAGE_DESCRIPTION);
                        String packageAmount = packageObject.optString(JsonFields.PACKAGE_RESPONSE_PACKAGE_AMOUNT);
                        String packageImage = packageObject.optString(JsonFields.PACKAGE_RESPONSE_PACKAGE_IMAGE);

                        listPackage.add(new Package(packageID, packageName, packageDescription,packageAmount,packageImage));
                    }
                    packageAdapter.notifyDataSetChanged();
                    llPackage.setVisibility(View.VISIBLE);
                    llNoInternetConnection.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);
                }
            } else if (flag == 2) {


            } else if (flag == 3) {
                progressBarHandler.hide();
                llPackage.setVisibility(View.VISIBLE);
                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
            } else {
                progressBarHandler.hide();
                llPackage.setVisibility(View.GONE);
                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                tvMessage.setText(Message);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
