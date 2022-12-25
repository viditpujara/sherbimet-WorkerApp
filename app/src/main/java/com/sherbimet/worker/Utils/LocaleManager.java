package com.sherbimet.worker.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleManager {

    private static final String ENGLISH = "en";
    private static final String HINDI = "hi";
    private static final String GUJARATI = "gu";

    private static final String LANGUAGE_KEY = "language_key";

    // the method is used to set the language at runtime
    public static Context setLocale(Context context) {
        // updating the language for devices above android nougat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context,getLanguagePref(context));
        }
        // for devices having lower version of android os
        return updateResourcesLegacy(context, getLanguagePref(context));
    }

    public static void setLanguagePref(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, language);
        editor.apply();
    }

    public static String getLanguagePref(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(LANGUAGE_KEY, ENGLISH);
    }

    // the method is used update the language of application by creating
    // object of inbuilt Locale class and passing language argument to it
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }


    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }


    public static String replaceEnglishToHindiNumbers(CharSequence original) {
        if (original != null)
        {
            return original.toString().replace("0","०")
                    .replace("1","१")
                    .replace("2","२")
                    .replace("3","३")
                    .replace("4","४")
                    .replace("5","५")
                    .replace("6","६")
                    .replace("7","७")
                    .replace("8","८")
                    .replace("9","९");
        }
        return "";
    }

    public static String  replaceEnglishToGujaratiNumbers(CharSequence original) {
        if (original != null)
        {
            return original.toString().replace("0","૦")
                    .replace("1","૧")
                    .replace("2","૨")
                    .replace("3","૩")
                    .replace("4","૪")
                    .replace("5","૫")
                    .replace("6","૬")
                    .replace("7","૭")
                    .replace("8","૮")
                    .replace("9","૯");
        }
        return "";
    }

}