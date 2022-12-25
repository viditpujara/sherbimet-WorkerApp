package com.sherbimet.worker.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.BaseActivity;
import com.sherbimet.worker.Utils.LocaleManager;
import com.sherbimet.worker.Utils.ProgressDialogHandler;
import com.sherbimet.worker.Utils.WorkerSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    CircleImageView civWorkerDrawable;
    TextView tvWorkerName;

    TextInputEditText etFirstName, etMiddleName, etLastName, etEmail, etCountryCode, etMobile, etPreferredLanguage, etAddressLine1, etAddressLine2, etCity, etPincode, etDateOfBirth, etAadharNumber, etPackage;

    RadioButton rbMale, rbFemale;
    Button btnSaveProfileDetails;

    ImageView iVBack;

    AtClass atClass;
    ProgressDialogHandler progressDialogHandler1, progressDialogHandler2, progressDialogHandler3;

    WorkerSessionManager workerSessionManager;
    String Gender;

    LinearLayout llProfile, llError, llNoInternetConnection;

    Button btnRetry;
    TextView tvMessage;

    String WorkerFirstName, WorkerMiddleName, WorkerLastName, WorkerGender, WorkerEmail, WorkerMobileNumber, WorkerImage, WorkerAddressLine1, WorkerAddressLine2, WorkerBirhtDate, WorkerAadharNumber, ChoosedOption;

    private static int PICK_IMAGE_REQUEST = 1;
    private static int CAMERA_REQUEST = 2;
    private static int REQUEST_CAMERA = 3;
    private static int REQUEST_STORAGE = 4;
    private static int CHOOSE_PREFERRED_LANGUAGE_REQUEST = 5;
    private static int CHOOSE_CITY_REQUEST = 6;
    private static int CHOOSE_PINCODE_REQUEST = 7;
    private static int CHOOSE_PACKAGE_REQUEST = 8;


    private String mCameraFileName;
    private String imagePath;

    String preferredLanguageID, preferredLanguageTitle;

    String cityID, cityTitle;

    String pincodeID, pincodeTitle;

    String packageID, packageTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        atClass = new AtClass();
        progressDialogHandler1 = new ProgressDialogHandler(this);
        progressDialogHandler2 = new ProgressDialogHandler(this);
        progressDialogHandler3 = new ProgressDialogHandler(this);

        workerSessionManager = new WorkerSessionManager(this);


        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(this);

        llProfile = findViewById(R.id.llProfile);
        llError = findViewById(R.id.llError);
        llNoInternetConnection = findViewById(R.id.llNoInternetConnection);
        btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);

        tvMessage = findViewById(R.id.tvMessage);

        civWorkerDrawable = findViewById(R.id.civWorkerDrawable);
        civWorkerDrawable.setOnClickListener(this);
        tvWorkerName = findViewById(R.id.tvWorkerName);

        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);

        etEmail = findViewById(R.id.etEmail);
        etCountryCode = findViewById(R.id.etCountryCode);

        etCountryCode.setFocusableInTouchMode(false);
        etCountryCode.setFocusable(false);
        etCountryCode.setInputType(InputType.TYPE_NULL);

        etMobile = findViewById(R.id.etMobile);
        etMobile.setFocusableInTouchMode(false);
        etMobile.setFocusable(false);
        etMobile.setInputType(InputType.TYPE_NULL);

        etPreferredLanguage = findViewById(R.id.etPreferredLanguage);
        etPreferredLanguage.setFocusableInTouchMode(false);
        etPreferredLanguage.setFocusable(false);
        etPreferredLanguage.setInputType(InputType.TYPE_NULL);
        etPreferredLanguage.setOnClickListener(this);

        etAddressLine1 = findViewById(R.id.etAddressLine1);
        etAddressLine2 = findViewById(R.id.etAddressLine2);
        etCity = findViewById(R.id.etCity);
        etCity.setFocusableInTouchMode(false);
        etCity.setFocusable(false);
        etCity.setInputType(InputType.TYPE_NULL);
        etCity.setOnClickListener(this);

        etPincode = findViewById(R.id.etPincode);
        etPincode.setFocusableInTouchMode(false);
        etPincode.setFocusable(false);
        etPincode.setInputType(InputType.TYPE_NULL);
        etPincode.setOnClickListener(this);

        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etDateOfBirth.setFocusableInTouchMode(false);
        etDateOfBirth.setFocusable(false);
        etDateOfBirth.setInputType(InputType.TYPE_NULL);
        etDateOfBirth.setOnClickListener(this);


        etPackage = findViewById(R.id.etPackage);
        etPackage.setFocusableInTouchMode(false);
        etPackage.setFocusable(false);
        etPackage.setInputType(InputType.TYPE_NULL);
        etPackage.setOnClickListener(this);


        etAadharNumber = findViewById(R.id.etAadharNumber);

        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        btnSaveProfileDetails = findViewById(R.id.btnSaveProfileDetails);
        btnSaveProfileDetails.setOnClickListener(this);


        rbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    rbMale.setChecked(true);
                    rbFemale.setChecked(false);
                    Gender = "Male";
                    Log.d("Gender", Gender);
                }
            }
        });

        rbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    rbFemale.setChecked(true);
                    rbMale.setChecked(false);
                    Gender = "Female";
                    Log.d("Gender", Gender);
                }
            }
        });

        if (atClass.isNetworkAvailable(this)) {
            getProfileData();
        } else {
            llNoInternetConnection.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
            llProfile.setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRetry:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, ProfileActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iVBack:
            onBackPressed();
            break;

            case R.id.btnSaveProfileDetails:
                if (atClass.isNetworkAvailable(this)) {
                    if (checkWorkerFirstName() && checkWorkerMiddleName() && checkWorkerLastName() && checkWorkerEmail() && checkWorkerGender() && checkPreferredLanguage() && checkAddressLine1() && checkAddressLine2() && checkCity() && checkPincode() && checkDateOfBirth() && checkWorkerAadharNumber() && checkPackage()) {
                        SaveProfileChanges();
                    }
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.profile_no_internet_toast_text),
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;

            case R.id.civWorkerDrawable:
                if (atClass.isNetworkAvailable(this)) {
                    ShowOptionCooserBottomSheetDialog();
                } else {
                    Toast.makeText(this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.etPreferredLanguage:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, SelectPreferredLanguageActivity.class);
                    startActivityForResult(i, CHOOSE_PREFERRED_LANGUAGE_REQUEST);
                } else {
                    Toast.makeText(this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.etCity:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, SelectCityActivity.class);
                    startActivityForResult(i, CHOOSE_CITY_REQUEST);
                } else {
                    Toast.makeText(this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.etPincode:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, SelectPincodeActivity.class);
                    startActivityForResult(i, CHOOSE_PINCODE_REQUEST);
                } else {
                    Toast.makeText(this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.etDateOfBirth:
                if (atClass.isNetworkAvailable(this)) {
                    ShowDateOfBirthDatePickerDialog();
                } else {
                    Toast.makeText(this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.etPackage:
                if (atClass.isNetworkAvailable(this)) {
                    Intent i = new Intent(this, SelectPackageActivity.class);
                    startActivityForResult(i, CHOOSE_PACKAGE_REQUEST);
                } else {
                    Toast.makeText(this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Boolean checkPackage() {
        boolean isCategoryValid = false;
        if (etPackage.getText().toString().trim().equals("")) {
            etPackage.setError(getString(R.string.profile_package_validation_error_text));
            isCategoryValid = false;
        } else {
            isCategoryValid = true;
            etPackage.setError(null);
        }
        return isCategoryValid;
    }

    private Boolean checkWorkerAadharNumber() {
        boolean isAadharNumberValid = false;
        if (etAadharNumber.getText().toString().equals("")) {
            etAadharNumber.setError("");
        } else {
            etAadharNumber.setError(null);
            if (etAadharNumber.getText().toString().trim().length() == 12) {
                isAadharNumberValid = true;
                etAadharNumber.setError(null);
            } else {
                isAadharNumberValid = false;
                etAadharNumber.setError(getString(R.string.profile_aadhar_validation_error_text));
            }
        }
        return isAadharNumberValid;
    }

    private Boolean checkDateOfBirth() {
        boolean isDOBValid = false;
        if (etDateOfBirth.getText().toString().trim().equals("")) {
            etDateOfBirth.setError(getString(R.string.profile_empty_dob_validation_error_text));
            isDOBValid = false;
        } else {
            isDOBValid = true;
            etDateOfBirth.setError(null);
        }
        return isDOBValid;
    }

    private void ShowDateOfBirthDatePickerDialog() {
        final Calendar c2 = Calendar.getInstance();
        int mYear = c2.get(Calendar.YEAR);
        int mMonth = c2.get(Calendar.MONTH);
        int mDay = c2.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                String mt, dy;
                if (monthOfYear < 10)
                    mt = "0" + monthOfYear;
                else mt = String.valueOf(monthOfYear);

                if (dayOfMonth < 10)
                    dy = "0" + dayOfMonth;
                else dy = String.valueOf(dayOfMonth);

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                String DateOfBirth = dy + "-" + mt + "-" + year;

                int age = calculateAgeFromDob(DateOfBirth, "dd-mm-yyyy");

                if (age > 14) {
                    etDateOfBirth.setText(DateOfBirth);
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.profile_dob_validation_error_text), Toast.LENGTH_SHORT).show();
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    public int calculateAgeFromDob(String birthDate, String dateFormat){
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            date = sdf.parse(birthDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private void ShowOptionCooserBottomSheetDialog() {
        ChoosedOption = "";
        View mView = LayoutInflater.from(this).inflate(R.layout.select_capture_image_bottom_sheet_layout, null, false);
        BottomSheetDialog selectOptionDialog = new BottomSheetDialog(this);
        selectOptionDialog.setContentView(mView);


        ImageView ivClose = mView.findViewById(R.id.ivClose);

        CardView cvChooseOptionFromGallery = mView.findViewById(R.id.cvChooseOptionFromGallery);
        CardView cvOpenCameraAndCaptureImage = mView.findViewById(R.id.cvOpenCameraAndCaptureImage);

        ImageView iVChooseOptionFromGallery = mView.findViewById(R.id.iVChooseOptionFromGallery);
        ImageView iVOpenCameraAndCaptureImage = mView.findViewById(R.id.iVOpenCameraAndCaptureImage);

        Button btnSelectOptionAndProceed = mView.findViewById(R.id.btnSelectOptionAndProceed);

        cvChooseOptionFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosedOption = "Gallery";
                iVChooseOptionFromGallery.setVisibility(View.VISIBLE);
                iVOpenCameraAndCaptureImage.setVisibility(View.INVISIBLE);
            }
        });

        cvOpenCameraAndCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosedOption = "Camera";
                iVChooseOptionFromGallery.setVisibility(View.INVISIBLE);
                iVOpenCameraAndCaptureImage.setVisibility(View.VISIBLE);
            }
        });


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionDialog.dismiss();
            }
        });

        btnSelectOptionAndProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ChoosedOption != null && !ChoosedOption.isEmpty() && ChoosedOption != "") {
                    if (ChoosedOption == "Gallery") {
                        selectOptionDialog.dismiss();
                        if (checkStoragePermissions()) {
                            pickImage();
                        }

                    } else if (ChoosedOption == "Camera") {
                        selectOptionDialog.dismiss();
                        if (checkCameraPermissions()) {
                            takePicture();
                        }
                    } else {
                        selectOptionDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, getString(R.string.profile_something_went_wrong_toast_text), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.profile_please_select_one_option_toast_text), Toast.LENGTH_SHORT).show();
                }
            }
        });
        selectOptionDialog.show();
    }


    private Boolean checkCameraPermissions() {
        boolean isPermissionValid = false;
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            isPermissionValid = false;
        } else {
            isPermissionValid = true;
        }
        return isPermissionValid;
    }

    private Boolean checkStoragePermissions() {
        boolean isPermissionValid = false;
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            isPermissionValid = false;
        } else {
            isPermissionValid = true;
        }
        return isPermissionValid;
    }


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void takePicture() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("-mm-ss");

            File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String strPath = storageDirectory.getPath() + "/" + "SherbimetWorker";
            storageDirectory = new File(strPath);
            storageDirectory.mkdirs();
            strPath = storageDirectory.getPath() + "/" + "user_image" + df.format(date) + ".jpg";
            storageDirectory = new File(strPath);
            mCameraFileName = storageDirectory.getPath();
            workerSessionManager.setPhotoURI(mCameraFileName);
            Uri outuri = Uri.fromFile(storageDirectory);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            String photoURI = workerSessionManager.getPhotoURI();
            if (photoURI != null) {
                imagePath = photoURI;
                Log.d("IMAGE_PATH", imagePath);

                if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("")) {
                    uploadWorkerImage();
                } else {
                    Toast.makeText(this, getString(R.string.profile_something_went_wrong__try_again_toast_text), Toast.LENGTH_SHORT).show();
                }
                //setImage(imagePath)
            } else {
                Toast.makeText(this, getString(R.string.profile_capture_uri_not_found_toast_text), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(projection[0]);
                imagePath = cursor.getString(index);
                Log.d("IMAGE_PATH", imagePath);

                if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("")) {
                    uploadWorkerImage();
                } else {
                    Toast.makeText(this, getString(R.string.profile_something_went_wrong__try_again_toast_text), Toast.LENGTH_SHORT).show();
                }
                //setImage(imagePath)
            }

        } else if (requestCode == CHOOSE_CITY_REQUEST && resultCode == Activity.RESULT_OK) {
            cityID = data.getStringExtra("cityID");
            cityTitle = data.getStringExtra("cityTitle");

            if (cityID != null && !cityID.isEmpty() && !cityID.equals("")
                    && cityTitle != null && !cityTitle.isEmpty() && !cityTitle.equals("")) {
                etCity.setText(cityTitle);
            } else {
                cityID = "";
                cityTitle = "";
                Toast.makeText(this, getString(R.string.profile_city_selection_error_toast_text), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHOOSE_PINCODE_REQUEST && resultCode == Activity.RESULT_OK) {
            pincodeID = data.getStringExtra("pincodeID");
            pincodeTitle = data.getStringExtra("pincodeTitle");
            if (pincodeID != null && !pincodeID.isEmpty() && pincodeID != "" && pincodeTitle != null && !pincodeTitle.isEmpty() && pincodeTitle != "") {
                etPincode.setText(pincodeTitle);
            } else {
                pincodeID = "";
                pincodeTitle = "";
                Toast.makeText(this, getString(R.string.profile_pincode_selection_error_toast_text), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHOOSE_PREFERRED_LANGUAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            preferredLanguageID = data.getStringExtra("preferredLanguageID");
            preferredLanguageTitle = data.getStringExtra("preferredLanguageTitle");
            if (preferredLanguageID != null && !preferredLanguageID.isEmpty() && preferredLanguageID != "" && preferredLanguageTitle != null && !preferredLanguageTitle.isEmpty() && preferredLanguageTitle != "") {
                etPreferredLanguage.setText(preferredLanguageTitle);
            } else {
                preferredLanguageID = "";
                preferredLanguageTitle = "";
                Toast.makeText(this, getString(R.string.profile_preferred_language_selection_error_toast_text), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CHOOSE_PACKAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            packageID = data.getStringExtra("packageID");
            packageTitle = data.getStringExtra("packageTitle");
            if (packageID != null && !packageID.isEmpty() && packageID != ""
                    && packageTitle != null && !packageTitle.isEmpty() && packageTitle != "") {
                etPackage.setText(packageTitle);
            } else {
                packageID = "";
                packageTitle = "";
                Toast.makeText(this, getString(R.string.profile_category_selection_error_toast_text), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadWorkerImage() {
        progressDialogHandler3.showPopupProgressSpinner(true);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(JsonFields.COMMON_REQUEST_PARAMS_WORKER_ID, workerSessionManager.getWorkerID());
        params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(ProfileActivity.this));
        params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
        params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
        params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
        params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(ProfileActivity.this));
        params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
        params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
        if (imagePath != null && !imagePath.isEmpty() && imagePath != "") {
            try {
                params.put(JsonFields.EDIT_WORKER_PROFILE_IMAGE_REQUEST_WORKER_IMAGE, new File(imagePath));
                Log.d("Customer Image File", "Posted");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        client.post(WebURL.EDIT_WORKER_PROFILE_IMAGE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Response", response.toString());
                progressDialogHandler3.showPopupProgressSpinner(false);
                parseImageJSON(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject res) {
                Log.d("Response", String.valueOf(res));
                Log.d("error", "dixit: $res");
                Log.d("response code", String.valueOf(statusCode));
                progressDialogHandler3.showPopupProgressSpinner(false);
                Toast.makeText(ProfileActivity.this, getString(R.string.profile_no_internet_toast_text), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseImageJSON(String response) {
        progressDialogHandler3.showPopupProgressSpinner(false);
        Log.d("RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);

            if (flag == 1) {
                progressDialogHandler3.showPopupProgressSpinner(false);

                Intent i = new Intent(this, SuccessMessageActivity.class);
                i.putExtra("Message", Message);
                i.putExtra("FromScreenName", "ProfileActivity");
                i.putExtra("ToScreenName", "ProfileActivity");
                startActivity(i);
                finish();
            } else {
                progressDialogHandler3.showPopupProgressSpinner(false);
                Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Boolean checkPincode() {
        boolean isPincodeValid = false;
        if (pincodeID != null && !pincodeID.isEmpty() && !pincodeID.equals("")) {
            if (pincodeTitle != null && !pincodeTitle.isEmpty() && !pincodeTitle.equals("")) {
                isPincodeValid = true;
                etPincode.setError(null);
            } else {
                isPincodeValid = false;
                etPincode.setError(getString(R.string.profile_pincode_validation_error_text));
            }
        } else {
            etPincode.setError(getString(R.string.profile_pincode_validation_error_text));
            isPincodeValid = false;
        }
        return isPincodeValid;

    }

    private Boolean checkCity() {
        boolean isCityValid = false;
        if (cityID != null && !cityID.isEmpty() && !cityID.equals("")) {
            if (cityTitle != null && !cityTitle.isEmpty() && !cityTitle.equals("")) {
                isCityValid = true;
                etCity.setError(null);

            } else {
                isCityValid = false;
                etCity.setError(getString(R.string.profile_city_validation_error_text));
            }
        } else {
            etCity.setError(getString(R.string.profile_city_validation_error_text));
            isCityValid = false;
        }
        return isCityValid;
    }


    private Boolean checkAddressLine2() {
        boolean isAddressLine2Valid = false;
        if (etAddressLine2.getText().toString().trim().equals("")) {
            etAddressLine2.setError(getString(R.string.profile_address_line_2_validation_error_text));
            isAddressLine2Valid = false;
        } else {
            isAddressLine2Valid = true;
            etAddressLine2.setError(null);
        }
        return isAddressLine2Valid;
    }

    private Boolean checkAddressLine1() {
        boolean isAddressLine1Valid = false;
        if (etAddressLine1.getText().toString().trim().equals("")) {
            etAddressLine1.setError(getString(R.string.profile_address_line_1_validation_error_text));
            isAddressLine1Valid = false;
        } else {
            isAddressLine1Valid = true;
            etAddressLine1.setError(null);
        }
        return isAddressLine1Valid;
    }


    private Boolean checkPreferredLanguage() {
        boolean isPreferredLanguageValid = false;
        if (preferredLanguageID != null && !preferredLanguageID.isEmpty() && !preferredLanguageID.equals("")) {
            if (preferredLanguageTitle != null && !preferredLanguageTitle.isEmpty() && !preferredLanguageTitle.equals("")) {
                isPreferredLanguageValid = true;
                etPreferredLanguage.setError(null);
            } else {
                isPreferredLanguageValid = false;
                etPreferredLanguage.setError(getString(R.string.profile_preferred_language_validation_error_text));
            }
        } else {
            etPreferredLanguage.setError(getString(R.string.profile_preferred_language_validation_error_text));
            isPreferredLanguageValid = false;
        }
        return isPreferredLanguageValid;
    }


    private Boolean checkWorkerGender() {
        boolean isGenderValid = false;
        if (Gender != null && !Gender.isEmpty() && !Gender.equals("")) {
            isGenderValid = true;
        } else {
            Toast.makeText(this, getString(R.string.profile_something_went_wrong_while_saving_profile_changes_toast_text), Toast.LENGTH_SHORT).show();
            isGenderValid = false;
        }
        return isGenderValid;
    }

    private Boolean checkWorkerEmail() {
        boolean isEmailValid = false;
        if (etEmail.getText().toString().trim().length() > 0) {
            if (validateEmailAddress(etEmail.getText().toString().trim())) {
                isEmailValid = true;
                etEmail.setError(null);
            } else {
                isEmailValid = false;
                etEmail.setError(getString(R.string.profile_email_not_valid_validation_error_text));
            }
        } else {
            isEmailValid = false;
            etEmail.setError(getString(R.string.profile_email_empty_validation_error_text));
        }
        return isEmailValid;
    }


    private Boolean validateEmailAddress(String emailAddress) {
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    private Boolean checkWorkerFirstName() {
        boolean isFirstNameValid = false;
        if (etFirstName.getText().toString().trim().equals("")) {
            etFirstName.setError(getString(R.string.profile_first_name_validation_error_text));
            isFirstNameValid = false;
        } else {
            isFirstNameValid = true;
            etFirstName.setError(null);
        }
        return isFirstNameValid;
    }


    private Boolean checkWorkerMiddleName() {
        boolean isMiddleNameValid = false;
        if (etMiddleName.getText().toString().trim().equals("")){
            etMiddleName.setError(getString(R.string.profile_middle_name_validation_error_text));
            isMiddleNameValid = false;
        } else{
            isMiddleNameValid = true;
            etMiddleName.setError(null);
        }
        return isMiddleNameValid;
    }

    private Boolean checkWorkerLastName() {
        boolean isLastNameValid = false;
        if (etLastName.getText().toString().trim().equals("")){
            etLastName.setError(getString(R.string.profile_last_name_validation_error_text));
            isLastNameValid = false;
        } else{
            isLastNameValid = true;
            etLastName.setError(null);
        }
        return isLastNameValid;
    }


    private void getProfileData() {
        progressDialogHandler1.showPopupProgressSpinner(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.WORKER_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogHandler1.showPopupProgressSpinner(false);
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llError.setVisibility(View.GONE);
                llProfile.setVisibility(View.GONE);
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
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(ProfileActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(ProfileActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                Log.d("params", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response) {
        Log.d("RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);
            if (flag == 1) {
                progressDialogHandler1.showPopupProgressSpinner(false);
                WorkerFirstName = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_FIRST_NAME);
                WorkerMiddleName = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_MIDDLE_NAME);
                WorkerLastName = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_LAST_NAME);
                WorkerGender = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_GENDER);
                WorkerEmail = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_EMAIL);
                WorkerMobileNumber = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_MOBILE_NUMBER);
                WorkerImage = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_IMAGE);
                WorkerAddressLine1 = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_ADDRESS_LINE_1);
                WorkerAddressLine2 = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_ADDRESS_LINE_2);
                preferredLanguageID = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_PREFERRED_LANGUAGE_ID);
                preferredLanguageTitle = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_PREFERRED_LANGUAGE_TITLE);
                cityID = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_PREFERRED_CITY_ID);
                cityTitle = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_PREFERRED_CITY_TITLE);
                pincodeID = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_PREFERRED_PINCODE_ID);
                pincodeTitle = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_PREFERRED_PINCODE_TITLE);
                WorkerBirhtDate = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_BIRTH_DATE);
                WorkerAadharNumber = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_AADHAR_NUMBER);
                packageID = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_PACKAGE_ID);
                packageTitle = jsonObject.optString(JsonFields.WORKER_PROFILE_RESPONSE_WORKER_PACKAGE_TITLE);

                llNoInternetConnection.setVisibility(View.GONE);
                llError.setVisibility(View.GONE);
                llProfile.setVisibility(View.VISIBLE);

                setProfileData();

                /*val i = Intent(this, SuccessMessageActivity::class.java)
                i.putExtra("Message", Message)
                i.putExtra("FromScreenName", "ProfileActivity")
                i.putExtra("ToScreenName", "ProfileActivity")
                startActivity(i)
                finish()*/
            } else {
                progressDialogHandler1.showPopupProgressSpinner(false);
                llNoInternetConnection.setVisibility(View.GONE);
                llProfile.setVisibility(View.GONE);
                tvMessage.setText(Message);
                llError.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void SaveProfileChanges() {
        progressDialogHandler2.showPopupProgressSpinner(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.EDIT_PORFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseSaveChangesJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogHandler2.showPopupProgressSpinner(false);
                llNoInternetConnection.setVisibility(View.VISIBLE);
                llError.setVisibility(View.GONE);
                llProfile.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_FIRST_NAME, etFirstName.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_MIDDLE_NAME, etMiddleName.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_LAST_NAME, etLastName.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_AADHAR_NUMBER, etAadharNumber.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_DATE_OF_BIRTH, etDateOfBirth.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_PACKAGE_ID, packageID);
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_PREFERRED_LANGUAGE_ID, preferredLanguageID);
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_ADDRESS_LINE_1, etAddressLine1.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_ADDRESS_LINE_2, etAddressLine2.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_CITY_ID, cityID);
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_PINCODE_ID, pincodeID);
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_EMAIL, etEmail.getText().toString().trim());
                params.put(JsonFields.EDIT_WORKER_PROFILE_REQUEST_WORKER_GENDER, Gender);
                params.put(JsonFields.COMMON_REQUEST_PARAMS_WORKER_ID, workerSessionManager.getWorkerID());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(ProfileActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(ProfileActivity.this));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                Log.d("params", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void parseSaveChangesJSON(String response) {
        Log.d("RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);
            if (flag == 1) {
                progressDialogHandler2.showPopupProgressSpinner(false);

                if (preferredLanguageTitle != null && !preferredLanguageTitle.isEmpty() && !preferredLanguageTitle.equals("")){
                    //1.Hindi 2.Gujarat 3.English
                    if (preferredLanguageID.equals("1")) {
                        LocaleManager.setLanguagePref(this, "hi");
                    } else if (preferredLanguageID.equals("2")) {
                        LocaleManager.setLanguagePref(this, "gu");
                    } else if (preferredLanguageID.equals("3")) {
                        LocaleManager.setLanguagePref(this, "en");
                    }
                } else{
                    LocaleManager.setLanguagePref(this, "en");
                }

                Intent i = new Intent(this, SuccessMessageActivity.class);
                i.putExtra("Message", Message);
                i.putExtra("FromScreenName", "ProfileActivity");
                i.putExtra("ToScreenName", "ProfileActivity");
                startActivity(i);
                finish();
            } else {
                progressDialogHandler2.showPopupProgressSpinner(false);

                llNoInternetConnection.setVisibility(View.GONE);
                llProfile.setVisibility(View.GONE);
                tvMessage.setText(Message);
                llError.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    private void setProfileData() {
        if (WorkerFirstName != null && !WorkerFirstName.isEmpty() && !WorkerFirstName.equals("")){
            setDataToView(etFirstName.getId(), WorkerFirstName);
        } else{
            setDataToView(etFirstName.getId(), "");
        }

        if (WorkerMiddleName != null && !WorkerMiddleName.isEmpty() && !WorkerMiddleName.equals(""))
        {
            setDataToView(etMiddleName.getId(), WorkerMiddleName);
        } else{
            setDataToView(etMiddleName.getId(), "");
        }

        if (WorkerLastName != null && !WorkerLastName.isEmpty() && !WorkerLastName.equals(""))
        {
            setDataToView(etLastName.getId(), WorkerLastName);
        } else{
            setDataToView(etLastName.getId(), "");
        }

        if (WorkerEmail != null && !WorkerEmail.isEmpty() && !WorkerEmail.equals("")){
            setDataToView(etEmail.getId(), WorkerEmail);
        } else{
            setDataToView(etEmail.getId(), "");
        }

        if (WorkerMobileNumber != null && !WorkerMobileNumber.isEmpty() && !WorkerMobileNumber.equals("")){
            setDataToView(etMobile.getId(), WorkerMobileNumber);
        } else{
            setDataToView(etMobile.getId(), "");
        }

        if (preferredLanguageTitle != null && !preferredLanguageTitle.isEmpty() && !preferredLanguageTitle.equals("")){
            setDataToView(etPreferredLanguage.getId(), preferredLanguageTitle);
        } else{
            setDataToView(etPreferredLanguage.getId(), "");
        }


        if (WorkerAddressLine1 != null && !WorkerAddressLine1.isEmpty() && !WorkerAddressLine1.equals("")){
            setDataToView(etAddressLine1.getId(), WorkerAddressLine1);
        } else{
            setDataToView(etAddressLine1.getId(), "");
        }


        if (WorkerAddressLine2 != null && !WorkerAddressLine2.isEmpty() && !WorkerAddressLine2.equals("")){
            setDataToView(etAddressLine2.getId(), WorkerAddressLine2);
        } else{
            setDataToView(etAddressLine2.getId(), "");
        }

        if (cityTitle != null && !cityTitle.isEmpty() && !cityTitle.equals("")){
            setDataToView(etCity.getId(), cityTitle);
        } else{
            setDataToView(etCity.getId(), "");
        }

        if (pincodeTitle != null && !pincodeTitle.isEmpty() && !pincodeTitle.equals("")){
            setDataToView(etPincode.getId(), pincodeTitle);
        } else{
            setDataToView(etPincode.getId(), "");
        }

        if (WorkerBirhtDate != null && !WorkerBirhtDate.isEmpty() && !WorkerBirhtDate.equals("")){
            setDataToView(etDateOfBirth.getId(), WorkerBirhtDate);
        } else{
            setDataToView(etDateOfBirth.getId(), "");
        }

        if (WorkerAadharNumber != null && !WorkerAadharNumber.isEmpty() && !WorkerAadharNumber.equals("")){
            setDataToView(etAadharNumber.getId(), WorkerAadharNumber);
        } else{
            setDataToView(etAadharNumber.getId(), "");
        }

        if (packageTitle != null && !packageTitle.isEmpty() && !packageTitle.equals("")){
            setDataToView(etPackage.getId(), packageTitle);
        } else{
            setDataToView(etPackage.getId(), "");
        }


        if (WorkerGender != null && !WorkerGender.isEmpty() && !WorkerGender.equals("")){
            if (WorkerGender.equals("Male")) {
                rbMale.setChecked(true);
                rbFemale.setChecked(false);
                Gender = WorkerGender;
            } else if (WorkerGender.equals("Female")) {
                rbFemale.setChecked(true);
                rbMale.setChecked(false);
                Gender = WorkerGender;
            }
        }

        Glide.with(ProfileActivity.this).load(WorkerImage).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.ic_avatar).into(civWorkerDrawable);

    }

    private void setDataToView(int ViewID,String data) {
        TextInputEditText textInputEditText = findViewById(ViewID);
        textInputEditText.setText(data);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed()
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
