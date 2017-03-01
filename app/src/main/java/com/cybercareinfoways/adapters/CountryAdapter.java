package com.cybercareinfoways.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.activities.SelectCountryCodeActivity;
import com.cybercareinfoways.helpers.Country;

import java.util.ArrayList;

/**
 * Created by YELOWFLASH on 03/01/2017.
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {
    String[] nameArray, codeArray;
    Context mContext;
    ArrayList<Country> list;


    public CountryAdapter(Context context, ArrayList<Country> list) {
        mContext = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_country, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Country c = list.get(position);
        holder.tvCountry.setText(c.getName());
        holder.tvCode.setText(c.getCode());
        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectCountryCodeActivity) mContext).foundCountry(c.getCode(), c.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void animateTo(ArrayList<Country> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    public Country removeItem(int position) {
        final Country model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Country model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Country model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void applyAndAnimateRemovals(ArrayList<Country> newModels) {
        for (int i = newModels.size() - 1; i >= 0; i--) {
            final Country model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Country> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Country model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Country> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Country model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCountry, tvCode;
        CardView cvItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCode = (TextView) itemView.findViewById(R.id.tv_country_code);
            tvCountry = (TextView) itemView.findViewById(R.id.tv_country_name);
            cvItem = (CardView) itemView.findViewById(R.id.cv_country);
        }
    }
}
