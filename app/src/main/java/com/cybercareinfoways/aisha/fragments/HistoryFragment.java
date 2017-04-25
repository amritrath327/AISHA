package com.cybercareinfoways.aisha.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.HistoryResponse;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.SimpleWebRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by amritrath on 25/04/17.
 */

public class HistoryFragment extends Fragment {
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;


    String userId;
    Call<HistoryResponse> profileResponseCall;
    ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, myView);
        //TODO
        userId = getActivity().getSharedPreferences(AishaConstants.USERPREFS, Context.MODE_PRIVATE)
                .getString(AishaConstants.USERID, "NA");
        Log.i("user", userId);
        if (!userId.equals("NA"))
            getHistory();

        return myView;
    }

    private void getHistory() {
        if (AishaUtilities.isConnectingToInternet(getActivity())) {
            callHistoryWebServices();

        } else {

            Snackbar snackbar = Snackbar.make(rvHistory, AishaConstants.ACCECPTTERMS, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void callHistoryWebServices() {
        dialog = AishaUtilities.showProgressDialog(getActivity(), AishaConstants.PROFILEMSG);
        dialog.show();
        WebApi api = AishaUtilities.setupRetrofit();
        profileResponseCall = api.getHiostry(new SimpleWebRequest(userId));
        profileResponseCall.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {

            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {

            }
        });
    }
}
