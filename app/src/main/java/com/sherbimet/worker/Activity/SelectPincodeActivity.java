package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.sherbimet.worker.Adapter.PincodeAdapter;
import com.sherbimet.worker.Model.Pincode;
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

public class SelectPincodeActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<Pincode> listPincode = new ArrayList<Pincode>();
    PincodeAdapter pincodeAdapter;
    Pincode pincode;

    RecyclerView rvSelectPincode;
    EditText etSearch;

    LinearLayoutManager linearLayoutManager;

    ImageView iVBack;

    LinearLayout llSelectPincodeMaster, llNoInternetConnection;

    LinearLayout llSelectPincode;
    LinearLayout llError, llNoRecordFound;

    TextView tvMessage, tvNoRecordFoundMessage;
    String Message;

    boolean isScrolling = false;
    int currentItems = 0;
    int totalItems = 0;
    int scrollOutItems = 0;
    int currentPageValue = 1;
    int searchCurrentPageValue = 1;
    ProgressBarHandler progressBarHandler;
    WorkerSessionManager workerSessionManager;

    AtClass atClass;

    LinearLayout llPincode;

    Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pincode);

        llPincode = findViewById(R.id.llPincode);

        atClass = new AtClass();
        workerSessionManager = new WorkerSessionManager(this);

        pincodeAdapter = new PincodeAdapter(listPincode);

        progressBarHandler = new ProgressBarHandler(this);

        llSelectPincodeMaster = findViewById(R.id.llSelectPincodeMaster);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);

        llSelectPincode = findViewById(R.id.llSelectPincode);
        llError = findViewById(R.id.llError);
        llNoRecordFound = findViewById(R.id.llNoRecordsFound);

        tvMessage = findViewById(R.id.tvMessage);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);
        tvNoRecordFoundMessage = findViewById(R.id.tvNoRecordFoundMessage);

        etSearch = findViewById(R.id.etSearch);

        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(this);

        rvSelectPincode = findViewById(R.id.rvSelectPincode);
        etSearch = findViewById(R.id.etSearch);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset_medium);
        rvSelectPincode.addItemDecoration(itemDecoration);
        linearLayoutManager = new LinearLayoutManager(this);
        rvSelectPincode.setLayoutManager(linearLayoutManager);
        rvSelectPincode.setAdapter(pincodeAdapter);
        setTextChangeListeners();
    }


    private void setTextChangeListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isNetworkAvailable(SelectPincodeActivity.this)) {
                    llPincode.setVisibility(View.GONE);
                    llSelectPincodeMaster.setVisibility(View.GONE);
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                } else {
                    if (etSearch.getText().toString().trim() != null && etSearch.getText().toString().trim() != "" && etSearch.getText().toString().trim() != "") {
                        searchCurrentPageValue = 1;
                        getPincodeData(charSequence.toString(), "3");
                    } else {
                        searchCurrentPageValue = 1;
                        currentPageValue = 1;
                        getPincodeData("", "3");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (atClass.isNetworkAvailable(this)) {
            if (etSearch.getText().toString().trim() != null && !etSearch.getText().toString().trim().isEmpty() && etSearch.getText().toString().trim() != "") {
                searchCurrentPageValue = 1;
                getPincodeData(etSearch.getText().toString().trim(), "3");
            } else {
                setPagination();
            }
        } else {
            llNoInternetConnection.setVisibility(View.VISIBLE);
            llSelectPincodeMaster.setVisibility(View.GONE);
            llPincode.setVisibility(View.VISIBLE);
        }
    }

    private void setPagination() {
        rvSelectPincode.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (isScrolling && currentItems + scrollOutItems == totalItems) {
                    isScrolling = false;
                    if (isNetworkAvailable(SelectPincodeActivity.this)) {
                        currentPageValue++;
                        searchCurrentPageValue++;
                        getPincodeData("", "2");
                    } else {
                        llPincode.setVisibility(View.GONE);
                        llSelectPincodeMaster.setVisibility(View.GONE);
                        llNoInternetConnection.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        if (isNetworkAvailable(SelectPincodeActivity.this)) {
            getPincodeData("", "1");
        } else {
            llPincode.setVisibility(View.GONE);
            llSelectPincodeMaster.setVisibility(View.GONE);
            llNoInternetConnection.setVisibility(View.VISIBLE);
        }
    }

    private void getPincodeData(String SearchQuery, String Status) {
        progressBarHandler.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SELECT_PINCODE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSON(response, SearchQuery, Status);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHandler.hide();
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llSelectPincodeMaster.setVisibility(View.GONE);
                llPincode.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.PINCODE_REQUEST_PARAMS_PINCODE_SEARCH_QUERY, SearchQuery);
                params.put(JsonFields.WORKERS_RESPONSE_WORKER_ID, workerSessionManager.getWorkerID());
                if (SearchQuery != null && !SearchQuery.isEmpty() && SearchQuery != "") {
                    params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, String.valueOf(searchCurrentPageValue));
                } else {
                    if (Status == "3") {
                        params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, "1");
                    } else {
                        params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, String.valueOf(currentPageValue));
                    }
                }
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(SelectPincodeActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(SelectPincodeActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                Log.d("params", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response, String SearchQuery, String Status) {
        Log.d("RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            Message = jsonObject.optString(JsonFields.MESSAGE);
            if (flag == 1) {
                if (currentPageValue == 1) {
                    listPincode.clear();
                } else if (searchCurrentPageValue == 1 && Status == "3") {
                    listPincode.clear();
                }
                progressBarHandler.hide();
                JSONArray arrSelectPincode = jsonObject.optJSONArray(JsonFields.PINCODE_RESPONSE_PINCODE_ARRAY);
                if (arrSelectPincode.length() > 0) {
                    for (int i = 0; i < arrSelectPincode.length(); i++) {
                        JSONObject selectPincodeObject = arrSelectPincode.optJSONObject(i);
                        String pincodeID = selectPincodeObject.optString(JsonFields.PINCODE_RESPONSE_PINCODE_ID);
                        String pincode = selectPincodeObject.optString(JsonFields.PINCODE_RESPONSE_PINCODE);

                        listPincode.add(new Pincode(pincodeID, pincode));

                    }
                    pincodeAdapter.notifyDataSetChanged();
                    llSelectPincodeMaster.setVisibility(View.VISIBLE);
                    llNoInternetConnection.setVisibility(View.GONE);
                    llPincode.setVisibility(View.VISIBLE);
                    llSelectPincode.setVisibility(View.VISIBLE);
                    llNoRecordFound.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);
                } else {
                    llPincode.setVisibility(View.VISIBLE);
                    llSelectPincodeMaster.setVisibility(View.VISIBLE);
                    llNoInternetConnection.setVisibility(View.GONE);
                    llSelectPincode.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);
                    tvMessage.setText(getString(R.string.pincode_something_went_wrong_message));
                }
            } else if (flag == 2) {
            } else if (flag == 3) {
                progressBarHandler.hide();
                llPincode.setVisibility(View.VISIBLE);
                llSelectPincodeMaster.setVisibility(View.VISIBLE);
                llNoInternetConnection.setVisibility(View.GONE);
            } else {
                progressBarHandler.hide();
                llPincode.setVisibility(View.VISIBLE);
                llSelectPincodeMaster.setVisibility(View.VISIBLE);
                llNoInternetConnection.setVisibility(View.GONE);
                if (SearchQuery != null && !SearchQuery.isEmpty() && SearchQuery != "") {
                    llSelectPincode.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.VISIBLE);
                    llError.setVisibility(View.GONE);
                    tvNoRecordFoundMessage.setText(Message);
                } else {
                    llSelectPincode.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.GONE);
                    llError.setVisibility(View.VISIBLE);
                    tvMessage.setText(Message);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public Boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iVBack :
                onBackPressed();
                break;

            case R.id.btnRetry:
                if (isNetworkAvailable(this)) {
                    Intent i = new Intent(this, SelectPincodeActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    llPincode.setVisibility(View.VISIBLE);
                    llSelectPincodeMaster.setVisibility(View.GONE);
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

}