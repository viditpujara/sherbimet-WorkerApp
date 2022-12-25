package com.sherbimet.worker.APIHelper;

import android.util.Base64;
import java.util.HashMap;

public class WebAuthorization {
    public static HashMap<String, String> checkAuthentication() {
        String tokenname = "sherbimet";
        String tokenvalue = "sherbimet@998";
        String authentication = "Basic " + Base64.encodeToString(String.format("%s:%s", tokenname, tokenvalue).getBytes(), Base64.DEFAULT);
        HashMap<String, String> mapAuthentication = new HashMap<>();
        mapAuthentication.put(JsonFields.AUTHORIZATION_KEY, authentication);
        return mapAuthentication;
    }
}

