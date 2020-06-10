package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
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

public class editnote extends AppCompatActivity {

    Intent data;
    EditText editnotetitle,editnotecontent;
    FirebaseFirestore fstore;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        editnotetitle=findViewById(R.id.editnotetitle);
        editnotecontent=findViewById(R.id.editnotecontent);
        Toolbar toolbar=findViewById(R.id.edittoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fstore=fstore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();

        data=getIntent();
        String notetitle=data.getStringExtra("title");
        String notecontent=data.getStringExtra("content");

        editnotetitle.setText(notetitle);
        editnotecontent.setText(notecontent);
        FloatingActionButton fab = findViewById(R.id.editnotefab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ncontent=editnotecontent.getText().toString();
                String ntitle=editnotetitle.getText().toString();

                if(ncontent.isEmpty() || ntitle.isEmpty())
                {
                    Toast.makeText(editnote.this, "Cannot save note with empty fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                //edit note
                DocumentReference docref=fstore.collection("notes").document(user.getUid()).collection("mynotes").document(data.getStringExtra("noteid"));
                Map<String,Object> note =new HashMap<>();
                note.put("title",ntitle);
                note.put("content",ncontent);
                docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(editnote.this, "Note Updated sucessfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(editnote.this,MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editnote.this, "Error Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }
}
