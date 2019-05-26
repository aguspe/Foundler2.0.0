package com.example.foundlerv2;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Cards cards_data[];
    private ArrayAdaptor arrayAdapter;
    private int i;
    private DatabaseReference usersDb;
    private String currentUid;

    ListView listView;
    List<Cards> rowItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        //add the view via xml or programmatically
                checkUserGender();

                rowItems = new ArrayList<Cards>();

                arrayAdapter = new ArrayAdaptor(this, R.layout.item, rowItems);

                SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
                //set the listener and the adapter
                flingContainer.setAdapter(arrayAdapter);
                flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                    @Override
                    public void removeFirstObjectInAdapter() {
                        // this is the simplest way to delete an object from the Adapter (/AdapterView)
                        Log.d("LIST", "removed object!");
                        rowItems.remove(0);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLeftCardExit(Object dataObject) {
                        //Do something on the left!
                        //You also have access to the original object.
                        //If you want to use it just cast it (String) dataObject
                        Cards obj = (Cards) dataObject;
                        String userId = obj.getUserId();
                        usersDb.child(userId).child("connections").child("No").child(currentUid).setValue(true);
                        Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRightCardExit(Object dataObject) {
                        Cards obj = (Cards) dataObject;
                        String userId = obj.getUserId();
                        usersDb.child(userId).child("connections").child("Yes").child(currentUid).setValue(true);
                        isConnectionMatch(userId);
                        Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    }

                    @Override
                    public void onScroll(float v) {

                    }
                });

                // Optionally add an OnItemClickListener
                flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClicked(int itemPosition, Object dataObject) {
                        Toast.makeText(MainActivity.this, "Click!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUid).child("connections").child("Yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(MainActivity.this, "It's a match", Toast.LENGTH_LONG).show();
                    usersDb.child(Objects.requireNonNull(dataSnapshot.getKey())).child("connections").child("matches").child(currentUid).setValue(true);
                    usersDb.child(currentUid).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  String userGender;
    private  String differentUserGender;
       public void checkUserGender () {
           final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
           assert user != null;
           DatabaseReference userDb = usersDb.child(user.getUid());
           userDb.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if (dataSnapshot.exists()){
                       if (dataSnapshot.child("gender").getValue() != null){
                           userGender = Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString();
                           switch (userGender){
                               case "Male":
                                   differentUserGender = "Female";
                                   break;
                               case "Female":
                                   differentUserGender = "Male";
                                   break;
                           }
                           getDifferentGenderUsers();
                            }
                        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
       }

       public void getDifferentGenderUsers(){
           usersDb.addChildEventListener(new ChildEventListener() {
               @Override
               @RequiresApi(api = Build.VERSION_CODES.KITKAT)
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                   Log.d("gender", differentUserGender);
                   Log.d("gender2", Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString());
                   if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("No").hasChild(currentUid) && !dataSnapshot.child("connections").child("Yes").hasChild(currentUid)
                   && Objects.equals(Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString(), differentUserGender)){
                        String profilePictureUrl = "default";
                       if(dataSnapshot.child("profilePictureUrl").getValue()!= null) {
                           if (!Objects.equals(dataSnapshot.child("profilePictureUrl").getValue(), "default")) {
                               profilePictureUrl = Objects.requireNonNull(dataSnapshot.child("profilePictureUrl").getValue()).toString();
                           }
                       }
                       Log.d("tag", profilePictureUrl);
                       Cards Item = new Cards(dataSnapshot.getKey(), Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), profilePictureUrl);
                       Log.d("Called", "This is called");
                       rowItems.add(Item);
                       arrayAdapter.notifyDataSetChanged();
                   }
               }

               @Override
               public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               }

               @Override
               public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

               }

               @Override
               public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }
    public void LogOut(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, StartUpActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
