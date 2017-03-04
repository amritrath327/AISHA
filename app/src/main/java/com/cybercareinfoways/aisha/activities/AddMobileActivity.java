package com.cybercareinfoways.aisha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AishaConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMobileActivity extends AppCompatActivity {
    private static final int SELECTCOUNTRYCODE = 12;
    @BindView(R.id.toolbar)
    Toolbar myToolbar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_dail_code)
    EditText etDailCode;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.btn_country)
    Button btnCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mobile);
        ButterKnife.bind(this);
        myToolbar.setTitle(AishaConstants.REGISTERTITLE);
        setSupportActionBar(myToolbar);
    }

    @OnClick(R.id.btn_country)
    public void selectCountry() {
        //Intent i = new Intent(AddMobileActivity.this, SelectCountryCodeActivity.class);
        //startActivityForResult(i, SELECTCOUNTRYCODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_mobile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.item_next){
            Intent intent = new Intent(AddMobileActivity.this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
