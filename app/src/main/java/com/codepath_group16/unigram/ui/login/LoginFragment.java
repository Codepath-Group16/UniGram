package com.codepath_group16.unigram.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.codepath_group16.unigram.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity(view);
        }


        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.log_out);
        Button btnSignup = view.findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password, view);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signUpUser(username, password, view);
            }
        });
    }

    private void loginUser(String username, String password, View view) {
        Log.i(TAG, "Attempting to login user " + username);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                goMainActivity(view);
                Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void signUpUser(final String username, String password, View view) {
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
                Toast.makeText(requireContext(), "Welcome " + username, Toast.LENGTH_LONG).show();
                goMainActivity(view);
            }
        });
    }


    private void goMainActivity(View view) {
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_navigation_feed);
        Navigation.findNavController(view).popBackStack(R.id.navigation_login, true);
    }
}