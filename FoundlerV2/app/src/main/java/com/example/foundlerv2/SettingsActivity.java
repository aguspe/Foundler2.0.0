package com.example.foundlerv2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField;
    private Button mBack, mSave;
    private ImageView mProfilePicture;

    private FirebaseAuth mAuth;
    private DatabaseReference mEnjoyerDatabase;
    private String userId, name, phone, profilePictureUrl;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mProfilePicture = (ImageView) findViewById(R.id.profilePicture);
        mSave = (Button) findViewById(R.id.save);
        mBack = (Button) findViewById(R.id.back);

        String userGender = Objects.requireNonNull(getIntent().getExtras()).getString("userGender");
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        assert userGender != null;
        mEnjoyerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userGender).child(userId);
        getUserInfo();
        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }

            private void saveUserInformation() {
                name = mNameField.getText().toString();
                phone = mPhoneField.getText().toString();

                Map<String, Object> userInfo = new HashMap<String, Object>();
                userInfo.put("name", name);
                userInfo.put("phone", phone);
                mEnjoyerDatabase.updateChildren(userInfo);
                if(resultUri != null){
                    Log.d("hello", "saveUserInformation: hey");
                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profilePictures").child(userId);
                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = filepath.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                finish();
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = android.net.Uri.parse(Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString());

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("profilePictureUrl", downloadUrl.toString());
                            mEnjoyerDatabase.updateChildren(userInfo);

                            finish();
                            return;
                        }
                    });
                }else{
                    finish();
                }
            }
        });
    }

    private void getUserInfo() {
        mEnjoyerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    assert map != null;
                    if(map.get("name") != null){
                        name = Objects.requireNonNull(map.get("name")).toString();
                        mNameField.setText(name);
                    }
                    if(map.get("phone") != null){
                        name = Objects.requireNonNull(map.get("phone")).toString();
                        mNameField.setText(phone);
                    }
                    if(map.get("profilePictureUrl") != null){
                        profilePictureUrl = Objects.requireNonNull(map.get("profilePictureUrl")).toString();
                        Glide.with(getApplication()).load(profilePictureUrl).into(mProfilePicture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            assert data != null;
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfilePicture.setImageURI(resultUri);
        }
    }
}
