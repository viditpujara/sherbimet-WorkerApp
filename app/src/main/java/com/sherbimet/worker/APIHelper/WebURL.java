package com.sherbimet.worker.APIHelper;

public class WebURL {
    public static final String IP_ADDRESS = "192.168.0.7";

    public static final String LIVE_URL = "http://sherbimet.swapsinfotech.com/admin/api/worker/";

    public static final String LOCAL_URL = "http://" + IP_ADDRESS + "test/sherbimet/admin/api/worker/";

    public static final String MAIN_URL = LIVE_URL;

    public static final String LOGIN_URL = MAIN_URL + "login-with-otp.php";

    public static final String LOGOUT_URL = MAIN_URL + "logout.php";

    public static final String VERIFY_OTP_URL = MAIN_URL + "verify-otp.php";

    public static final String RESEND_OTP_URL = MAIN_URL + "resend-otp.php";

    public static final String DASHBOARD_URL = MAIN_URL + "worker-dashboard.php";

    public static final String WORKER_PROFILE_URL = MAIN_URL + "worker-profile-list.php";

    public static final String EDIT_PORFILE_URL = MAIN_URL + "worker-profile-update.php";

    public static final String EDIT_WORKER_PROFILE_IMAGE_URL = MAIN_URL + "worker-profile-image-update.php";

    public static final String WORKERS_BOOKINGS_URL = MAIN_URL + "my-booking-list.php";

    public static final String WORKERS_BOOKINGS_STATUS_UPDATE_URL = MAIN_URL + "booking-status-update.php";

    public static final String WORKERS_REQUESTS_URL = MAIN_URL + "my-request-list.php";

    public static final String WORKERS_REQUESTS_STATUS_UPDATE_URL = MAIN_URL + "request-status-update.php";

    public static final String AREA_URL = MAIN_URL + "area-list.php";

    public static final String SUB_CATEGORY_URL = MAIN_URL + "subcategory-list.php";

    public static final String WORKERS_URL = MAIN_URL + "worker-list.php";

    public static final String BOOK_WORKER_URL = MAIN_URL + "add-new-booking-request.php";

    public static final String BOOKINGS_REQUESTS_URL = MAIN_URL + "booking-request-list.php";

    public static final String ABOUT_US_URL = MAIN_URL + "aboutus.php";

    public static final String SELECT_PREFERRED_LANGUAGE_URL = MAIN_URL + "language-list.php";

    public static final String SELECT_CITY_URL = MAIN_URL + "city-list.php";

    public static final String SELECT_PINCODE_URL = MAIN_URL + "pincode-list.php";

    public static final String SELECT_PACKAGE_URL = MAIN_URL + "package-list.php";
}
