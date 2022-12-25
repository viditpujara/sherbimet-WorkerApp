package com.sherbimet.worker.Model;

public class Package {
    String PackageID,PackageName,PackageDescription,PackageAmount,PackageImage;

    public Package(String packageID, String packageName, String packageDescription, String packageAmount, String packageImage) {
        PackageID = packageID;
        PackageName = packageName;
        PackageDescription = packageDescription;
        PackageAmount = packageAmount;
        PackageImage = packageImage;
    }

    public Package() {
    }

    public String getPackageID() {
        return PackageID;
    }

    public void setPackageID(String packageID) {
        PackageID = packageID;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getPackageDescription() {
        return PackageDescription;
    }

    public void setPackageDescription(String packageDescription) {
        PackageDescription = packageDescription;
    }

    public String getPackageAmount() {
        return PackageAmount;
    }

    public void setPackageAmount(String packageAmount) {
        PackageAmount = packageAmount;
    }

    public String getPackageImage() {
        return PackageImage;
    }

    public void setPackageImage(String packageImage) {
        PackageImage = packageImage;
    }
}