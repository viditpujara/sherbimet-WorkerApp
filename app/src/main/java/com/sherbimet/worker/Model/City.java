package com.sherbimet.worker.Model;

public class City {
    String CityID,CityName,CityNameInitials;

    public City(String cityID, String cityName, String cityNameInitials) {
        CityID = cityID;
        CityName = cityName;
        CityNameInitials = cityNameInitials;
    }

    public City() {
    }


    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getCityNameInitials() {
        return CityNameInitials;
    }

    public void setCityNameInitials(String cityNameInitials) {
        CityNameInitials = cityNameInitials;
    }
}
