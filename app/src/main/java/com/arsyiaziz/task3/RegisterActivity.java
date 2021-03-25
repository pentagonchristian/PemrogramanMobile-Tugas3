package com.arsyiaziz.task3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView tvLoginLink;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutPasswordRepeat;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private TextInputEditText inputPasswordRepeat;
    private Button btnRegister;
    private ConstraintLayout progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        progressOverlay = findViewById(R.id.progress_overlay);

        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputLayoutPasswordRepeat = findViewById(R.id.input_layout_password_repeat);

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputPasswordRepeat = findViewById(R.id.input_password_repeat);

        btnRegister = findViewById(R.id.btn_register);

        tvLoginLink = findViewById(R.id.tv_login_link);
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressOverlay.setVisibility(View.VISIBLE);
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String passwordRepeat = inputPasswordRepeat.getText().toString();

                inputLayoutEmail.setError(null);
                inputLayoutPassword.setError(null);
                inputLayoutPasswordRepeat.setError(null);

                if (email.isEmpty() && password.isEmpty()) {
                    inputLayoutEmail.setError("Please input your email address");
                    inputLayoutPassword.setError("Please input a password");
                    inputEmail.requestFocus();

                } else if (password.isEmpty()) {
                    inputLayoutPassword.setError("Please input a password");
                    inputPassword.requestFocus();

                } else if (email.isEmpty()) {
                    inputLayoutEmail.setError("Please input your email address");
                    inputEmail.requestFocus();
                } else if (!isEmailValid(email)) {
                    inputLayoutEmail.setError("Please input a valid email address");
                    inputEmail.requestFocus();
                } else if (!(email.isEmpty() && password.isEmpty())) {
                    if (password.equals(passwordRepeat)) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        inputLayoutPasswordRepeat.setError("The passwords are not the same");
                        inputPasswordRepeat.requestFocus();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "An error has occurred, please try again", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));

        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}