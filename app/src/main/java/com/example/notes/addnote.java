package com.example.notes;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addnote extends AppCompatActivity {

    FirebaseFirestore fstore;
    EditText addnotecontent,addnotetitle;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);
        Toolbar toolbar = findViewById(R.id.toolbar);
       // toolbar.setTitle(" fssdf ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fstore=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        addnotetitle=findViewById(R.id.addnote_title);
        addnotecontent=findViewById(R.id.addnote_content);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ncontent=addnotecontent.getText().toString();
                String ntitle=addnotetitle.getText().toString();

                if(ncontent.isEmpty() || ntitle.isEmpty())
                {
                    Toast.makeText(addnote.this, "Cannot save note with empty fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                //save note
                DocumentReference docref=fstore.collection("notes").document(user.getUid()).collection("mynotes").document();
                Map<String,Object>note =new HashMap<>();
                note.put("title",ntitle);
                note.put("content",ncontent);
                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(addnote.this, "Note addded sucessfuly", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addnote.this, "Error Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
