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
import com.sherbimet.worker.Activity.SelectCityActivity;
import com.sherbimet.worker.Model.City;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.AtClass;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private Context mContext;
    ArrayList<City> listCity;
    AtClass atClass;

    public CityAdapter(ArrayList<City> listCity) {
        this.listCity = listCity;
    }

    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View mView = LayoutInflater.from(mContext).inflate(R.layout.city_list_row_item_layout, parent, false);
        atClass = new AtClass();
        return new CityAdapter.ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final CityAdapter.ViewHolder holder, final int position) {
        final City city = listCity.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvCityName.setText(Html.fromHtml(city.getCityName(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvCityName.setText(Html.fromHtml(city.getCityName()));
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvCityNameInitials.setText(Html.fromHtml(city.getCityNameInitials(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.tvCityNameInitials.setText(Html.fromHtml(city.getCityNameInitials()));
        }


        holder.cVCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("cityID", city.getCityID());
                intent.putExtra("cityTitle", city.getCityName());
                ((SelectCityActivity)mContext).setResult(Activity.RESULT_OK, intent);
                ((SelectCityActivity)mContext).finish();
            }
        });

    }


    @Override
    public int getItemCount() {
        return listCity.size();
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
        TextView tvCityName,tvCityNameInitials;
        CardView cVCity;


        public ViewHolder(View itemView) {
            super(itemView);

            tvCityName = (TextView) itemView.findViewById(R.id.tvCityName);
            tvCityNameInitials = (TextView) itemView.findViewById(R.id.tvCityNameInitials);
            cVCity = (CardView) itemView.findViewById(R.id.cVCity);
        }
    }
}

