package com.arsyiaziz.task3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView tvLoginLink;
    private TextInputLayout inputLayoutEmail;
    private TextInputEditText inputEmail;
    private Button btnReset;
    private ConstraintLayout progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        mAuth = FirebaseAuth.getInstance();
        progressOverlay = findViewById(R.id.progress_overlay);


        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputEmail = findViewById(R.id.input_email);
        btnReset = findViewById(R.id.btn_reset);

        tvLoginLink = findViewById(R.id.tv_login_link);
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressOverlay.setVisibility(View.VISIBLE);
                String email = inputEmail.getText().toString();

                if (!isEmailValid(email)) {
                    inputLayoutEmail.setError("Please input a valid email");
                    inputEmail.requestFocus();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetActivity.this, "A reset link has been sent to "+email, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ResetActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
            startActivity(new Intent(ResetActivity.this, HomeActivity.class));
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}