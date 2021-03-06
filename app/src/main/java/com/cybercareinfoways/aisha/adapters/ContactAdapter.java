package com.cybercareinfoways.aisha.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.aisha.model.UserData;
import com.cybercareinfoways.helpers.OnItemClickListner;

import java.util.ArrayList;

/**
 * Created by Nutan on 01-03-2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewhOlder>{
    private Context context;
    private ArrayList<Contacts> cotacList;
    private OnItemClickListner listner;

    public ContactAdapter(Context context, ArrayList<Contacts> cotacList) {
        this.context = context;
        this.cotacList = cotacList;
    }

    @Override
    public ContactViewhOlder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.contact_list_item,parent,false);
        return new ContactViewhOlder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewhOlder holder, int position) {
        holder.contactName.setText(cotacList.get(position).getContactName());
        holder.contactNumber.setText(cotacList.get(position).getMobile());
    }
    public void setOnItemclickListner(OnItemClickListner listner1){
        this.listner  = listner1;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cotacList.size();
    }

    public class ContactViewhOlder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView contactImage;
        TextView contactName,contactNumber;
        public ContactViewhOlder(View itemView) {
            super(itemView);
            contactImage = (ImageView)itemView.findViewById(R.id.imgContact);
            contactName  = (TextView)itemView.findViewById(R.id.txtContactName);
            contactNumber = (TextView)itemView.findViewById(R.id.txtContactNumber);
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
