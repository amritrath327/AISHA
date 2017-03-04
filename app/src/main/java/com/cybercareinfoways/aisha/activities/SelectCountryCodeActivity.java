package com.cybercareinfoways.aisha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cybercareinfoways.adapters.CountryAdapter;
import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AppConstants;
import com.cybercareinfoways.helpers.AppHelper;
import com.cybercareinfoways.helpers.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YELOWFLASH on 02/28/2017.
 */
public class SelectCountryCodeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_country)
    RecyclerView rvCountry;

    private ArrayList<Country> list;

    private CountryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvCountry.setLayoutManager(llm);
        JSONArray json = AppHelper.readJsonFromRaw(SelectCountryCodeActivity.this);
        list = getListFromJson(json);

        adapter = new CountryAdapter(SelectCountryCodeActivity.this, list);
        rvCountry.setAdapter(adapter);
    }

    private ArrayList<Country> getListFromJson(JSONArray json) {
        ArrayList<Country> list = new ArrayList<>();
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject countryObject = json.getJSONObject(i);
                Country c = new Country(countryObject.getString(AppConstants.COUNTRYNAME), countryObject.getString(AppConstants.COUNTRYCODE));

                list.add(c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.country_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        final ArrayList<Country> filteredModelList = filter(list, query);
        adapter.animateTo(filteredModelList);
        rvCountry.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<Country> filteredModelList = filter(list, newText);
        adapter.animateTo(filteredModelList);
        rvCountry.scrollToPosition(0);
        return true;
    }

    private ArrayList<Country> filter(ArrayList<Country> models, String query) {
        query = query.toLowerCase();

        final ArrayList<Country> filteredModelList = new ArrayList<>();
        for (Country model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void foundCountry(String code, String name) {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putString(AppConstants.COUNTRYCODE, code);
        b.putString(AppConstants.COUNTRYNAME, name);
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();
    }
}
