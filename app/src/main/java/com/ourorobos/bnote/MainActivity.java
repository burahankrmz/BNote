package com.ourorobos.bnote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ourorobos.bnote.model.Notes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private List<Notes> list = new ArrayList<>();
    private NoteAdapter adapter;
    private FloatingActionButton fnoteaddbutton;
    private RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Notes");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fnoteaddbutton = findViewById(R.id.fnoteaddbutton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fnoteaddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        readData();
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialognoteadd);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);


        ImageButton dialogclosebutton = dialog.findViewById(R.id.dialogclosebutton);
        Button buttonekle = dialog.findViewById(R.id.buttonekle);
        final EditText writenote = dialog.findViewById(R.id.writenote);

        dialogclosebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(writenote.getText())) {
                    writenote.setError("Boş bırakamazsın cano");
                }
                else {
                    addDataToFirebase(writenote.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void addDataToFirebase(String text) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Notes");
        String id = myRef.push().getKey();
        Notes notes = new Notes(id,text);

        myRef.child(id).setValue(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Başarıyla Ekledin",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void readData() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Notes value = snapshot.getValue(Notes.class);
                    list.add(value);
                }
                adapter = new NoteAdapter(MainActivity.this,list);
                recyclerView.setAdapter(adapter);
                setClick();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tag", "Failed to read value.", error.toException());
            }
        });
    }

    private void setClick() {
        adapter.setOnCallBack(new NoteAdapter.OnCallBack() {
            @Override
            public void onButtonDeleteClick(Notes notes) {
                deletNote(notes);
            }

            @Override
            public void onButtonEditClick(Notes notes) {
                showUpdateDialog(notes);
            }
        });
    }

    private void showUpdateDialog(Notes notes) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialognoteadd);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);


        ImageButton dialogclosebutton = dialog.findViewById(R.id.dialogclosebutton);
        Button buttonekle = dialog.findViewById(R.id.buttonekle);
        final EditText writenote = dialog.findViewById(R.id.writenote);
        writenote.setText(notes.getText());

        dialogclosebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(writenote.getText())) {
                    writenote.setError("Boş bırakamazsın cano");
                }
                else {
                    updateNote(notes,writenote.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void updateNote(Notes notes, String newtext) {
        myRef.child(notes.getId()).child("text").setValue(newtext).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Başarıyla düzenlendi.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletNote(Notes notes) {
        myRef.child(notes.getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError  error, @NonNull  DatabaseReference ref) {
                Toast.makeText(getApplicationContext(),"Başarıyla Silindi",Toast.LENGTH_SHORT).show();
            }
        });
    }
}