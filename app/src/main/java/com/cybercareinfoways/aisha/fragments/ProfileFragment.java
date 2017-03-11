package com.cybercareinfoways.aisha.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.ProfileResponse;
import com.cybercareinfoways.webapihelpers.SimpleWebRequest;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.btn_edit)
    ImageButton btnEdit;
    String userId;
    ProgressDialog dialog;
    Call<ProfileResponse> profileResponseCall;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        userId = getActivity().getSharedPreferences(AishaConstants.USERPREFS, Context.MODE_PRIVATE)
                .getString(AishaConstants.USERID, "NA");
        if (!userId.equals("NA"))
            getProfile();
        return view;
    }

    private void getProfile() {
        if (AishaUtilities.isConnectingToInternet(getActivity())) {
            callProfileWebService();
        } else {
            Snackbar snackbar = Snackbar.make(etName, AishaConstants.ACCECPTTERMS, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void callProfileWebService() {
        dialog = AishaUtilities.showProgressDialog(getActivity(), AishaConstants.PROFILEMSG);
        dialog.show();
        WebApi api = AishaUtilities.setupRetrofit();
        profileResponseCall = api.getProfile(new SimpleWebRequest(userId));
        profileResponseCall.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == AishaConstants.SUCCESS) {
                        int imgStat = response.body().getImage_status();

                        String name = response.body().getName();
                        etName.setText(name);
                        if (imgStat == AishaConstants.SUCCESS) {
                            String imageUrl = response.body().getImage_url();
                            Picasso.with(getActivity()).load(imageUrl).resize(160, 160).into(ivProfile);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(etName, AishaConstants.PROFILEFETCHERROR, Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        });
    }

}
