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
import com.cybercareinfoways.aisha.model.UserData;
import com.cybercareinfoways.helpers.TextDrawable;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nutan on 07-03-2017.
 */

public class UserAvailableAdapter extends RecyclerView.Adapter<UserAvailableAdapter.UserAvialableViewHolder>{
    private Context context;
    private ArrayList<UserData> availableUserList;
    private Picasso picasso;

    public UserAvailableAdapter(Context context, ArrayList<UserData> availableUserList) {
        this.context = context;
        this.availableUserList = availableUserList;
        picasso = Picasso.with(context);
    }

    @Override
    public UserAvialableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_available_list,parent,false);
        return new UserAvialableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAvialableViewHolder holder, int position) {
        UserData userData = availableUserList.get(position);
        if (!TextUtils.isEmpty(userData.getImage_url())){
            picasso.load(userData.getImage_url()).resize(300,300).placeholder(android.R.drawable.gallery_thumb)
                    .error(android.R.drawable.gallery_thumb).memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                    .into(holder.imgAvailableUserPic);
        }else {
            holder.imgAvailableUserPic.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_gallery));
        }
        holder.txtavailableUserMobile.setText(userData.getMobile());
        holder.txtAvailableUserStatus.setText(""+userData.getImage_status());
    }

    @Override
    public int getItemCount() {
        return availableUserList.size();
    }

    public class UserAvialableViewHolder extends RecyclerView.ViewHolder{
        ImageView imgAvailableUserPic;
        TextView txtavailableUserMobile,txtAvailableUserStatus;
        public UserAvialableViewHolder(View itemView) {
            super(itemView);
            imgAvailableUserPic =(ImageView)itemView.findViewById(R.id.imgAvailableUserPic);
            txtavailableUserMobile = (TextView)itemView.findViewById(R.id.txtavailableUserMobile);
            txtAvailableUserStatus =(TextView)itemView.findViewById(R.id.txtAvailableUserStatus);
        }
    }
}
