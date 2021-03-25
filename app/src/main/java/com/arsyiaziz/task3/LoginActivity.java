package com.arsyiaziz.task3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView tvRegisterLink;
    private TextView tvResetLink;
    private TextInputLayout inputLayoutEmail;
    private TextInputEditText inputEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputEditText inputPassword;
    private Button btnLogin;
    private CheckBox checkboxRememberMe;
    private ConstraintLayout progressOverlay;

    private static final String PREFS_NAME = "preferences";
    private static final String PREF_EMAIL = "Email";
    private static final String PREF_PASSWORD = "Password";
    private static final String PREF_REMEMBER = "Remember";


    private final String defaultEmailValue = "";
    private final String defaultPasswordValue = "";
    private final Boolean defaultRememberValue = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        progressOverlay = findViewById(R.id.progress_overlay);

        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutPassword = findViewById(R.id.input_layout_password);

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
        tvResetLink = findViewById(R.id.tv_reset_password_link);
        checkboxRememberMe = findViewById(R.id.checkbox_remember_me);

        tvResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
                startActivity(intent);
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class) ;
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressOverlay.setVisibility(View.VISIBLE);
                System.out.println(progressOverlay.getVisibility());
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                inputLayoutEmail.setError(null);
                inputLayoutPassword.setError(null);
                if (email.isEmpty() && password.isEmpty()) {
                    inputLayoutEmail.setError("Please input your email address");
                    inputLayoutPassword.setError("Please input your password");
                    inputEmail.requestFocus();

                } else if (password.isEmpty()) {
                    inputLayoutPassword.setError("Please input your password");
                    inputPassword.requestFocus();

                } else if (email.isEmpty()) {
                    inputLayoutEmail.setError("Please input your email");
                    inputEmail.requestFocus();

                } else if (!isEmailValid(email)) {
                    inputLayoutEmail.setError("Please input a valid email address");
                    inputEmail.requestFocus();
                } else if (!(email.isEmpty() && password.isEmpty())) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (checkboxRememberMe.isChecked()) {
                                    savePreferences(email, password, true);
                                } else {
                                    savePreferences(null, null, false);
                                }
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "An error has occurred, please try again", Toast.LENGTH_SHORT).show();
                }
                progressOverlay.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser currentUser){

        if(currentUser != null){
            Toast.makeText(this,"You have successfully signed in",Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }

    private void savePreferences(String emailValue, String passwordValue, Boolean rememberValue) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(PREF_EMAIL, emailValue);
        editor.putString(PREF_PASSWORD, passwordValue);
        editor.putBoolean(PREF_REMEMBER, rememberValue);

        editor.commit();
    }

    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        String emailValue = settings.getString(PREF_EMAIL, defaultEmailValue);
        String passwordValue = settings.getString(PREF_PASSWORD, defaultPasswordValue);
        Boolean rememberValue = settings.getBoolean(PREF_REMEMBER, defaultRememberValue);

        inputEmail.setText(emailValue);
        inputPassword.setText(passwordValue);
        checkboxRememberMe.setChecked(rememberValue);
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}