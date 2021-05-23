package com.renaud.appsante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;

public class ProfileActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView fullName, dateCreation;

    TextInputEditText NASEditText, firstNameEditText, lastNameEditText,
            birthDateEditText, emailEditText, phoneEditText;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        //
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        fullName = findViewById(R.id.full_name);
        dateCreation = findViewById(R.id.card_description);

        //
        NASEditText = findViewById(R.id.edit_text_nas);
        firstNameEditText = findViewById(R.id.edit_text_first_name);
        lastNameEditText = findViewById(R.id.edit_text_last_name);
        birthDateEditText = findViewById(R.id.edit_text_birth_date);
        emailEditText = findViewById(R.id.edit_text_email);
        phoneEditText = findViewById(R.id.edit_text_phone);

        //
        setSupportActionBar(toolbar);

        //
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NASEditText.setText(ParseUser.getCurrentUser().get("NAS").toString());
        lastNameEditText.setText(ParseUser.getCurrentUser().get("lastName").toString());
        firstNameEditText.setText(ParseUser.getCurrentUser().get("firstName").toString());
        birthDateEditText.setText(new SimpleDateFormat("dd/MM/yyyy").format(ParseUser.getCurrentUser().get("birthDate")));
        emailEditText.setText(ParseUser.getCurrentUser().getUsername());
        phoneEditText.setText(ParseUser.getCurrentUser().get("phone").toString());

        fullName.setText((ParseUser.getCurrentUser().get("lastName").toString() + " " + ParseUser.getCurrentUser().get("firstName").toString()));
        dateCreation.setText(new SimpleDateFormat("dd/MM/yyyy").format(ParseUser.getCurrentUser().getCreatedAt()));

        //
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home :
                        Intent intentHome = new Intent(ProfileActivity.this, DashboardActivity.class);
                        startActivity(intentHome);
                        break;
                    case R.id.nav_ask_permit :
                        Toast.makeText(getApplicationContext(), "Asking for Permit", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_logout :
                        ParseUser.logOut();
                        ActivityCompat.finishAffinity(ProfileActivity.this);
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_profile :
                        break;
                    case R.id.nav_share:
                        Toast.makeText(getApplicationContext(), "Share your success !!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_rate:
                        Toast.makeText(getApplicationContext(), "10/10 would gladly recommend !", Toast.LENGTH_SHORT).show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navigationView.setCheckedItem(R.id.nav_home);
    }

    public void goHome(View view)
    {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}