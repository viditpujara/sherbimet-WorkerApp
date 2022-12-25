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
import com.sherbimet.worker.Activity.SelectPreferredLanguageActivity;
import com.sherbimet.worker.Model.PreferredLanguage;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PreferredLanguageAdapter extends RecyclerView.Adapter<PreferredLanguageAdapter.ViewHolder> {

    private Context mContext;
    ArrayList<PreferredLanguage> listPreferredLanguage;
    AtClass atClass;

    public PreferredLanguageAdapter(ArrayList<PreferredLanguage> listPreferredLanguage) {
        this.listPreferredLanguage = listPreferredLanguage;
    }

    @Override
    public PreferredLanguageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View mView = LayoutInflater.from(mContext).inflate(R.layout.preferred_language_list_row_item_layout, parent, false);
        atClass = new AtClass();
        return new PreferredLanguageAdapter.ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final PreferredLanguageAdapter.ViewHolder holder, final int position) {
        final PreferredLanguage preferredLanguage = listPreferredLanguage.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPreferredLanguageName.setText(Html.fromHtml(preferredLanguage.getPreferredLanguageName(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPreferredLanguageName.setText(Html.fromHtml(preferredLanguage.getPreferredLanguageName()));
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvPreferredLanguageNameInitials.setText(Html.fromHtml(preferredLanguage.getPreferredLanguageInitials(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvPreferredLanguageNameInitials.setText(Html.fromHtml(preferredLanguage.getPreferredLanguageInitials()));
        }


        holder.cVPreferredLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("preferredLanguageID", preferredLanguage.getPreferredLanguageID());
                intent.putExtra("preferredLanguageTitle", preferredLanguage.getPreferredLanguageName());
                ((SelectPreferredLanguageActivity)mContext).setResult(Activity.RESULT_OK, intent);
                ((SelectPreferredLanguageActivity)mContext).finish();
            }
        });

    }


    @Override
    public int getItemCount() {
        return listPreferredLanguage.size();
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
        TextView tvPreferredLanguageName,tvPreferredLanguageNameInitials;
        CardView cVPreferredLanguage;


        public ViewHolder(View itemView) {
            super(itemView);
            tvPreferredLanguageName = (TextView) itemView.findViewById(R.id.tvPreferredLanguageName);
            tvPreferredLanguageNameInitials = (TextView) itemView.findViewById(R.id.tvPreferredLanguageNameInitials);
            cVPreferredLanguage = (CardView) itemView.findViewById(R.id.cVPreferredLanguage);
        }
    }
}

