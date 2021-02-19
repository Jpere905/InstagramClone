package com.example.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

// in this class, the user's login credentials are gathered and then used to log them in
// there is error checking involved, so that if bad credentials are provided, the user won't be
// logged in. If login is successful, then the application will use a new intent to go to
// MainActivity
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    // grab references from the three widgets on the login screen
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check if user is already logged in. if so, do not show login screen, proceed directly to
        // MainActivity
        if (ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "username or password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login as user " + username);

        // this will log in the user
        // LogInInBackground will execute on a different thread from the UI thread,
        // therefore allowing the application to continue running other tasks.
        // i.e. it doesn't keep the user waiting on the login page
        // for the login process to complete (it's asynchronous)
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null ){
                    Log.e(TAG, "Issue with logging in", e);
                    Toast.makeText(LoginActivity.this, "Login fail, check username & password", Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // use the intent system in android to navigate to the next activity
    private void goMainActivity() {
        // 'this' <- the context, which is referring to 'LoginActivity' which is an instance of a context
        // MainActivity.class <- the class where you want to navigate to
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();

    }
}
