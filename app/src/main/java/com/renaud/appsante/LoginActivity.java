package com.renaud.appsante;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    ImageView image;
    TextView title, slogan;
    TextInputLayout inputEmail, inputPassword;
    Button signInButton, goSignUpButton;

    TextInputEditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        image = findViewById(R.id.logo_image);
        title = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.slogan_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        signInButton = findViewById(R.id.sign_in_button);
        goSignUpButton = findViewById(R.id.go_sign_up_button);

        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);

    }

    public void login(View view)
    {
        if (!emailEditText.getText().toString().equals("") &&
                !passwordEditText.getText().toString().equals(""))
        {
            ParseUser.logInInBackground(emailEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e == null){
                        Log.i("Login", "Success!");
                        emailEditText.setText("");
                        passwordEditText.setText("");
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Account not found !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Fill the form", Toast.LENGTH_SHORT).show();
        }
    }

    public void goSignUp(View view)
    {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(image, "logo_image");
        pairs[1] = new Pair<View, String>(title, "logo_name");
        pairs[2] = new Pair<View, String>(slogan, "logo_slogan");
        pairs[3] = new Pair<View, String>(inputEmail, "courriel_tran");
        pairs[4] = new Pair<View, String>(inputPassword, "password_tran");
        pairs[5] = new Pair<View, String>(signInButton, "button_tran");
        pairs[6] = new Pair<View, String>(goSignUpButton, "login_signup_tran");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
        startActivity(intent, options.toBundle());
    }
}