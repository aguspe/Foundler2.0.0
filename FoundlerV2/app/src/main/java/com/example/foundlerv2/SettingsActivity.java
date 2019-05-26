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

    private EditText mNameField, mPhoneField, mGenderField;
    private Button mBack, mSave;
    private ImageView mProfilePicture;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String userId, name, phone, profilePictureUrl, userGender;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mGenderField = (EditText) findViewById(R.id.gender);
        mProfilePicture = (ImageView) findViewById(R.id.profilePicture);
        mSave = (Button) findViewById(R.id.save);
        mBack = (Button) findViewById(R.id.back);

        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
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
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
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
                userGender = mGenderField.getText().toString();

                Map<String, Object> userInfo = new HashMap<String, Object>();
                userInfo.put("name", name);
                userInfo.put("phone", phone);
                userInfo.put("gender", userGender);
                mUserDatabase.updateChildren(userInfo);
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
                            mUserDatabase.updateChildren(userInfo);

                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    });
                }else{
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        });
    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        phone = Objects.requireNonNull(map.get("phone")).toString();
                        mPhoneField.setText(phone);
                    }
                    if(map.get("gender") != null){
                        userGender = Objects.requireNonNull(map.get("gender")).toString();
                        mGenderField.setText(userGender);
                    }
                    Glide.clear(mProfilePicture);

                    if(map.get("profilePictureUrl") != null){
                        profilePictureUrl = Objects.requireNonNull(map.get("profilePictureUrl")).toString();
                        Log.d("tagi", profilePictureUrl);
                        switch (profilePictureUrl){
                            case "default":
                                mProfilePicture.setImageResource(R.mipmap.ic_launcher);
                                break;

                            default:
                                Glide.with(getApplication()).load(profilePictureUrl).into(mProfilePicture);
                                break;
                        }
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
