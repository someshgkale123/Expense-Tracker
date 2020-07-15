package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
    EditText Emailinput, Passwordinput;
    Button loginbt, registerbt;
    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Emailinput=findViewById(R.id.log_email);
        Passwordinput=findViewById(R.id.log_password);
        loginbt=findViewById(R.id.log_button1);
        registerbt=findViewById(R.id.reg_button1);
        progressBar=findViewById(R.id.progressBar);

        firebaseAuth=FirebaseAuth.getInstance();
        registerbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        loginbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Emailinput.getText().toString().isEmpty() && !Passwordinput.getText().toString().isEmpty())
                {

                    validate(Emailinput.getText().toString(),Passwordinput.getText().toString());
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Please Fill out the boxes",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void validate(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "email or password is incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    checkifEmailVerified();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

        });
    }
    private void checkifEmailVerified()
    {
        FirebaseUser user=firebaseAuth.getCurrentUser();

        assert user!=null;
        if (user.isEmailVerified())
        {

            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else
        {
            Toast.makeText(this, "check your email", Toast.LENGTH_SHORT).show();
        }

    }
}
