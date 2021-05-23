package com.renaud.appsante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.renaud.appsante.model.Permit;
import com.renaud.appsante.service.CallRestApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    LinearLayout nbrDoseLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    CardView validPermitCard, infosPermitCard;
    TextView validPermitTitle, typePermit, dateDebut, dateExpiration, nbrDose;
    ImageView QRCode;
    Bitmap bitmapQR;

    Permit permit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        //
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        infosPermitCard = findViewById(R.id.card_view_info_permit);
        validPermitTitle = findViewById(R.id.text_valid_permit);
        validPermitCard = findViewById(R.id.card_view_valid_permit);
        nbrDoseLayout = findViewById(R.id.layout_nbr_dose);
        nbrDose = findViewById(R.id.text_nbr_dose);
        typePermit = findViewById(R.id.text_type_permit);
        dateDebut = findViewById(R.id.text_date_debut);
        dateExpiration = findViewById(R.id.text_date_expiration);
        QRCode = findViewById(R.id.image_view_code_qr);

        permit = new Permit();

        checkForPermit();
        //
        setSupportActionBar(toolbar);

        //
        Menu menu = navigationView.getMenu();

        //
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_ask_permit:
                        getPermit();
                        break;
                    case R.id.nav_profile:
                        Intent intentProfile = new Intent(DashboardActivity.this, ProfileActivity.class);
                        startActivity(intentProfile);
                        break;
                    case R.id.nav_logout:
                        ParseUser.logOut();
                        ActivityCompat.finishAffinity(DashboardActivity.this);
                        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                        startActivity(intent);
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

    private void checkForPermit() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Permits");
        query.whereContains("citizen", ParseUser.getCurrentUser().getString("NAS"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    displayPermit(object);
                } else
                    Toast.makeText(getApplicationContext(), "No current permit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void displayPermit(ParseObject object) {
        if (object != null) {
            boolean isActive = checkPermitValidity(object);

            if (isActive) {
                validPermitTitle.setText("Active Permit");
                validPermitCard.setCardBackgroundColor(Color.GREEN);
            } else {
                validPermitTitle.setText("Inactive Permit");
                validPermitCard.setCardBackgroundColor(Color.RED);
            }
            typePermit.setText(object.getString("type"));

            if (object.getString("type").equals(Permit.TYPE_VACCIN)) {
                nbrDose.setText(object.getInt("nbrDose"));
                nbrDoseLayout.setVisibility(View.VISIBLE);
            }
            dateDebut.setText(new SimpleDateFormat("dd/MM/yyyy").format(object.get("dateTest")));
            dateExpiration.setText(new SimpleDateFormat("dd/MM/yyyy").format(object.get("dateExpiration")));

            infosPermitCard.setVisibility(View.VISIBLE);

            bitmapQR = BitmapFactory.decodeByteArray(object.getBytes("QRCode"), 0, object.getBytes("QRCode").length);
            QRCode.setImageBitmap(Bitmap.createScaledBitmap(bitmapQR, QRCode.getWidth(), QRCode.getHeight(), false));
        } else {
            Toast.makeText(getApplicationContext(), "No current permit", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermitValidity(ParseObject object)
    {
        boolean isActive;

        if (object.getDate("dateExpiration").after(Date.from(Instant.now()))) {
            return object.getBoolean("isActive");
        } else {
            isActive = false;

            object.put("isActive", isActive);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("UPDATE PERMIT", "COMPLETE");
                    } else {
                        Toast.makeText(getApplicationContext(), "An Error occured ...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return false;
        }
    }

    private void getPermit() {
        try {
            CallRestApi callRestApi = new CallRestApi(getApplicationContext(), permit, new CallRestApi.AsyncResponse() {
                @Override
                public void processSuccessful(Boolean output) {
                    if (output)
                        savePermit();
                    else
                        Toast.makeText(getApplicationContext(), "Sorry, No new valid permit found ...", Toast.LENGTH_SHORT).show();
                }
            });
            callRestApi.execute("http://10.0.2.2:9090/appSante/getCredentialsPermit/" + ParseUser.getCurrentUser().get("NAS"));
        } catch (Exception e) {
            Log.i("ERROR", e.getMessage());
        }
    }

    private void savePermit() {
        ParseObject parsePermit = new ParseObject("Permits");
        parsePermit.put("type", permit.getType());
        if (permit.getType().equals(Permit.TYPE_VACCIN))
            parsePermit.put("nbrDose", permit.getNbrDose());
        parsePermit.put("dateTest", Date.from(permit.getDateTest().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        parsePermit.put("dateExpiration", Date.from(permit.getDateExpiration().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        parsePermit.put("isActive", permit.isActive());
        parsePermit.put("citizen", ParseUser.getCurrentUser().getString("NAS"));
        parsePermit.put("QRCode", permit.getQrCode());
        parsePermit.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Permit created", Toast.LENGTH_SHORT).show();
                    checkForPermit();
                } else
                    Toast.makeText(getApplicationContext(), "An error occured when saving ...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void confirmSaveImage(View view) {
        if (bitmapQR != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save QR code to phone storage ?");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveToGallery();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else
            Toast.makeText(getApplicationContext(), "Please ask for a permit first", Toast.LENGTH_SHORT).show();
    }

    private void saveToGallery() {
        OutputStream output;

        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures");

        if (!dir.exists()) {
            if (!dir.mkdirs())
                Toast.makeText(getApplicationContext(), "Couldn't create the directory", Toast.LENGTH_SHORT).show();
        }

        File file = new File(dir, "qrCode.jpeg");
        try {
            output = new FileOutputStream(file);

            bitmapQR.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaStore.Images.Media.insertImage(getContentResolver(), bitmapQR,
                "QR_"+ dateDebut.getText().toString() +
                        ".jpeg", "qrCode");

    }

    public void askPermit(View view) {
        getPermit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}