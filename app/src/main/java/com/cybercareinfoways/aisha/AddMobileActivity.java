package com.cybercareinfoways.aisha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import com.cybercareinfoways.helpers.AppConstants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMobileActivity extends AppCompatActivity {
    private static final int SELECTCOUNTRYCODE = 12;
    @Bind(R.id.toolbar)
    Toolbar myToolbar;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_dail_code)
    EditText etDailCode;
    @Bind(R.id.et_mobile)
    EditText etMobile;
    @Bind(R.id.btn_country)
    Button btnCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mobile);
        ButterKnife.bind(this);
        myToolbar.setTitle(AppConstants.REGISTERTITLE);
        setSupportActionBar(myToolbar);

    }

    @OnClick(R.id.btn_country)
    public void selectCountry() {
        Intent i = new Intent(AddMobileActivity.this, SelectCountryCodeActivity.class);
        startActivityForResult(i, SELECTCOUNTRYCODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_mobile, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
