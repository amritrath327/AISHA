package com.cybercareinfoways.aisha.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cybercareinfoways.aisha.R;
import com.cybercareinfoways.aisha.fragments.ContactsFragment;
import com.cybercareinfoways.aisha.fragments.ProfileFragment;
import com.cybercareinfoways.aisha.services.SyncTokenService;
import com.cybercareinfoways.helpers.AishaUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    BottomNavigationView navigationView;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AISHA");
        fragmentManager = getSupportFragmentManager();

        sendTokenServiceCall();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.history:

                        break;
                    case R.id.contacts:
                        fragmentManager.beginTransaction().replace(R.id.content, new ContactsFragment(), "ContactFrag")
                                .commit();
                        break;
                    case R.id.profile:
                        fragmentManager.beginTransaction().replace(R.id.content, new ProfileFragment(), "ProfileFragment")
                                .commit();
                        break;

                }
                return true;
            }
        });
    }

    private void sendTokenServiceCall() {
        if (AishaUtilities.isConnectingToInternet(this)) {
            Intent i = new Intent(getApplicationContext(), SyncTokenService.class);
            startService(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.item_contact) {
            Intent intent = new Intent(HomeActivity.this, ContactsActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.item_new) {
            Intent intent = new Intent(HomeActivity.this, NewContactsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
