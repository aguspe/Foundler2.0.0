package com.example.foundlerv2.Matches;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.foundlerv2.LoginActivity;
import com.example.foundlerv2.MainActivity;
import com.example.foundlerv2.R;
import com.example.foundlerv2.StartUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdaptor;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private Button mMatchesBack;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdaptor = new MatchesAdaptor(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdaptor);

        getUserMatchId();


        mMatchesBack = (Button) findViewById(R.id.button4);
        mMatchesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            private void FetchMatchInformation(String key) {
                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String userId = dataSnapshot.getKey();
                            String name = "";
//                            String phone = "";
                            String profilePictureUrl = "";
                            if (dataSnapshot.child("name").getValue() != null){
                                    name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            }
//                            if (dataSnapshot.child("phone").getValue() != null){
//                                name = Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
//                            }
                            if (dataSnapshot.child("profilePictureUrl").getValue() != null){
                                profilePictureUrl = Objects.requireNonNull(dataSnapshot.child("profilePictureUrl").getValue()).toString();
                            }

                            MatchesObject obj = new MatchesObject(userId, name, profilePictureUrl);
                            resultMatches.add(obj);
                            mMatchesAdaptor.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultMatches;
    }
}
