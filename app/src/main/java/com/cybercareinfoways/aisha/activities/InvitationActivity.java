package com.cybercareinfoways.aisha.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.model.Contacts;
import com.cybercareinfoways.helpers.AishaConstants;

public class InvitationActivity extends AppCompatActivity {
  private Contacts contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        contacts = getIntent().getParcelableExtra(AishaConstants.EXTRA_INVITATION);
        Log.v("CONATCTYYYY",contacts.toString());
    }
}
