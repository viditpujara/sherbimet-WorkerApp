package com.sherbimet.worker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DashboardAreaManager  {
    private static final String MY_DASHBOARD_AREA_PREF = "user_dashboard_area_pref";
    private static final String AREA_ID = "area_id";
    private static final String AREA_NAME = "area_name";

    private final SharedPreferences.Editor editor;
    Context mContext;
    SharedPreferences preferences;

    public DashboardAreaManager(Context mContext) {
        this.mContext = mContext;
        preferences = mContext.getSharedPreferences(MY_DASHBOARD_AREA_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setAreaID(String AreaID) {
        editor.putString(AREA_ID, AreaID).apply();
    }

    public String getAreaID() {
        return preferences.getString(AREA_ID, "");
    }


    public void setAreaName(String AreaName) {
        editor.putString(AREA_NAME, AreaName).apply();
    }

    public String getAreaName() {
        return preferences.getString(AREA_NAME, "");
    }

    public void RemoveDashboardArea() {
        editor.remove(AREA_ID);
        editor.remove(AREA_NAME);
        editor.commit();
    }
}
