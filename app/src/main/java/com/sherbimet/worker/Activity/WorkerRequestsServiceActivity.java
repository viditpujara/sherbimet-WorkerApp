package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.sherbimet.worker.Adapter.WorkerRequestsAdapter;
import com.sherbimet.worker.Model.WorkerRequests;
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

public class WorkerRequestsServiceActivity extends BaseActivity implements View.OnClickListener {

    WorkerSessionManager workerSessionManager;
    AtClass atClass;
    ProgressBarHandler progressBarHandler;

    LinearLayout llRequests, llError, llNoInternetConnection;
    RecyclerView rVRequests;

    TextView tvMessage, tvTitle;

    Button btnRetry;

    ImageView iVBack;

    String AreaID;

    boolean isScrolling;
    int currentItems = 0;
    int totalItems = 0;
    int scrollOutItems = 0;
    int currentPageValue = 1;
    LinearLayoutManager linearLayoutManager;

    ArrayList<WorkerRequests> listRequests = new ArrayList<WorkerRequests> ();
    WorkerRequestsAdapter workerRequestsAdapter;

    String Title, StatusID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_requests_service);

        Intent i = getIntent();

        if (i.hasExtra("StatusID")) {
            StatusID = i.getStringExtra("StatusID");
        }


        workerRequestsAdapter = new WorkerRequestsAdapter(listRequests, StatusID);

        workerSessionManager = new WorkerSessionManager(this);
        atClass = new AtClass();
        progressBarHandler = new ProgressBarHandler(this);

        tvTitle = findViewById(R.id.tvTitle);
        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(this);

        rVRequests = findViewById(R.id.rVRequests);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset_large);
        rVRequests.addItemDecoration(itemDecoration);
        linearLayoutManager = new LinearLayoutManager(this);
        rVRequests.setLayoutManager(linearLayoutManager);
        rVRequests.setAdapter(workerRequestsAdapter);


        llRequests = findViewById(R.id.llRequests);
        llError = findViewById(R.id.llError);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);

        tvMessage = findViewById(R.id.tvMessage);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        if (StatusID.equals("1")) {
            tvTitle.setText(getString(R.string.worker_request_pending_toolbar_title_text));
        } else if (StatusID.equals("2")) {
            tvTitle.setText(getString(R.string.worker_request_accepted_toolbar_title_text));
        } else if (StatusID.equals("3")) {
            tvTitle.setText(getString(R.string.worker_request_ongoing_toolbar_title_text));
        } else if (StatusID.equals("4")) {
            tvTitle.setText(getString(R.string.worker_request_completed_toolbar_title_text));
        } else {
            tvTitle.setText(getString(R.string.worker_request_general_toolbar_title_text));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRetry:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, WorkerRequestsServiceActivity.class);
                    i.putExtra("StatusID", StatusID);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.worker_request_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }

            case R.id.iVBack:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        rVRequests.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView,int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && currentItems + scrollOutItems == totalItems){
                    isScrolling = false;
                    if (atClass.isNetworkAvailable(WorkerRequestsServiceActivity.this)){
                        currentPageValue++;
                        getReqeusts();
                    } else{
                        llRequests.setVisibility(View.GONE);
                        llError.setVisibility(View.GONE);
                        llNoInternetConnection.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (atClass.isNetworkAvailable(WorkerRequestsServiceActivity.this)){
            getReqeusts();
        } else{
            llRequests.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
            llNoInternetConnection.setVisibility(View.VISIBLE);

        }
    }

    private void getReqeusts() {
        progressBarHandler.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.WORKERS_REQUESTS_URL, new Response.Listener<String>() {
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
                llRequests.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.WORKER_REQUESTS_REQUEST_PARAMS_STATUS_ID, StatusID);
                params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, String.valueOf(currentPageValue));
                params.put(JsonFields.COMMON_REQUEST_PARAMS_WORKER_ID, workerSessionManager.getWorkerID());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(WorkerRequestsServiceActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(WorkerRequestsServiceActivity.this));
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
                listRequests.clear();
            }

            if (flag == 1) {
                progressBarHandler.hide();
                JSONArray arrRequests = jsonObject.optJSONArray(JsonFields.WORKER_REQUESTS_RESPONSE_REQUESTS_ARRAY);
                if (arrRequests.length() > 0) {
                    for (int i = 0; i< arrRequests.length(); i++){
                        JSONObject requetsObject = arrRequests.optJSONObject(i);
                        String RequestID = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_ID);
                        String RequestDateTime = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_DATE_TIME);
                        String RequestAddress = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_ADDRESS);
                        String RequestMessage = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_MESSAGE);
                        String RequestTotalAmount = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_TOTAL_AMOUNT);
                        String UserID = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_USER_ID);
                        String UserImage = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_USER_IMAGE);
                        String UserName = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_USER_NAME);
                        String UserGender = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_USER_GENDER);
                        String UserMobile = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_USER_MOBILE);
                        String UserEmail = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_USER_EMAIL);
                        String RequestStatusID = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_STATUS_ID);
                        String RequestStatus = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_STATUS);
                        String SubCategory = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_SUB_CATEGORY);
                        String CanAccept = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_CAN_ACCEPT);
                        String CanFeedback = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_CAN_FEEDBACK);
                        String FeedbackRatings = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_FEEDBACK_RATINGS);
                        String FeedbackMessage = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_FEEDBACK_MESSAGE);
                        String PaymentMethod = requetsObject.optString(JsonFields.WORKER_REQUESTS_RESPONSE_REQUEST_PAYMENT_METHOD);

                        listRequests.add(new WorkerRequests(RequestID,RequestDateTime,RequestAddress,RequestMessage,RequestTotalAmount,UserID,UserImage,UserName,UserGender,UserMobile,UserEmail,RequestStatusID,RequestStatus,SubCategory,CanAccept,CanFeedback,FeedbackRatings,FeedbackMessage,PaymentMethod));
                    }
                    workerRequestsAdapter.notifyDataSetChanged();
                    llRequests.setVisibility(View.VISIBLE);
                    llNoInternetConnection.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);

                }
            } else if (flag == 2) {


            } else if (flag == 3) {
                progressBarHandler.hide();
                llRequests.setVisibility(View.VISIBLE);
                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
            } else {
                progressBarHandler.hide();
                llRequests.setVisibility(View.GONE);
                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                tvMessage.setText(Message);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
