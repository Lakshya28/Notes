package com.example.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notes.model.Note;
import com.example.notes.model.adapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    RecyclerView noteslist;
    adapter proadapter;
    FirebaseFirestore fstore;
    FirebaseUser user;
    FirebaseAnalytics firebaseAnalytics;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fstore = FirebaseFirestore.getInstance();
        firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        user = FirebaseAuth.getInstance().getCurrentUser();

        Query query = fstore.collection("notes").document(user.getUid()).collection("mynotes").orderBy("title", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.notecontent.setText(note.getContent());
                noteViewHolder.notetitle.setText(note.getTitle());
                final String noteid = noteAdapter.getSnapshots().getSnapshot(i).getId();
                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), notedetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("noteid", noteid);

                        view.getContext().startActivity(i);
                    }
                });

                ImageView menuicon = noteViewHolder.itemView.findViewById(R.id.note_option);
                menuicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final String docid = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(view.getContext(), view);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Intent i = new Intent(view.getContext(), editnote.class);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                i.putExtra("noteid", docid);
                                startActivity(i);
                                return false;
                            }
                        });
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("mynotes").document(docid);
                                docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Deleted Sucessfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error in deleting note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        menu.show();
                        //Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };


        drawerLayout = findViewById(R.id.drawer);
        noteslist = findViewById(R.id.recycler_view);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        noteslist.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteslist.setAdapter(noteAdapter);

        FloatingActionButton fab_contentmain = findViewById(R.id.fab_contentmain);
        fab_contentmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, addnote.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        switch (menuitem.getItemId()) {
            case R.id.nav_addnew:
                startActivity(new Intent(this, addnote.class));
                break;
            case R.id.nav_logout:
                checkuser();
                break;
            case R.id.nav_sync:
                if (user.isAnonymous()) {
                    startActivity(new Intent(getApplicationContext(), login.class));
                } else {
                    Toast.makeText(this, "You are Connected", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Welcome to Notes", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkuser() {
        //if user is real or not
        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            displayalert();
        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, splash.class));
            finish();
        }
    }

    private void displayalert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure ? ")
                .setMessage("you are logged in with temperory account.Logging out will delete all the notes ")
                .setPositiveButton("Sync Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //todo: delete all the notes
                        // todo: delete anon user
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), splash.class));
                                finish();
                            }
                        });
                    }
                });
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings)
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView notetitle;
        public TextView notecontent;
        View view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notecontent = itemView.findViewById(R.id.note_text);
            notetitle = itemView.findViewById(R.id.note_title);
            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null)
            noteAdapter.stopListening();
    }
}
