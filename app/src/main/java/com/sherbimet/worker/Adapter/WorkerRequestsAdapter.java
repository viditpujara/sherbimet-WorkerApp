package com.sherbimet.worker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sherbimet.worker.APIHelper.JsonFields;
import com.sherbimet.worker.APIHelper.WebAuthorization;
import com.sherbimet.worker.APIHelper.WebURL;
import com.sherbimet.worker.Activity.ProfileActivity;
import com.sherbimet.worker.Activity.SelectCityActivity;
import com.sherbimet.worker.Activity.SuccessMessageActivity;
import com.sherbimet.worker.Activity.WorkerRequestsServiceActivity;
import com.sherbimet.worker.Model.City;
import com.sherbimet.worker.Model.WorkerRequests;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;
import com.sherbimet.worker.Utils.ProgressDialogHandler;
import com.sherbimet.worker.Utils.WorkerSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerRequestsAdapter extends RecyclerView.Adapter<WorkerRequestsAdapter.ViewHolder> {

    private Context mContext;
    ArrayList<WorkerRequests> listWorkerRequests;
    AtClass atClass;
    String StatusID;
    WorkerSessionManager workerSessionManager;
    ProgressDialogHandler progressDialogHandler;

    public WorkerRequestsAdapter(ArrayList<WorkerRequests> listWorkerRequests, String StatusID) {
        this.listWorkerRequests = listWorkerRequests;
        this.StatusID = StatusID;
    }

    @Override
    public WorkerRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View mView = LayoutInflater.from(mContext).inflate(R.layout.worker_requests_list_row_item_layout, parent, false);
        workerSessionManager = new WorkerSessionManager(mContext);
        atClass = new AtClass();
        progressDialogHandler = new ProgressDialogHandler(mContext);
        return new WorkerRequestsAdapter.ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final WorkerRequestsAdapter.ViewHolder holder, final int position) {
        final WorkerRequests workerRequests = listWorkerRequests.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvRequestID.setText(Html.fromHtml(workerRequests.getRequestID(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvRequestID.setText(HtmlCompat.fromHtml(workerRequests.getRequestID(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvRequestDateTime.setText(Html.fromHtml(workerRequests.getRequestDateTime(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvRequestDateTime.setText(HtmlCompat.fromHtml(workerRequests.getRequestDateTime(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvRequestAddress.setText(Html.fromHtml(workerRequests.getRequestAddress(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvRequestAddress.setText(HtmlCompat.fromHtml(workerRequests.getRequestAddress(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvRequestMessage.setText(Html.fromHtml(workerRequests.getRequestMessage(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvRequestMessage.setText(HtmlCompat.fromHtml(workerRequests.getRequestMessage(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvRequestAmount.setText(Html.fromHtml(workerRequests.getRequestTotalAmount(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvRequestAmount.setText(HtmlCompat.fromHtml(workerRequests.getRequestTotalAmount(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvUserName.setText(Html.fromHtml(workerRequests.getUserName(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvUserName.setText(HtmlCompat.fromHtml(workerRequests.getUserMobile(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvUserMobile.setText(Html.fromHtml(workerRequests.getUserMobile(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvUserMobile.setText(HtmlCompat.fromHtml(workerRequests.getUserMobile(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvUserEmail.setText(Html.fromHtml(workerRequests.getUserEmail(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvUserEmail.setText(HtmlCompat.fromHtml(workerRequests.getUserEmail(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvRequestStatus.setText(Html.fromHtml(workerRequests.getRequestStatus(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvRequestStatus.setText(HtmlCompat.fromHtml(workerRequests.getRequestStatus(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvRatings.setText(Html.fromHtml(workerRequests.getFeedbackRatings(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvRatings.setText(HtmlCompat.fromHtml(workerRequests.getFeedbackRatings(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvReviewMessage.setText(Html.fromHtml(workerRequests.getFeedbackMessage(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvReviewMessage.setText(HtmlCompat.fromHtml(workerRequests.getFeedbackMessage(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPaymentMethod.setText(Html.fromHtml(workerRequests.getPaymentMethod(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPaymentMethod.setText(HtmlCompat.fromHtml(workerRequests.getPaymentMethod(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        Glide.with(mContext).load(workerRequests.getUserImage()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).error(R.drawable.ic_avatar).into(holder.ciVUserImage);

        if (workerRequests.getRequestStatusID().equals("1")) {
            holder.btnEndRequestService.setVisibility(View.GONE);
            holder.llRequestAmount.setVisibility(View.GONE);
            holder.llReview.setVisibility(View.GONE);
            if (workerRequests.getCanAccept().equals("1")) {
                holder.btnAcceptRequestService.setVisibility(View.VISIBLE);
                holder.btnStartRequestService.setVisibility(View.GONE);
            } else {
                holder.btnAcceptRequestService.setVisibility(View.GONE);
                holder.btnStartRequestService.setVisibility(View.GONE);
            }
        } else if (workerRequests.getRequestStatusID().equals("2")) {
            holder.btnStartRequestService.setVisibility(View.VISIBLE);
            holder.btnEndRequestService.setVisibility(View.GONE);
            holder.llRequestAmount.setVisibility(View.GONE);
            holder.btnAcceptRequestService.setVisibility(View.GONE);
            holder.llReview.setVisibility(View.GONE);

        } else if (workerRequests.getRequestStatusID().equals("3")) {
            holder.btnStartRequestService.setVisibility(View.GONE);
            holder.btnEndRequestService.setVisibility(View.VISIBLE);
            holder.llRequestAmount.setVisibility(View.GONE);
            holder.btnAcceptRequestService.setVisibility(View.GONE);
            holder.llReview.setVisibility(View.GONE);

        } else if (workerRequests.getRequestStatusID().equals("4")) {
            holder.btnStartRequestService.setVisibility(View.GONE);
            holder.btnEndRequestService.setVisibility(View.GONE);
            holder.llRequestAmount.setVisibility(View.VISIBLE);
            holder.btnAcceptRequestService.setVisibility(View.GONE);

            if (workerRequests.getCanFeedback().equals("0")) {
                holder.llReview.setVisibility(View.GONE);
            } else {
                holder.llReview.setVisibility(View.VISIBLE);
            }
        } else {
            holder.llReview.setVisibility(View.GONE);
        }

        holder.btnAcceptRequestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (atClass.isNetworkAvailable(mContext)) {
                    ChangeRequestStatusService("2", workerRequests.getRequestID());
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.worker_request_list_row_no_internet_connection_toast_text), Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.btnStartRequestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (atClass.isNetworkAvailable(mContext)) {
                    ChangeRequestStatusService("3", workerRequests.getRequestID());
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.worker_request_list_row_no_internet_connection_toast_text), Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.btnEndRequestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (atClass.isNetworkAvailable(mContext)) {
                    ChangeRequestStatusService("4", workerRequests.getRequestID());
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.worker_request_list_row_no_internet_connection_toast_text), Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /*Intent i = new Intent(mContext, WorkerActivity::class.java)
                 i.putExtra("SubCategoryID", subCategory.SubCategoryID)
                 i.putExtra("SubCategoryName", subCategory.SubCategoryName)
                 mContext!!.startActivity(i)*/
            }
        });

    }


    private void ChangeRequestStatusService(String StatusID,String RequestID) {
        progressDialogHandler.showPopupProgressSpinner(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebURL.WORKERS_REQUESTS_STATUS_UPDATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseStartServiceJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    Toast.makeText(mContext, mContext.getString(R.string.accept_start_end_service_network_error_text), Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mContext, mContext.getString(R.string.accept_start_end_service_no_connection_error_text), Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mContext, mContext.getString(R.string.accept_start_end_service_server_error_text), Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(mContext, mContext.getString(R.string.accept_start_end_service_connection_timeout_error_text), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.accept_start_end_service_error_text), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WebAuthorization.checkAuthentication();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(JsonFields.WORKER_REQUESTS_REQUEST_PARAMS_STATUS_ID, StatusID);
                params.put(JsonFields.WORKER_REQUESTS_STATUS_UPDATE_REQUEST_PARAMS_REQUEST_ID, RequestID);
                params.put(JsonFields.COMMON_REQUEST_PARAMS_WORKER_ID, workerSessionManager.getWorkerID());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_ID, atClass.getDeviceID(mContext));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TYPE, atClass.getDeviceType());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_TOKEN, atClass.getRefreshedToken());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_OS_DETAILS, atClass.getOsInfo());
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_IP_ADDRESS, atClass.getRefreshedIpAddress(mContext));
                params.put(JsonFields.COMMON_REQUEST_PARAM_DEVICE_MODEL_DETAILS, atClass.getDeviceManufacturerModel());
                params.put(JsonFields.COMMON_REQUEST_PARAM_APP_VERSION_DETAILS, atClass.getAppVersionNumberAndVersionCode());
                Log.d("params", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void parseStartServiceJSON(String response) {
        Log.d("RESPONSE", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int flag = jsonObject.optInt(JsonFields.FLAG);
            String Message = jsonObject.optString(JsonFields.MESSAGE);

            if (flag == 1) {
                progressDialogHandler.showPopupProgressSpinner(false);
                Intent i = new Intent(mContext, SuccessMessageActivity.class);
                i.putExtra("Message", Message);
                i.putExtra("StatusID", StatusID);
                i.putExtra("FromScreenName", "WorkerBookingsAdapter");
                i.putExtra("ToScreenName", "WorkerRequestsServiceActivity");
                mContext.startActivity(i);
                ((WorkerRequestsServiceActivity)mContext).finish();
            } else if (flag == 3) {
                //User Is Deactivated By Admin
            } else {
                progressDialogHandler.showPopupProgressSpinner(false);
                Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return listWorkerRequests.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cVRequests;
        CircleImageView ciVUserImage;
        TextView tvRequestID, tvRequestDateTime, tvRequestAddress, tvRequestMessage, tvRequestAmount, tvUserName, tvUserGender, tvUserMobile, tvUserEmail,tvPaymentMethod;

        TextView tvRequestStatus, tvSubCategoryName;

        Button btnStartRequestService, btnEndRequestService, btnAcceptRequestService;


        LinearLayout llRequestAmount, llReview;

        TextView tvRatings, tvReviewMessage;


        public ViewHolder(View itemView) {
            super(itemView);

            cVRequests = (CardView) itemView.findViewById(R.id.cVRequests);
            ciVUserImage = (CircleImageView) itemView.findViewById(R.id.ciVUserImage);
            tvRequestID = (TextView) itemView.findViewById(R.id.tvRequestID);
            tvRequestDateTime = (TextView) itemView.findViewById(R.id.tvRequestDateTime);
            tvRequestAddress = (TextView) itemView.findViewById(R.id.tvRequestAddress);
            tvRequestMessage = (TextView) itemView.findViewById(R.id.tvRequestMessage);
            tvRequestAmount = (TextView) itemView.findViewById(R.id.tvRequestAmount);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvUserGender = (TextView) itemView.findViewById(R.id.tvUserGender);
            tvUserMobile = (TextView) itemView.findViewById(R.id.tvUserMobile);
            tvUserEmail = (TextView) itemView.findViewById(R.id.tvUserEmail);
            tvRequestStatus = (TextView) itemView.findViewById(R.id.tvRequestStatus);
            tvSubCategoryName = (TextView) itemView.findViewById(R.id.tvSubCategoryName);
            tvRatings = (TextView) itemView.findViewById(R.id.tvRatings);
            tvReviewMessage = (TextView) itemView.findViewById(R.id.tvReviewMessage);
            tvPaymentMethod = (TextView) itemView.findViewById(R.id.tvPaymentMethod);

            btnStartRequestService = (Button) itemView.findViewById(R.id.btnStartRequestService);
            btnEndRequestService = (Button) itemView.findViewById(R.id.btnEndRequestService);
            btnAcceptRequestService = (Button) itemView.findViewById(R.id.btnAcceptRequestService);

            llRequestAmount = (LinearLayout) itemView.findViewById(R.id.llRequestAmount);
            llReview = (LinearLayout) itemView.findViewById(R.id.llReview);
        }
    }
}