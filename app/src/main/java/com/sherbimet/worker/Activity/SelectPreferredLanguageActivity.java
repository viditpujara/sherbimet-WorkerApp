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
import com.sherbimet.worker.Adapter.PreferredLanguageAdapter;
import com.sherbimet.worker.Model.PreferredLanguage;
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

public class SelectPreferredLanguageActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<PreferredLanguage> listPreferredLanguage = new ArrayList<PreferredLanguage> ();
    PreferredLanguageAdapter preferredLanguageAdapter;
    PreferredLanguage preferredLanguage;

    RecyclerView rvSelectPreferredLanguage;
    EditText etSearch;

    LinearLayoutManager linearLayoutManager;

    ImageView iVBack;

    LinearLayout llSelectPreferredLanguageMaster;
    LinearLayout llNoInternetConnection;
    Button btnRetry;

    LinearLayout llSelectPreferredLanguage, llError, llNoRecordFound;

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

    LinearLayout llPreferredLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_preferred_language);

        llPreferredLanguage = findViewById(R.id.llPreferredLanguage);

        atClass = new AtClass();
        workerSessionManager = new WorkerSessionManager(this);

        preferredLanguageAdapter = new PreferredLanguageAdapter(listPreferredLanguage);

        progressBarHandler = new ProgressBarHandler(this);

        llSelectPreferredLanguageMaster = findViewById(R.id.llSelectPreferredLanguageMaster);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);

        llSelectPreferredLanguage = findViewById(R.id.llSelectPreferredLanguage);
        llError = findViewById(R.id.llError);
        llNoRecordFound = findViewById(R.id.llNoRecordsFound);

        tvMessage = findViewById(R.id.tvMessage);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);
        tvNoRecordFoundMessage = findViewById(R.id.tvNoRecordFoundMessage);

        etSearch = findViewById(R.id.etSearch);

        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(this);

        rvSelectPreferredLanguage = findViewById(R.id.rvSelectPreferredLanguage);
        etSearch = findViewById(R.id.etSearch);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset_medium);
        rvSelectPreferredLanguage.addItemDecoration(itemDecoration);
        linearLayoutManager = new LinearLayoutManager(this);
        rvSelectPreferredLanguage.setLayoutManager(linearLayoutManager);
        rvSelectPreferredLanguage.setAdapter(preferredLanguageAdapter);
        setTextChangeListeners();
    }


    private void setTextChangeListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isNetworkAvailable(SelectPreferredLanguageActivity.this)) {
                    llPreferredLanguage.setVisibility(View.GONE);
                    llSelectPreferredLanguageMaster.setVisibility(View.GONE);
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                } else {
                    if (etSearch.getText().toString().trim() != null && etSearch.getText().toString().trim() != "" && etSearch.getText().toString().trim() != "") {
                        searchCurrentPageValue = 1;
                        getPreferredLanguageData(charSequence.toString(), "3");
                    } else {
                        searchCurrentPageValue = 1;
                        currentPageValue = 1;
                        getPreferredLanguageData("", "3");
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
                getPreferredLanguageData(etSearch.getText().toString().trim(), "3");
            } else {
                setPagination();
            }
        } else {
            llNoInternetConnection.setVisibility(View.VISIBLE);
            llSelectPreferredLanguageMaster.setVisibility(View.GONE);
            llPreferredLanguage.setVisibility(View.VISIBLE);
        }
    }

    private void setPagination() {
        rvSelectPreferredLanguage.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (isNetworkAvailable(SelectPreferredLanguageActivity.this)) {
                        currentPageValue++;
                        searchCurrentPageValue++;
                        getPreferredLanguageData("", "2");
                    } else {
                        llPreferredLanguage.setVisibility(View.GONE);
                        llSelectPreferredLanguageMaster.setVisibility(View.GONE);
                        llNoInternetConnection.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        if (isNetworkAvailable(SelectPreferredLanguageActivity.this)) {
            getPreferredLanguageData("", "1");
        } else {
            llPreferredLanguage.setVisibility(View.GONE);
            llSelectPreferredLanguageMaster.setVisibility(View.GONE);
            llNoInternetConnection.setVisibility(View.VISIBLE);
        }
    }

    private void getPreferredLanguageData(String SearchQuery, String Status) {
        progressBarHandler.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.SELECT_PREFERRED_LANGUAGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSON(response, SearchQuery, Status);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarHandler.hide();
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llSelectPreferredLanguageMaster.setVisibility(View.GONE);
                llPreferredLanguage.setVisibility(View.GONE);
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
                params.put(JsonFields.PREFERRED_LANGUAGE_REQUEST_PARAMS_PREFERRED_LANGUAGE_SEARCH_QUERY, SearchQuery);
                if (SearchQuery != null && !SearchQuery.isEmpty() && SearchQuery != "") {
                    params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, String.valueOf(searchCurrentPageValue));
                } else {
                    if (Status == "3") {
                        params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, "1");
                    } else {
                        params.put(JsonFields.COMMON_REQUEST_PARAMS_CURRENT_PAGE, String.valueOf(currentPageValue));
                    }
                }
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(SelectPreferredLanguageActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(SelectPreferredLanguageActivity.this));
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
                    listPreferredLanguage.clear();
                } else if (searchCurrentPageValue == 1 && Status == "3") {
                    listPreferredLanguage.clear();
                }
                progressBarHandler.hide();
                JSONArray arrSelectPreferredLanguage = jsonObject.optJSONArray(JsonFields.PREFERRED_LANGUAGE_REQUEST_PARAMS_PREFERRED_LANGUAGE_ARRAY);
                if (arrSelectPreferredLanguage.length() > 0) {
                    for (int i = 0; i < arrSelectPreferredLanguage.length(); i++) {
                        JSONObject selectPreferredLanguageObject = arrSelectPreferredLanguage.optJSONObject(i);
                        String preferredLanguageID = selectPreferredLanguageObject.optString(JsonFields.PREFERRED_LANGUAGE_REQUEST_PARAMS_PREFERRED_LANGUAGE_ID);
                        String preferredLanguageName = selectPreferredLanguageObject.optString(JsonFields.PREFERRED_LANGUAGE_REQUEST_PARAMS_PREFERRED_LANGUAGE_NAME);
                        String preferredLanguageNameInitials = selectPreferredLanguageObject.optString(JsonFields.PREFERRED_LANGUAGE_REQUEST_PARAMS_PREFERRED_LANGUAGE_INITIALS);

                        listPreferredLanguage.add(new PreferredLanguage(preferredLanguageID, preferredLanguageName, preferredLanguageNameInitials));
                    }
                    preferredLanguageAdapter.notifyDataSetChanged();
                    llSelectPreferredLanguageMaster.setVisibility(View.VISIBLE);
                    llNoInternetConnection.setVisibility(View.GONE);
                    llPreferredLanguage.setVisibility(View.VISIBLE);
                    llSelectPreferredLanguage.setVisibility(View.VISIBLE);
                    llNoRecordFound.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);
                } else {
                    llPreferredLanguage.setVisibility(View.VISIBLE);
                    llSelectPreferredLanguageMaster.setVisibility(View.VISIBLE);
                    llNoInternetConnection.setVisibility(View.GONE);
                    llSelectPreferredLanguage.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.GONE);
                    llError.setVisibility(View.GONE);
                    tvMessage.setText(getString(R.string.preferred_language_something_went_wrong_message));
                }
            } else if (flag == 2) {
            } else if (flag == 3) {
                progressBarHandler.hide();
                llPreferredLanguage.setVisibility(View.VISIBLE);
                llSelectPreferredLanguageMaster.setVisibility(View.VISIBLE);
                llNoInternetConnection.setVisibility(View.GONE);
            } else {
                progressBarHandler.hide();
                llPreferredLanguage.setVisibility(View.VISIBLE);
                llSelectPreferredLanguageMaster.setVisibility(View.VISIBLE);
                llNoInternetConnection.setVisibility(View.GONE);
                if (SearchQuery != null && !SearchQuery.isEmpty() && SearchQuery != "") {
                    llSelectPreferredLanguage.setVisibility(View.GONE);
                    llNoRecordFound.setVisibility(View.VISIBLE);
                    llError.setVisibility(View.GONE);
                    tvNoRecordFoundMessage.setText(Message);
                } else {
                    llSelectPreferredLanguage.setVisibility(View.GONE);
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
            case R.id.iVBack:
                onBackPressed();
                break;
            case R.id.btnRetry:
                if (isNetworkAvailable(this)) {
                    Intent i = new Intent(this, SelectPreferredLanguageActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    llPreferredLanguage.setVisibility(View.VISIBLE);
                    llSelectPreferredLanguageMaster.setVisibility(View.GONE);
                    llNoInternetConnection.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}