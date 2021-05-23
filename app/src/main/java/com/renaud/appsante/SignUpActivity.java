package com.renaud.appsante;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.renaud.appsante.model.Citizen;
import com.renaud.appsante.service.CallRestApi;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    ImageView image;
    TextView title, slogan;
    TextInputLayout inputEmail, inputPassword;
    TextInputEditText NASEditText, firstNameEditText, lastNameEditText,
            birthDateEditText, emailEditText, phoneEditText, passwordEditText;
    Button signUpButton, goSignInButton;

    private Citizen citizen;

    @SuppressLint("SimpleDateFormat")
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        image = findViewById(R.id.logo_image);
        title = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.slogan_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        signUpButton = findViewById(R.id.sign_up_button);
        goSignInButton = findViewById(R.id.go_sign_in_button);

        NASEditText = findViewById(R.id.edit_text_nas);
        firstNameEditText = findViewById(R.id.edit_text_first_name);
        lastNameEditText = findViewById(R.id.edit_text_last_name);
        birthDateEditText = findViewById(R.id.edit_text_birth_date);
        emailEditText = findViewById(R.id.edit_text_email);
        phoneEditText = findViewById(R.id.edit_text_phone);
        passwordEditText = findViewById(R.id.edit_text_password);

        citizen = new Citizen();
    }

    public void subscribe(View view) {

        if (!NASEditText.getText().toString().equals("") &&
                !firstNameEditText.getText().toString().equals("") &&
                !lastNameEditText.getText().toString().equals("") &&
                !birthDateEditText.getText().toString().equals("") &&
                !emailEditText.getText().toString().equals("") &&
                !phoneEditText.getText().toString().equals("") &&
                !passwordEditText.getText().toString().equals(""))
        {
            if (birthDateEditText.getText().toString().matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {

                citizen.setNAS(NASEditText.getText().toString());
                citizen.setFirstName(firstNameEditText.getText().toString());
                citizen.setLastName(lastNameEditText.getText().toString());
                citizen.setBirthDate(LocalDate.parse(birthDateEditText.getText().toString(), dateTimeFormatter));
                citizen.setPassword(passwordEditText.getText().toString());
                citizen.setPhoneNumber(phoneEditText.getText().toString());
                citizen.setEmail(emailEditText.getText().toString());

                checkCitizenValidity();
            } else {
                Toast.makeText(getApplicationContext(), "Please respect the date format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Fill the form", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCitizenValidity() {
        try {
            CallRestApi callRestApi = new CallRestApi(getApplicationContext(), citizen, new CallRestApi.AsyncResponse() {
                @Override
                public void processSuccessful(Boolean output) {
                    if (output)
                        createUser();
                    else
                        Toast.makeText(getApplicationContext(), "INVALID DATA", Toast.LENGTH_SHORT).show();
                }
            });
            callRestApi.execute("http://10.0.2.2:9090/appSante/isCitizenValid");
        } catch (Exception e) {
            Log.i("ButtonError", e.getMessage());
        }
    }

    public void createUser() {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(citizen.getEmail());
        parseUser.setPassword(citizen.getPassword());
        parseUser.put("NAS", citizen.getNAS());
        parseUser.put("firstName", citizen.getFirstName());
        parseUser.put("lastName", citizen.getLastName());
        parseUser.put("phone", citizen.getPhoneNumber());
        parseUser.put("birthDate", Date.from(citizen.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.i("INFOS", e.getMessage().substring(e.getMessage().indexOf(" ")));
                    Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goSignIn(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(image, "logo_image");
        pairs[1] = new Pair<View, String>(title, "logo_name");
        pairs[2] = new Pair<View, String>(slogan, "logo_slogan");
        pairs[3] = new Pair<View, String>(inputEmail, "courriel_tran");
        pairs[4] = new Pair<View, String>(inputPassword, "password_tran");
        pairs[5] = new Pair<View, String>(signUpButton, "button_tran");
        pairs[6] = new Pair<View, String>(goSignInButton, "login_signup_tran");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
        startActivity(intent, options.toBundle());
    }
}