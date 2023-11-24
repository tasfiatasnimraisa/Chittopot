package com.example.chittopot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class dashboard extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    FirebaseRecyclerAdapter<filemodel, Viewholder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize FirebaseDatabase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("myvideos");

        recyclerView = findViewById(R.id.recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<filemodel> options =
                new FirebaseRecyclerOptions.Builder<filemodel>()
                        .setQuery(databaseReference, filemodel.class)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<filemodel, Viewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull filemodel model) {
                        holder.setVideo(model.getTitle(), model.getVurl(), model.getCatagory());
                    }

                    @NonNull
                    @Override
                    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                        return new Viewholder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
