package com.sherbimet.worker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sherbimet.worker.Activity.ProfileActivity;
import com.sherbimet.worker.Activity.SelectPincodeActivity;
import com.sherbimet.worker.Model.Pincode;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PincodeAdapter extends RecyclerView.Adapter<PincodeAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<Pincode> listPincode;
    AtClass atClass;

    public PincodeAdapter(ArrayList<Pincode> listPincode) {
        this.listPincode = listPincode;
    }

    @Override
    public PincodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View mView = LayoutInflater.from(mContext).inflate(R.layout.pincode_list_row_item_layout, parent, false);
        atClass = new AtClass();
        return new PincodeAdapter.ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final PincodeAdapter.ViewHolder holder, final int position) {
        final Pincode pincode = listPincode.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPincodeName.setText(Html.fromHtml(pincode.getPincodeNumber(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPincodeName.setText(Html.fromHtml(pincode.getPincodeNumber()));
        }


        holder.cVPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("pincodeID", pincode.getPincodeID());
                intent.putExtra("pincodeTitle", pincode.getPincodeNumber());
                ((SelectPincodeActivity) mContext).setResult(Activity.RESULT_OK, intent);
                ((SelectPincodeActivity) mContext).finish();
            }
        });

    }


    @Override
    public int getItemCount() {
        return listPincode.size();
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
        TextView tvPincodeName;
        CardView cVPincode;


        public ViewHolder(View itemView) {
            super(itemView);

            tvPincodeName = (TextView)itemView.findViewById(R.id.tvPincodeName);
            cVPincode = (CardView) itemView.findViewById(R.id.cVPincode);
        }
    }
}

