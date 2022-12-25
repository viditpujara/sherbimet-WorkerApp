package com.sherbimet.worker.Model;

public class PreferredLanguage {
    String PreferredLanguageID,PreferredLanguageName,PreferredLanguageInitials;

    public PreferredLanguage(String preferredLanguageID, String preferredLanguageName, String preferredLanguageInitials) {
        PreferredLanguageID = preferredLanguageID;
        PreferredLanguageName = preferredLanguageName;
        PreferredLanguageInitials = preferredLanguageInitials;
    }

    public PreferredLanguage() {
    }

    public String getPreferredLanguageID() {
        return PreferredLanguageID;
    }

    public void setPreferredLanguageID(String preferredLanguageID) {
        PreferredLanguageID = preferredLanguageID;
    }

    public String getPreferredLanguageName() {
        return PreferredLanguageName;
    }

    public void setPreferredLanguageName(String preferredLanguageName) {
        PreferredLanguageName = preferredLanguageName;
    }

    public String getPreferredLanguageInitials() {
        return PreferredLanguageInitials;
    }

    public void setPreferredLanguageInitials(String preferredLanguageInitials) {
        PreferredLanguageInitials = preferredLanguageInitials;
    }
}
