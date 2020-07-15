package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText Emailinput, Passwordinput, Confirmpassword;
    Button regbt,regloginbt;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Emailinput=findViewById(R.id.Register_email);
        Passwordinput=findViewById(R.id.register_password);
        Confirmpassword=findViewById(R.id.register_confrmpass);
        regbt=findViewById(R.id.reg_button);
        regloginbt=findViewById(R.id.log_button);
        progressBar=findViewById(R.id.progressBar2);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference=firebaseDatabase.getReference("users");

        regloginbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                }
        });
        regbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=Emailinput.getText().toString();
                String password=Passwordinput.getText().toString();

                final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                    else
                                    {
                                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                        Toast.makeText(RegisterActivity.this,"Please check email verification",Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

    }
}