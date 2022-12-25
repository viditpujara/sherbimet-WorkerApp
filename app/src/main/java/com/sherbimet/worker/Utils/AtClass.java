package com.sherbimet.worker.Utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sherbimet.worker.BuildConfig;
import com.sherbimet.worker.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class AtClass extends Application {
    String IPAddress;
    private static AtClass mInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        RefreshIPAddress(mInstance);
        FirebaseApp.initializeApp(this);

        /*final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, String.valueOf(BuildConfig.VERSION_NAME));
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id="+mInstance.getPackageManager());

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(10) // fetch every 10 seconds
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d("TAG", "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });*/
    }

    public String getRefreshedToken() {
        String refreshedDeviceToken = FirebaseInstanceId.getInstance().getToken();
        return refreshedDeviceToken;
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public String RefreshIPAddress(final Context context) {
        String stringUrl = "https://ipinfo.io/ip";
        //String stringUrl = "http://whatismyip.akamai.com/";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        //String url ="http://www.google.com";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, stringUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e("IP","GET IP : " + response);
                        IPAddress = response;
                        setRefreshedIpAddress(context,IPAddress);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IPAddress = "0.0.0.0";
                setRefreshedIpAddress(context,IPAddress);

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return IPAddress;
    }

    public void setRefreshedIpAddress(Context context, String ipAddress) {
        SharedPreferences prefs = context.getSharedPreferences("IP_ADDRESS_PREF", Context.MODE_PRIVATE);
        prefs.edit().putString("IP_ADDRESS", ipAddress).apply();
    }

    public String getRefreshedIpAddress(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("IP_ADDRESS_PREF", Context.MODE_PRIVATE);
        String refreshedIpAddress = prefs.getString("IP_ADDRESS", "0.0.0.0");
        return refreshedIpAddress;
    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
        Log.d("", "Out of memory");
    }

    public static synchronized AtClass getInstance() {
        return mInstance;
    }

    private String getAndroidSdkVersionNumber() {
        int sdkVersion = Build.VERSION.SDK_INT;
        return String.valueOf(sdkVersion);
    }

    public String getAppVersionName() {
        String versionRelease = BuildConfig.VERSION_NAME;
        return String.valueOf(versionRelease);
    }

    public String getAppName(Context context) {
        String AppName = context.getString(R.string.app_name);
        return AppName;
    }

    public String getOrderFrom() {
        return "3";
    }

    private String getAppVersionNumber() {
        String versionCodeRelease = String.valueOf(BuildConfig.VERSION_CODE);
        return String.valueOf(versionCodeRelease);
    }


    public String getAppVersionNumberAndVersionCode() {
        String versionNameRelease = String.valueOf(BuildConfig.VERSION_NAME);
        String versionCodeRelease = String.valueOf(BuildConfig.VERSION_CODE);
        return versionNameRelease+"|"+versionCodeRelease;
    }

    public String getAndroidVersionName() {
        int sdkVersion = Build.VERSION.SDK_INT;
        String release = Build.VERSION.RELEASE;

        String OsName = "Unknown Os";

        switch (sdkVersion) {
            case 11:
                OsName = "Honeycomb";
                break;

            case 12:
                OsName = "Honeycomb";
                break;

            case 13:
                OsName = "Honeycomb";
                break;

            case 14:
                OsName = "Ice Cream Sandwich";
                break;

            case 15:
                OsName = "Ice Cream Sandwich";
                break;

            case 16:
                OsName = "Jelly Bean";
                break;

            case 17:
                OsName = "Jelly Bean";
                break;

            case 18:
                OsName = "Jelly Bean";
                break;

            case 19:
                OsName = "KitKat";
                break;

            case 21:
                OsName = "Lollipop";
                break;

            case 22:
                OsName = "Lollipop";
                break;

            case 23:
                OsName = "Marshmallow";
                break;

            case 24:
                OsName = "Nougat";
                break;

            case 25:
                OsName = "Nougat";
                break;

            case 26:
                OsName = "Oreo";
                break;


            case 27:
                OsName = "Oreo";
                break;


            case 28:
                OsName = "Pie";
                break;

            case 29:
                OsName = "Android 10";
                break;

        }
        return OsName + "(" + release + ")";
    }

    private String getDeviceManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        return manufacturer;
    }

    private String getDeviceModel() {
        String model = Build.MODEL;
        return model;
    }

    public String getDeviceManufacturerModel() {
        String manufacturer = getDeviceManufacturer();
        String model = getDeviceModel();
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + "|" + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public String getDeviceType() {
        return "Android";
    }

    private String getDeviceDetailsInfo() {
        String DeviceDetailsInfo =  getDeviceManufacturerModel();
        return DeviceDetailsInfo;
    }

    public String getOsInfo() {
        String AndroidVersionName =  getAndroidVersionName();
        String AndroidSdkVersion =  getAndroidSdkVersionNumber();

        String AndroidOsInfo;

        if(AndroidVersionName!= null && !AndroidVersionName.isEmpty() &&!AndroidVersionName.equals(""))
        {
            AndroidOsInfo = AndroidVersionName;
        }else
        {
            AndroidOsInfo = "Unknown Os Version Name";
        }

        /*if(AndroidVersionNumber!= null && !AndroidVersionNumber.isEmpty() &&!AndroidVersionNumber.equals(""))
        {
            AndroidOsInfo = AndroidOsInfo+"|"+AndroidVersionNumber;
        }else
        {
            AndroidOsInfo = AndroidOsInfo+"|"+"Unknown Os Version Number";
        }*/

        if(AndroidSdkVersion!= null && !AndroidSdkVersion.isEmpty() &&!AndroidSdkVersion.equals(""))
        {
            AndroidOsInfo = AndroidOsInfo+"|"+AndroidSdkVersion;
        }else
        {
            AndroidOsInfo = AndroidOsInfo+"|"+"Unknown Os SDK Number";
        }
        return AndroidOsInfo;
    }

    public String getDeviceID(Context context)
    {
        String DeviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return DeviceID;
    }


    public void disablefor1sec(final View v) {
        try {
            v.setEnabled(false);
            v.setAlpha((float) 0.5);
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        v.setEnabled(true);
                        v.setAlpha((float) 1.0);
                    } catch (Exception e) {
                        Log.d("disablefor1sec", " Exception while un hiding the view : " + e.getMessage());
                    }
                }
            }, 1000);
        } catch (Exception e) {
            Log.d("disablefor1sec", " Exception while hiding the view : " + e.getMessage());
        }
    }


    public interface LogOutListener {
        void doLogout(boolean isForground);
    }

    static Timer longTimer;
    static final int LOGOUT_TIME = 900000; // delay in milliseconds i.e. 15 min = 900000 ms or use timeout argument

    public static synchronized void startLogoutTimer(final Context context, final LogOutListener logOutListener) {
        if (longTimer != null) {
            longTimer.cancel();
            longTimer = null;
        }
        if (longTimer == null) {

            longTimer = new Timer();

            longTimer.schedule(new TimerTask() {

                public void run() {
                    cancel();

                    longTimer = null;

                    try {
                        boolean foreGround = new ForegroundCheckTask().execute(context).get();

                        if (foreGround) {
                            logOutListener.doLogout(foreGround);
                        } else {
                            logOutListener.doLogout(foreGround);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            }, LOGOUT_TIME);
        }
    }

    public static synchronized void stopLogoutTimer() {
        if (longTimer != null) {
            longTimer.cancel();
            longTimer = null;
        }
    }

    static class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }


    public void showToast(Context context,String Message) {
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast_message_layout, null);

        TextView text1= layout.findViewById(R.id.text1);
        TextView text2= layout.findViewById(R.id.text2);

        ImageView image = layout.findViewById(R.id.imageView);

        image.setImageResource(R.drawable.ic_otp_toast);

        if (LocaleManager.getLanguagePref(context) == "en") {
            text1.setText("Your OTP is : ");
        } else if (LocaleManager.getLanguagePref(context) == "hi") {
            text1.setText("आपका ओटीपी है : ");
        } else if (LocaleManager.getLanguagePref(context) == "gu") {
            text1.setText("તમારો ઓટીપી છે : ");
        }

        text2.setText(Message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 40);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}