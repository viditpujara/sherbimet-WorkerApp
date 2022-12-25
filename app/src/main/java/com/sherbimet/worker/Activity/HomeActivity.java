package com.sherbimet.worker.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.navigation.NavigationView;
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.ProgressBarHandler;
import com.sherbimet.worker.Utils.ProgressDialogHandler;
import com.sherbimet.worker.Utils.UserConfirmationAlertDialog;
import com.sherbimet.worker.Utils.WorkerSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    WorkerSessionManager workerSessionManager;
    AtClass atClass;

    LinearLayout llHome, llError, llNoInternetConnection;

    CircleImageView civWorkerImage;
    TextView tvWorkerName;
    TextView tvUserRoleGroupName;
    ProgressBarHandler progressBarHandler;

    TextView tvGreeting;
    ProgressDialogHandler progressDialogHandler;


    TextView tvMessage;
    Button btnRetry;

    ImageView iVLogout;
    NavigationView navigationView;
    CircleImageView ciVHeaderWorkerImage;
    TextView tvHeaderWorkerName;

    CardView cVPending, cVAccepted, cVOngoing, cVCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        cVPending = findViewById(R.id.cVPending);
        cVPending.setOnClickListener(this);
        cVAccepted = findViewById(R.id.cVAccepted);
        cVAccepted.setOnClickListener(this);
        cVOngoing = findViewById(R.id.cVOngoing);
        cVOngoing.setOnClickListener(this);
        cVCompleted = findViewById(R.id.cVCompleted);
        cVCompleted.setOnClickListener(this);

        iVLogout = findViewById(R.id.iVLogout);
        iVLogout.setOnClickListener(this);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvMessage = findViewById(R.id.tvMessage);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        progressDialogHandler = new ProgressDialogHandler(this);


        llHome = findViewById(R.id.llHome);
        llError = findViewById(R.id.llError);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);

        atClass = new AtClass();
        workerSessionManager = new WorkerSessionManager(this);
        progressBarHandler = new ProgressBarHandler(this);


        civWorkerImage = findViewById(R.id.civWorkerImage);
        tvWorkerName = findViewById(R.id.tvWorkerName);
        tvUserRoleGroupName = findViewById(R.id.tvUserRoleGroupName);

        Toolbar toolbar = findViewById(R.id.tl_toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);

        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View mView = navigationView.getHeaderView(0);
        ciVHeaderWorkerImage = mView.findViewById(R.id.ciVHeaderWorkerImage);
        tvHeaderWorkerName = mView.findViewById(R.id.tvHeaderWorkerName);

        iVLogout = findViewById(R.id.iVLogout);
        iVLogout.setOnClickListener(this::onClick);


        if (atClass.isNetworkAvailable(this)) {
            getDashboardData();
        } else {
            llNoInternetConnection.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
            llHome.setVisibility(View.GONE);
        }
    }

    private void getDashboardData() {
        progressDialogHandler.showPopupProgressSpinner(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.DASHBOARD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseDashboardJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogHandler.showPopupProgressSpinner(false);
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llError.setVisibility(View.GONE);
                llHome.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.WORKERS_RESPONSE_WORKER_ID, workerSessionManager.getWorkerID());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(HomeActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(HomeActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                Log.d("params", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void parseDashboardJSON(String response) {
        Log.d("RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);
            if (flag == 1) {
                progressDialogHandler.showPopupProgressSpinner(false);

                String WorkerName = jsonObject.optString(JsonFields.DASHBOARD_RESPONSE_WORKER_NAME);
                String WorkerImageURL = jsonObject.optString(JsonFields.DASHBOARD_RESPONSE_WORKER_IMAGE_URL);
                String Greeting = jsonObject.optString(JsonFields.DASHBOARD_RESPONSE_WORKER_GREETING);


                llHome.setVisibility(View.VISIBLE);
                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);

                setDashboardData(WorkerName, WorkerImageURL, Greeting);
            } else if (flag == 3) {

            } else {
                progressDialogHandler.showPopupProgressSpinner(false);
                llHome.setVisibility(View.GONE);
                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);

                tvMessage.setText(Message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDashboardData(String workerName, String workerImageURL, String greeting) {
        Glide.with(HomeActivity.this).load(workerImageURL).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).error(R.drawable.ic_avatar).into(civWorkerImage);


        Glide.with(HomeActivity.this).load(workerImageURL).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).error(R.drawable.ic_avatar).into(ciVHeaderWorkerImage);


        if (workerName != null && !workerName.isEmpty() && !workerName.equals("")) {
            setDataToTextView(tvWorkerName.getId(), workerName);
        } else {
        }

        if (workerName != null && !workerName.isEmpty() && !workerName.equals("")) {
            setDataToTextView(tvHeaderWorkerName.getId(), workerName);
        } else {
        }

        if (greeting != null && !greeting.isEmpty() && !greeting.equals("")) {
            setDataToTextView(tvGreeting.getId(), greeting);
        } else {
        }

    }

    private void setDataToTextView(int TextViewID, String Text) {
        TextView textView = findViewById(TextViewID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(Text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(Text));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRetry:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iVLogout:
                if (atClass.isNetworkAvailable(this)) {
                    ShowLogoutDealerConfirmationAlert();
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cVPending:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, WorkerRequestsServiceActivity.class);
                    i.putExtra("StatusID", "1");
                    startActivity(i);
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cVAccepted:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, WorkerRequestsServiceActivity.class);
                    i.putExtra("StatusID", "2");
                    startActivity(i);
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cVOngoing:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, WorkerRequestsServiceActivity.class);
                    i.putExtra("StatusID", "3");
                    startActivity(i);
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cVCompleted:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, WorkerRequestsServiceActivity.class);
                    i.putExtra("StatusID", "4");
                    startActivity(i);
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_service_bookings:
                if (atClass.isNetworkAvailable(this)) {
                    /*Intent i = new Intent(this, WorkerBookingsStatusActivity.class);
                    startActivity(i);*/
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_service_requests:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i2 = new Intent(this, WorkerRequestsServiceActivity.class);
                    i2.setFlags(i2.FLAG_ACTIVITY_CLEAR_TASK | i2.FLAG_ACTIVITY_CLEAR_TOP | i2.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i2);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_profile:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i3 = new Intent(this, ProfileActivity.class);
                    startActivity(i3);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.nav_about:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i4 = new Intent(this, AboutUsActivity.class);
                    startActivity(i4);
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_logout:
                if (atClass.isNetworkAvailable(this)) {
                    ShowLogoutDealerConfirmationAlert();
                } else {
                    Toast.makeText(this, getString(R.string.home_no_internet_connection_toast), Toast.LENGTH_SHORT).show();
                }
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ShowLogoutDealerConfirmationAlert() {
        UserConfirmationAlertDialog userConfirmationAlertDialog = new UserConfirmationAlertDialog(this);
        userConfirmationAlertDialog.setTitle(getString(R.string.home_logout_title));
        //userConfirmationAlertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        userConfirmationAlertDialog.setMessage(getString(R.string.home_logout_description));
        userConfirmationAlertDialog.setPositiveButton(getString(R.string.home_logout_yes_button), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userConfirmationAlertDialog.dismiss();
                //Do want you want
                //LocaleManager.setNewLocale(this, "en")
                workerSessionManager.logout();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK | i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });


        userConfirmationAlertDialog.setNegativeButton(getString(R.string.home_logout_no_button), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userConfirmationAlertDialog.dismiss();
                //Do want you want

            }
        });

        userConfirmationAlertDialog.setCancelable(false);
        userConfirmationAlertDialog.setCanceledOnTouchOutside(false);
        userConfirmationAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userConfirmationAlertDialog.show();
        userConfirmationAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}