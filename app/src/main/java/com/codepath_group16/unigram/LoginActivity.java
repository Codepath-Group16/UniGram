package com.codepath_group16.unigram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity
{
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null)
        {
            goMainActivity();
        }


        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.log_out);
        Button btnSignup = findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signUpUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password)
    {
        Log.i(TAG, "Attempting to login user " + username);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                   if(e != null)
                   {
                       Log.e(TAG, "Issue with login", e);
                       return;
                   }
                   goMainActivity();
                   Toast.makeText(com.codepath_group16.unigram.LoginActivity.this, "Success!", Toast.LENGTH_SHORT);
            }

        });
    }
    private void signUpUser(final String username, String password)
    {
        Log.i(TAG, "Attempting to sign up user " + username);

        ParseUser user = new ParseUser();
// Set the user's username and password, which can be obtained by a forms
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with Signup", e);
                }
                Toast.makeText(com.codepath_group16.unigram.LoginActivity.this, "Welcome " + username, Toast.LENGTH_LONG).show();
                goMainActivity();
            }
        });
    }


    private void goMainActivity() {
        Intent i = new Intent(com.codepath_group16.unigram.LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}