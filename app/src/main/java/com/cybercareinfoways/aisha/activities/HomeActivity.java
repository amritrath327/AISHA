package com.cybercareinfoways.aisha.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cybercareinfoways.aisha.R;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AISHA");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.v("Token==>",token);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.item_contact){
            Intent intent = new Intent(HomeActivity.this,ContactsActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId()==R.id.item_new){
            Intent intent = new Intent(HomeActivity.this,NewContactsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
