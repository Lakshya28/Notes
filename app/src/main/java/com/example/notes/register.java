package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class register extends AppCompatActivity {

    EditText Username, Useremail, Userpass, UserconfirmPass;
    Button syncAccount;
    TextView login;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Create new Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Username = findViewById(R.id.userName);
        Useremail = findViewById(R.id.userEmail);
        Userpass = findViewById(R.id.password);
        UserconfirmPass = findViewById(R.id.passwordConfirm);
        syncAccount = findViewById(R.id.createAccount);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);
        fAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });
        syncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uusername = Username.getText().toString();
                String uuseremail = Useremail.getText().toString();
                String uuserpass = Userpass.getText().toString();
                String uuserconpass = UserconfirmPass.getText().toString();
                if (uusername.isEmpty() || uuseremail.isEmpty() || uuserpass.isEmpty() || uuserconpass.isEmpty()) {
                    Toast.makeText(register.this, "All Fields are required", Toast.LENGTH_SHORT).show();
                }
                if (!uuserpass.equals(uuserconpass)) {
                    UserconfirmPass.setError("Password do not match ");
                }

                AuthCredential credential = EmailAuthProvider.getCredential(uuseremail, uuserpass);
                fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(register.this, "Sucessful linked", Toast.LENGTH_SHORT).show();

                        FirebaseUser user=fAuth.getCurrentUser();
                        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                                .setDisplayName(uusername)
                                .build();
                        user.updateProfile(request);

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(register.this, "Failed to connect Try Again", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}
