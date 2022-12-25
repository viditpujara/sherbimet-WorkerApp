package com.sherbimet.worker.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.LinearLayout;

import com.sherbimet.worker.R;

public class ProgressDialogHandler {
    Context mContext;
    private Dialog progressDialog = null;
    LinearLayout llProgress;

    public ProgressDialogHandler(Context context) {
        this.mContext = context;
    }

    public void showPopupProgressSpinner(Boolean isShowing) {
        if (isShowing == true) {
            progressDialog = new Dialog(mContext);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.popup_progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.show();
        } else if (isShowing == false) {
            if ((this.progressDialog != null) && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        }
    }
}