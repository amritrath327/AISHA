package com.cybercareinfoways.aisha.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.ZipprCodeResponse;
import com.cybercareinfoways.helpers.AishaConstants;
import com.cybercareinfoways.helpers.AishaUtilities;
import com.cybercareinfoways.helpers.WebApi;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAishaCodeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Call<ZipprCodeResponse> callCodeResponse;
    ProgressDialog dialog;
    private String userId;
    private WebApi codeApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_aisha_code);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AISHA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userId = AishaUtilities.getSharedPreffUserid(SearchAishaCodeActivity.this);
        codeApi = AishaUtilities.setupRetrofit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.code_serach_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.item_search));
        searchView.setQueryHint("Search by Aisha Code...");
        EditText editText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(getResources().getColor(R.color.white));
        editText.setHintTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (AishaUtilities.isConnectingToInternet(SearchAishaCodeActivity.this)) {
                    if (query.length() == 6) {
                        searchAsihaGeneratedCode(query);
                    } else {
                        Toast.makeText(SearchAishaCodeActivity.this, "Please enter 6 digit generated code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchAishaCodeActivity.this, AishaConstants.NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (AishaUtilities.isConnectingToInternet(SearchAishaCodeActivity.this)) {
                    if (newText.length() == 6) {
                        searchAsihaGeneratedCode(newText);
                    } else {
                        Toast.makeText(SearchAishaCodeActivity.this, "Please enter 6 digit generated code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchAishaCodeActivity.this, AishaConstants.NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        searchView.setIconified(false);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        return true;
    }

    private void searchAsihaGeneratedCode(final String newText) {
        dialog = AishaUtilities.showProgressDialog(SearchAishaCodeActivity.this, AishaConstants.SEARCHCODEMSG);
        dialog.show();
        Map<String, String> mapCode = new HashMap<>(2);
        mapCode.put("user_id", userId);
        mapCode.put("zipper_code", newText.toUpperCase());
        callCodeResponse = codeApi.getAddressFromCode(mapCode);
        callCodeResponse.enqueue(new Callback<ZipprCodeResponse>() {
            @Override
            public void onResponse(Call<ZipprCodeResponse> call, Response<ZipprCodeResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 1) {
                        Toast.makeText(SearchAishaCodeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        ZipprCodeResponse zipprCodeResponse = new ZipprCodeResponse();
                        zipprCodeResponse.setLatitude(response.body().getLatitude());
                        zipprCodeResponse.setLongitude(response.body().getLongitude());
                        zipprCodeResponse.setAddress_type(response.body().getAddress_type());
                        if (response.body().getAddress_type().equals("1")) {
                            zipprCodeResponse.setAddress_line(response.body().getAddress_line());
                        }
                        if (response.body().getAddress_type().equals("2")) {
                            zipprCodeResponse.setAddress_name(response.body().getAddress_name());
                            zipprCodeResponse.setPlot_number(response.body().getPlot_number());
                            zipprCodeResponse.setCity(response.body().getCity());
                            zipprCodeResponse.setState(response.body().getState());
                            zipprCodeResponse.setPincode(response.body().getPincode());
                        }
                        zipprCodeResponse.setImage_status(response.body().getImage_status());
                        zipprCodeResponse.setImage_url(response.body().getImage_url());
                        Intent intent = new Intent(SearchAishaCodeActivity.this, ZipprFoundActivity.class);
                        intent.putExtra(AishaConstants.EXTRA_ZIPPR, zipprCodeResponse);
                        intent.putExtra(AishaConstants.EXTRA_ZIPPR_CODE, newText);
                        startActivity(intent);

                    } else {
                        Toast.makeText(SearchAishaCodeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchAishaCodeActivity.this, "Code not found,Please tryagain", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ZipprCodeResponse> call, Throwable t) {
                dialog.dismiss();
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(SearchAishaCodeActivity.this, "Connection timeout", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("ERROR", t.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
