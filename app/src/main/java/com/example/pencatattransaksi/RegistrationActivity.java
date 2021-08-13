package com.example.pencatattransaksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    EditText mEmail;
    EditText mPassword;
    EditText mPasswordConfirm;
    Button btnReg;
    TextView mSignin;
    FirebaseAuth mAuth;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_registration);

        mEmail = findViewById(R.id.email_regis);
        mPassword = findViewById(R.id.password_regis);
        mPasswordConfirm = findViewById(R.id.pastikanpassword_regis);
        btnReg = findViewById(R.id.regisbtn_ragis);
        mSignin = findViewById(R.id.masuk_regis);
        pb = findViewById(R.id.progressBar3);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String confirmpassword = mPasswordConfirm.getText().toString();

                mAuth = FirebaseAuth.getInstance();

                if (email.isEmpty()){
                    mEmail.setError("Masukan Email");
                    pb.setVisibility(View.INVISIBLE);
                    return;
                }

                if (password.isEmpty()){
                    mPassword.setError("Password tidak boleh kosong");
                    pb.setVisibility(View.INVISIBLE);
                    return;
                }

                if (!password.equals(confirmpassword)){
                    mPasswordConfirm.setError("Password tidak sama");
                    pb.setVisibility(View.INVISIBLE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pb.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegistrationActivity.this,"Berhasil Buat Akun", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pb.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}