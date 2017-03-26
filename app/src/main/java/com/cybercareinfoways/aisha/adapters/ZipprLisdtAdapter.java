package com.cybercareinfoways.aisha.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.ZipprListData;
import com.cybercareinfoways.helpers.OnItemClickListner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nutan on 26-03-2017.
 */

public class ZipprLisdtAdapter extends RecyclerView.Adapter<ZipprLisdtAdapter.ZipprListViewHolder>{
    private Context context;
    private ArrayList<ZipprListData>zipprListDatas;
    private OnItemClickListner listner;

    public ZipprLisdtAdapter(Context context, ArrayList<ZipprListData> zipprListDatas,OnItemClickListner listner1) {
        this.context = context;
        this.zipprListDatas = zipprListDatas;
        this.listner = listner1;
    }

    @Override
    public ZipprListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.zippr_list_item,parent,false);
        return new ZipprListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ZipprListViewHolder holder, int position) {
        ZipprListData zipprListData = zipprListDatas.get(position);
        if (zipprListData.getImage_status()==0){
            holder.imgZipprPic.setImageDrawable(context.getResources().getDrawable(R.drawable.add));
        }else {
            //TODO
            Picasso.with(context).load("http://downloadicons.net/sites/default/files/add-icon-76240.png").placeholder(R.drawable.add).error(R.drawable.add).into(holder.imgZipprPic);
        }
        if (zipprListData.getAddress_type()==1){
            holder.txtZipprAddress.setText(zipprListData.getAddress_line());
        }else {
            holder.txtZipprAddress.setText(zipprListData.getAddress_line()+zipprListData.getAddress_name()+zipprListData.getPlot_number()+
            zipprListData.getStreet_name()+zipprListData.getCity()+zipprListData.getState()+zipprListData.getPincode());
        }
        holder.txtZipprCode.setText(zipprListData.getZipper_code());
    }

    @Override
    public int getItemCount() {
        return zipprListDatas.size();
    }

    public void clear() {
        zipprListDatas.clear();
        notifyDataSetChanged();
    }

    public class ZipprListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgZipprPic;
        TextView txtZipprAddress,txtZipprCode;
        public ZipprListViewHolder(View itemView) {
            super(itemView);
            imgZipprPic = (ImageView)itemView.findViewById(R.id.imgZipprPic);
            txtZipprAddress = (TextView)itemView.findViewById(R.id.txtZipprAddress);
            txtZipprCode = (TextView)itemView.findViewById(R.id.txtZipprCode);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listner!=null){
                listner.OnItemClick(v,getAdapterPosition());
            }
        }
    }
}
