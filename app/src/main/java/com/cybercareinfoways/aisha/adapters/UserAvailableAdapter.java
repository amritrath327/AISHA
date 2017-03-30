package com.cybercareinfoways.aisha.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.activities.ContactsActivity;
import com.cybercareinfoways.aisha.activities.HomeActivity;
import com.cybercareinfoways.aisha.activities.NewContactsActivity;
import com.cybercareinfoways.aisha.fragments.ContactsFragment;
import com.cybercareinfoways.aisha.model.LoationRequest;
import com.cybercareinfoways.aisha.model.UserData;
import com.cybercareinfoways.helpers.UserClickListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nutan on 07-03-2017.
 */

public class UserAvailableAdapter extends RecyclerView.Adapter<UserAvailableAdapter.UserAvialableViewHolder> {
    private Context context;
    private ArrayList<UserData> availableUserList;
    private Picasso picasso;
    private UserClickListener userClickListener;
    private Map<String,LoationRequest> requestedNumbers;
    private LocationAcceptListner listner;


    public UserAvailableAdapter(Context context, ArrayList<UserData> availableUserList) {
        this.context = context;
        this.availableUserList = availableUserList;
        picasso = Picasso.with(context);
        requestedNumbers=new HashMap<>();
    }
    public void addRequest(LoationRequest request){
        requestedNumbers.put(request.getRequestFrom(),request);
        notifyDataSetChanged();
    }
    public void setOnUSerClicked(UserClickListener uSerClicked){
        this.userClickListener = uSerClicked;
    }

    @Override
    public UserAvialableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_available_list, parent, false);
        return new UserAvialableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAvialableViewHolder holder, int position) {
        UserData userData = availableUserList.get(position);
        if (!TextUtils.isEmpty(userData.getImage_url())) {
            picasso.load(userData.getImage_url()).resize(300, 300).placeholder(android.R.drawable.gallery_thumb)
                    .error(android.R.drawable.gallery_thumb).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.imgAvailableUserPic);
        } else {
            holder.imgAvailableUserPic.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_gallery));
        }

        holder.txtavailableUserMobile.setText(userData.getMobile());
        holder.txtAvailableUserStatus.setText("" + userData.getImage_status());
        if (context instanceof ContactsActivity) {
            holder.txtNamefromNumber.setText(((ContactsActivity) context).getNameFromNumber(availableUserList.get(position).getMobile()));
        }
        if (context instanceof NewContactsActivity) {
            holder.txtNamefromNumber.setText(((NewContactsActivity) context).getNameFromNumber(availableUserList.get(position).getMobile()));
        }
        if (context instanceof HomeActivity) {
            holder.txtNamefromNumber.setText(((ContactsFragment) ((HomeActivity) context).getSupportFragmentManager().findFragmentByTag("ContactFrag")).getNameFromNumber(availableUserList.get(position).getMobile()));
        }
        if(requestedNumbers.get(userData.getMobile())!=null){
            holder.acceptRejectView.setVisibility(View.VISIBLE);
        }else{
            holder.acceptRejectView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return availableUserList.size();
    }
    public void setListner(LocationAcceptListner listner){
        this.listner=listner;
    }


    public class UserAvialableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgAvailableUserPic;
        TextView txtavailableUserMobile, txtAvailableUserStatus, txtNamefromNumber;
        LinearLayout user_avialable_layout,acceptRejectView;
        List<TextView> textViews;
        Button btnAccept,btnReject;

        public UserAvialableViewHolder(View itemView) {
            super(itemView);
            imgAvailableUserPic = (ImageView) itemView.findViewById(R.id.imgAvailableUserPic);
            txtavailableUserMobile = (TextView) itemView.findViewById(R.id.txtavailableUserMobile);
            txtAvailableUserStatus = (TextView) itemView.findViewById(R.id.txtAvailableUserStatus);
            user_avialable_layout = (LinearLayout) itemView.findViewById(R.id.user_avialable_layout);
            txtNamefromNumber = (TextView) itemView.findViewById(R.id.txtNamefromNumber);
            acceptRejectView=(LinearLayout) itemView.findViewById(R.id.acceptRejectView);
            textViews = new LinkedList<>();
            btnAccept = (Button)itemView.findViewById(R.id.btnAccept);
            btnReject = (Button)itemView.findViewById(R.id.btnReject);
            itemView.setOnClickListener(this);
            btnAccept.setOnClickListener(this);
            btnReject.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btnAccept){
                 if(listner!=null){
                     int pos=getAdapterPosition();
                     UserData userData=availableUserList.get(pos);
                     com.cybercareinfoways.aisha.model.LoationRequest loationRequest=requestedNumbers.get(userData.getMobile());
                     listner.onAccept(loationRequest);
                 }
            }else if(v.getId()==R.id.btnReject){
                int pos=getAdapterPosition();
                UserData userData=availableUserList.get(pos);
                com.cybercareinfoways.aisha.model.LoationRequest loationRequest=requestedNumbers.get(userData.getMobile());
                listner.onReject(loationRequest);
            }
            else  if(userClickListener!=null){
                userClickListener.onUserCliked(v,getAdapterPosition());
            }
        }
    }
    public interface LocationAcceptListner{
        void onAccept(com.cybercareinfoways.aisha.model.LoationRequest loationRequest);
        void onReject(com.cybercareinfoways.aisha.model.LoationRequest loationRequest);
    }
}
