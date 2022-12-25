package com.sherbimet.worker.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WorkerSessionManager {
    private static final String WORKER_PREF = "worker_pref";
    private static final String KEY_IS_LOGIN = "IS_LOGIN";
    private final SharedPreferences.Editor editor;
    Context mContext;
    SharedPreferences preferences;

    public static final String WORKER_ID ="worker_id";
    public static final String WORKER_NAME ="worker_name";
    public static final String WORKER_MOBILE ="worker_mobile";
    public static final String WORKER_IMAGE_URL ="worker_image_url";
    public static final String WORKER_PROFILE_IMAGE_FILE_PATH ="worker_image_path";

    AtClass atClass;

    public WorkerSessionManager(Context mContext) {
        this.mContext = mContext;
        atClass = new AtClass();
        preferences = mContext.getSharedPreferences(WORKER_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setLoginStatus() {
        editor.putBoolean(KEY_IS_LOGIN, true).commit();
    }

    public boolean getLoginStatus() {
        return preferences.getBoolean(KEY_IS_LOGIN, false);
    }

    public void logout() {
        LogoutWorker();
    }

    public void setUserDetails(String worker_id,String worker_name,String worker_mobile,String worker_image_url) {
        editor.putString(WORKER_ID, worker_id);
        editor.putString(WORKER_NAME, worker_name);
        editor.putString(WORKER_MOBILE, worker_mobile);
        editor.putString(WORKER_IMAGE_URL, worker_image_url);
        editor.apply();
    }



    public void setPhotoURI(String workerImagePath) {
        editor.putString(WORKER_PROFILE_IMAGE_FILE_PATH, workerImagePath).apply();
    }

    public String getPhotoURI() {
        return preferences.getString(WORKER_PROFILE_IMAGE_FILE_PATH, "");
    }

    public String getWorkerID() {
        return preferences.getString(WORKER_ID, "");
    }
    public String getWorkerName() {
        return preferences.getString(WORKER_NAME, "");
    }
    public String getWorkerMobile() {
        return preferences.getString(WORKER_MOBILE, "");
    }
    public String getWorkerImageURL() { return preferences.getString(WORKER_IMAGE_URL, ""); }

    private void LogoutWorker() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.LOGOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseLogoutJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    RemoveSession();
                } else if (error instanceof NoConnectionError) {
                    RemoveSession();
                } else if (error instanceof ServerError) {
                    RemoveSession();
                } else if (error instanceof TimeoutError) {
                    RemoveSession();
                } else {
                    RemoveSession();
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
                params.put(JsonFields.COMMON_REQUEST_PARAMS_WORKER_ID, getWorkerID());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(mContext));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(mContext));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void parseLogoutJSON(String response) {
        Log.d("RESPONSE", response.toString());
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);

            if (flag == 1) {
                RemoveSession();
            } else {
                RemoveSession();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RemoveSession() {
        editor.remove(WORKER_ID);
        editor.remove(WORKER_NAME);
        editor.remove(WORKER_MOBILE);
        editor.remove(WORKER_IMAGE_URL);
        editor.remove(WORKER_PROFILE_IMAGE_FILE_PATH);
        editor.remove(KEY_IS_LOGIN);
        editor.commit();
    }



}
