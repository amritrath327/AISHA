package com.cybercareinfoways.aisha.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AppConstants;
import com.cybercareinfoways.helpers.AppHelper;
import com.cybercareinfoways.helpers.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMobileActivity extends AppCompatActivity {
    private static final int SELECTCOUNTRYCODE = 12;
    private static final int MSGREQ = 32;
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

    @BindView(R.id.cb_terms)
    CheckBox cbTerms;
    @BindView(R.id.tv_terms)
    TextView tvTerms;
    InputMethodManager iim;
    private boolean isChecked;
    private String code;
    private String phone;
    private String name;
    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        iim = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_add_mobile);
        ButterKnife.bind(this);
        myToolbar.setTitle(AppConstants.REGISTERTITLE);
        setSupportActionBar(myToolbar);
        tvTerms.setClickable(true);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvTerms.setText(Html.fromHtml(AppConstants.TERMS, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvTerms.setText(Html.fromHtml(AppConstants.TERMS));
        }

        etDailCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    iim.hideSoftInputFromWindow(etDailCode.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    fireIntentToSelectCountry();
                }
            }
        });
        cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                isChecked = b;
            }
        });

        if (telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT || telephonyManager.getSimState() != TelephonyManager.SIM_STATE_UNKNOWN) {
            String countryId = telephonyManager.getSimCountryIso().toUpperCase();
            Log.i("countryid", countryId);
            JSONArray json = AppHelper.readJsonFromRaw(AddMobileActivity.this);
            Country c = getCoutry(json, countryId);

            if (c != null) {
                etDailCode.setText(c.getCode());
                btnCountry.setText(c.getName());
                etMobile.requestFocus();
            } else {
                fireIntentToSelectCountry();
            }

        } else {
            fireIntentToSelectCountry();
        }
    }

    private Country getCoutry(JSONArray json, String countryId) {
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = json.getJSONObject(i);
                if (object.getString("code").equals(countryId)) {
                    Country c = new Country(object.getString(AppConstants.COUNTRYNAME), object.getString(AppConstants.COUNTRYCODE));
                    return c;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fireIntentToSelectCountry() {
        Intent i = new Intent(AddMobileActivity.this, SelectCountryCodeActivity.class);
        startActivityForResult(i, SELECTCOUNTRYCODE);
    }

    @OnClick(R.id.btn_country)
    public void selectCountry() {

        fireIntentToSelectCountry();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_mobile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_next) {

            if (isChecked) {
                name = etName.getText().toString();
                code = etDailCode.getText().toString();
                phone = etMobile.getText().toString();
                if (name.length() < 3) {
                    etName.setError(AppConstants.ENTERNAME);
                } else if (phone.equals("") && phone.length() < 10) {
                    etName.setError(null);
                    etMobile.setError(AppConstants.PHOENERROR);
                } else {
                    etName.setError(null);
                    etMobile.setError(null);
                    requestMessagePermission(code, phone, name);
                }
            } else {
                Snackbar snackbar = Snackbar.make(etMobile, AppConstants.ACCECPTTERMS, Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                tvMessage.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestMessagePermission(String code, String mobile, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS}, MSGREQ);
            } else {
                showAlertDialog(code, mobile, name);
            }
        } else {
            showAlertDialog(code, mobile, name);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MSGREQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAlertDialog(code, phone, name);
            } else {
                Toast.makeText(AddMobileActivity.this, "Please Grant messaging permission from App Settings", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showAlertDialog(final String code, final String phone, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddMobileActivity.this);
        builder.setMessage(AppConstants.CONFIRMDIALOG + "'" + code + phone + "'?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                fireIntent(code, phone, name);
            }

        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fireIntent(String code, String phone, String name) {
        Intent i = new Intent(AddMobileActivity.this, VerifyOTPActivity.class);
        i.putExtra(AppConstants.MOBILENUMBER, code + phone);
        i.putExtra(AppConstants.NAME, name);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTCOUNTRYCODE && resultCode == RESULT_OK) {
            String countryCode = data.getExtras().getString(AppConstants.COUNTRYCODE);
            String countryname = data.getExtras().getString(AppConstants.COUNTRYNAME);
            etDailCode.setText(countryCode);
            btnCountry.setText(countryname);
            etMobile.requestFocus();
        }
    }
}
