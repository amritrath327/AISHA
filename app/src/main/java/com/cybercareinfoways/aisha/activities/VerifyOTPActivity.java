package com.cybercareinfoways.aisha.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.helpers.AppConstants;
import com.cybercareinfoways.helpers.AppHelper;
import com.cybercareinfoways.helpers.WebApi;
import com.cybercareinfoways.webapihelpers.GenOtpRequest;
import com.cybercareinfoways.webapihelpers.GenOtpResponse;
import com.cybercareinfoways.webapihelpers.VerifyOtpRequest;
import com.cybercareinfoways.webapihelpers.VerifyOtpResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    InputMethodManager iim;
    MenuItem menuItem;
    boolean showMenu = true;
    ProgressDialog dialog;
    Call<GenOtpResponse> genOtpResponseCall;
    Call<VerifyOtpResponse> getVerifyOtpResponseCall;
    private int counterTime = 2 * 60 * 1000;
    private String name, code, mobile;
    private OTPReceiver otpReceiver;
    private CountDownTimer timer;
    private String userId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        name = b.getString(AppConstants.NAME);
        code = b.getString(AppConstants.COUNTRYCODE);
        mobile = b.getString(AppConstants.MOBILENUMBER);
        setContentView(R.layout.activity_verify_otp);
        iim = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
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
        if (AppHelper.isConnectingToInternet(VerifyOTPActivity.this)) {
            dialog = AppHelper.showProgressDialog(VerifyOTPActivity.this, AppConstants.GENOTPMESSAGE);
            dialog.show();
            //setup retrofit
            WebApi api = AppHelper.setupRetrofit();
            final GenOtpRequest request = new GenOtpRequest(mobile, code, name);
            Log.i("data", request.toString());
            genOtpResponseCall = api.getGenOtpResponseCall(request);
            genOtpResponseCall.enqueue(new Callback<GenOtpResponse>() {
                @Override
                public void onResponse(Call<GenOtpResponse> call, Response<GenOtpResponse> response) {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        Log.i("status", response.body().getUser_id());
                        dialog.dismiss();
                        if (status == AppConstants.SUCCESS) {
                            userId = response.body().getUser_id();
                            updateUiForResend(false);
                        } else {
                            Snackbar snackbar = Snackbar.make(etOtp, AppConstants.WENTWRONG, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            generateOtp();
                                        }
                                    });
                            snackbar.setActionTextColor(Color.RED);
                            // Changing action button text color
                            View sbView = snackbar.getView();
                            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            tvMessage.setTextColor(Color.YELLOW);
                            snackbar.show();
                        }
                    }
                }


                @Override
                public void onFailure(Call<GenOtpResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("Error", t.getLocalizedMessage());
                }
            });
        } else {
            Snackbar snackbar = Snackbar.make(etOtp, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            generateOtp();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void updateUiForResend(boolean b) {
        if (b) {
            tvCounter.setVisibility(View.GONE);
            btnResendOtp.setEnabled(true);
        } else {
            tvCounter.setVisibility(View.VISIBLE);
            btnResendOtp.setEnabled(false);
            CountDownTimer timer = new CountDownTimer(2 * 60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long totalSecs = millisUntilFinished / 1000;
                    int mins = (int) (totalSecs / 60);
                    int sec = (int) (totalSecs % 60);
                    tvCounter.setText("Resend OTP in: " + String.valueOf(mins) + ":" + String.valueOf(sec));
                    if (sec < 10)
                        tvCounter.setText("Resend OTP in: " + String.valueOf(mins) + ":0" + String.valueOf(sec));
                }

                @Override
                public void onFinish() {
                    updateUiForResend(true);
                }
            }.start();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(otpReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verify_otp, menu);
        menuItem = menu.findItem(R.id.item_verify);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_verify) {
            String otp = etOtp.getText().toString();
            if (otp.trim().length() == 6 && !userId.equals("")) {
                verifyOTP(otp);
            } else {
                showOTPError();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOTPError() {
        Snackbar snackbar = Snackbar.make(etOtp, AppConstants.INCORRECTOTP, Snackbar.LENGTH_INDEFINITE)
                .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        generateOtp();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        tvMessage.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void verifyOTP(String otp) {
        if (AppHelper.isConnectingToInternet(VerifyOTPActivity.this)) {
            dialog = AppHelper.showProgressDialog(VerifyOTPActivity.this, AppConstants.VERFYOTPMSG);
            dialog.show();
            showMenu = false;
            invalidateOptionsMenu();
            WebApi api = AppHelper.setupRetrofit();
            VerifyOtpRequest request = new VerifyOtpRequest(userId, otp);
            getVerifyOtpResponseCall = api.getVerifyOtpResponseCall(request);
            getVerifyOtpResponseCall.enqueue(new Callback<VerifyOtpResponse>() {
                @Override
                public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                    dialog.dismiss();
                    showMenu = true;
                    invalidateOptionsMenu();

                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        int verified = response.body().getVerified();

                        if (status == AppConstants.SUCCESS && verified == AppConstants.SUCCESS) {

                            saveUserIdAndGoHome(response.body().getUser_id());

                        } else {
                            Toast.makeText(VerifyOTPActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("Error", t.getLocalizedMessage());
                    showMenu = true;
                    invalidateOptionsMenu();
                }
            });
        } else

        {
            Snackbar snackbar = Snackbar.make(etOtp, AppConstants.NONETWORK, Snackbar.LENGTH_INDEFINITE)
                    .setAction(AppConstants.TRYAGAIN, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (etOtp.getText().toString().trim().length() == 6 && !userId.equals(""))
                                verifyOTP(etOtp.getText().toString());
                            else {
                                showOTPError();
                            }
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView tvMessage = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tvMessage.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    private void saveUserIdAndGoHome(String user_id) {
        SharedPreferences shr = getSharedPreferences(AppConstants.USERPREFS, MODE_PRIVATE);
        SharedPreferences.Editor e = shr.edit();
        e.putString(AppConstants.USERID, user_id);
        e.apply();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem.setVisible(showMenu);
        return true;
    }

    public class OTPReceiver extends BroadcastReceiver {
        public final static String ACTION = AppConstants.OTPINTENTFILTERACTION;

        @Override
        public void onReceive(Context context, Intent intent) {
            String otp = intent.getExtras().getString(AppConstants.OTP);
            Log.i("OTP", otp);
            if (!userId.equals(""))
                verifyOTP(otp);
        }
    }

}
