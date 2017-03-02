package com.cybercareinfoways.aisha.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YELOWFLASH on 02/28/2017.
 */
public class VerifyOTPActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_otp)
    Toolbar toolbar;
    @BindView(R.id.tv_otp_instruction)
    TextView tvOtpInstr;
    @BindView(R.id.et_otp)
    EditText etOtp;
    @BindView(R.id.btn_resend_otp)
    Button btnResendOtp;
    @BindView(R.id.tv_resend_counter)
    TextView tvCounter;
    boolean disabled = false;
    IntentFilter filter;
    private int counterTime = 2 * 60 * 1000;
    private String name, code, mobile;
    private OTPReceiver otpReceiver;
    private CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        name = b.getString(AppConstants.NAME);
        code = b.getString(AppConstants.COUNTRYCODE);
        mobile = b.getString(AppConstants.MOBILENUMBER);
        setContentView(R.layout.activity_verify_otp);

        ButterKnife.bind(this);
        toolbar.setTitle(AppConstants.VERIFYOTP);
        setSupportActionBar(toolbar);

        tvOtpInstr.setText("Hi, " + name + ". Your One-Time-Password has been sent to " + code + mobile + ".");

        otpReceiver = new OTPReceiver();
        filter = new IntentFilter(OTPReceiver.ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(otpReceiver, filter);
        generateOtp();
    }

    private void generateOtp() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verify_otp, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_verify) {

        }
        return super.onOptionsItemSelected(item);
    }

    public class OTPReceiver extends BroadcastReceiver {
        public final static String ACTION = AppConstants.OTPINTENTFILTERACTION;

        @Override
        public void onReceive(Context context, Intent intent) {
            String otp = intent.getExtras().getString(AppConstants.OTP);
            Log.i("OTP", otp);

        }
    }


}
