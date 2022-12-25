package com.sherbimet.worker.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sherbimet.worker.R;

public class UserConfirmationAlertDialog extends Dialog {
    private String message;
    private String title;
    private String btnPositiveText;
    private String btnNegativeText;
    private int icon=0;
    private View.OnClickListener btnPositiveListener=null;
    private View.OnClickListener btnNegativeListener=null;

    public UserConfirmationAlertDialog(Context context) {
        super(context);
    }

    public UserConfirmationAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UserConfirmationAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_confirmation_alert_dialog_layout);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
        tvTitle.setText(getTitle());

        TextView tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvMessage.setText(getMessage());

        Button btnPositiveButton = (Button) findViewById(R.id.btnPositiveButton);
        Button btnNegativeButton = (Button) findViewById(R.id.btnNegativeButton);

        btnPositiveButton.setText(btnPositiveText);
        btnNegativeButton.setText(btnNegativeText);

        btnPositiveButton.setOnClickListener(btnPositiveListener);
        btnNegativeButton.setOnClickListener(btnNegativeListener);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setPositiveButton(String btnPositiveText, View.OnClickListener onClickListener) {
        dismiss();
        this.btnPositiveText = btnPositiveText;
        this.btnPositiveListener = onClickListener;
    }

    public void setNegativeButton(String btnNegativeText, View.OnClickListener onClickListener) {
        dismiss();
        this.btnNegativeText = btnNegativeText;
        this.btnNegativeListener = onClickListener;
    }
}
