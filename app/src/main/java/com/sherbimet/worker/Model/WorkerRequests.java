package com.sherbimet.worker.Model;

public class WorkerRequests {

    String RequestID,RequestDateTime,RequestAddress,RequestMessage,RequestTotalAmount,UserID,UserImage,UserName,UserGender,UserMobile,UserEmail,RequestStatusID,RequestStatus,PackageName,CanAccept,CanFeedback,FeedbackRatings,FeedbackMessage,PaymentMethod;


    public WorkerRequests(String requestID, String requestDateTime, String requestAddress, String requestMessage, String requestTotalAmount, String userID, String userImage, String userName, String userGender, String userMobile, String userEmail, String requestStatusID, String requestStatus, String packageName, String canAccept, String canFeedback, String feedbackRatings, String feedbackMessage, String paymentMethod) {
        RequestID = requestID;
        RequestDateTime = requestDateTime;
        RequestAddress = requestAddress;
        RequestMessage = requestMessage;
        RequestTotalAmount = requestTotalAmount;
        UserID = userID;
        UserImage = userImage;
        UserName = userName;
        UserGender = userGender;
        UserMobile = userMobile;
        UserEmail = userEmail;
        RequestStatusID = requestStatusID;
        RequestStatus = requestStatus;
        PackageName = packageName;
        CanAccept = canAccept;
        CanFeedback = canFeedback;
        FeedbackRatings = feedbackRatings;
        FeedbackMessage = feedbackMessage;
        PaymentMethod = paymentMethod;
    }

    public WorkerRequests() {
    }

    public String getRequestID() {
        return RequestID;
    }

    public void setRequestID(String requestID) {
        RequestID = requestID;
    }

    public String getRequestDateTime() {
        return RequestDateTime;
    }

    public void setRequestDateTime(String requestDateTime) {
        RequestDateTime = requestDateTime;
    }

    public String getRequestAddress() {
        return RequestAddress;
    }

    public void setRequestAddress(String requestAddress) {
        RequestAddress = requestAddress;
    }

    public String getRequestMessage() {
        return RequestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        RequestMessage = requestMessage;
    }

    public String getRequestTotalAmount() {
        return RequestTotalAmount;
    }

    public void setRequestTotalAmount(String requestTotalAmount) {
        RequestTotalAmount = requestTotalAmount;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserGender() {
        return UserGender;
    }

    public void setUserGender(String userGender) {
        UserGender = userGender;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String userMobile) {
        UserMobile = userMobile;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getRequestStatusID() {
        return RequestStatusID;
    }

    public void setRequestStatusID(String requestStatusID) {
        RequestStatusID = requestStatusID;
    }

    public String getRequestStatus() {
        return RequestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        RequestStatus = requestStatus;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getCanAccept() {
        return CanAccept;
    }

    public void setCanAccept(String canAccept) {
        CanAccept = canAccept;
    }

    public String getCanFeedback() {
        return CanFeedback;
    }

    public void setCanFeedback(String canFeedback) {
        CanFeedback = canFeedback;
    }

    public String getFeedbackRatings() {
        return FeedbackRatings;
    }

    public void setFeedbackRatings(String feedbackRatings) {
        FeedbackRatings = feedbackRatings;
    }

    public String getFeedbackMessage() {
        return FeedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        FeedbackMessage = feedbackMessage;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }
}
