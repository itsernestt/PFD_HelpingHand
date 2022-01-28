package com.example.pfdhelpinghand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ElderlySettingsActivity extends AppCompatActivity {

    private static final int PICK_IMG_REQUEST = 1;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    Elderly elderly;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    boolean hasimg;
    Uri mImageUri;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_settings);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Button eldSettingsBackBtn = findViewById(R.id.eldSettingsBack);
        eldSettingsBackBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent goBack = new Intent(ElderlySettingsActivity.this, MainActivity.class);
                startActivity(goBack);
            }
        });

        Button upload = findViewById(R.id.uploadProfileImg);
        upload.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                uploadImage(elderly);
            }
        });


        TextView viewCaretakers = findViewById(R.id.caregiversTV);
        viewCaretakers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent navigateNext = new Intent(ElderlySettingsActivity.this, ViewCaregivers.class);
                startActivity(navigateNext);
            }
        });


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        String userID = user.getUid();
        DocumentReference docRef = fStore.collection("Elderly").document(userID);
        Log.d("TAG", String.valueOf(docRef));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                elderly = documentSnapshot.toObject(Elderly.class);

                Log.d("UserDetails", elderly.toString());

                String imageURL = elderly.getImg();

                if(imageURL != null) {

                    ImageView imageView = findViewById(R.id.imageProfilePic);

                    Picasso.with(getApplicationContext()).load(imageURL).into(imageView);
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        hasimg = true;
        startActivityForResult(intent, PICK_IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();

            ImageView imageView = findViewById(R.id.imageProfilePic);
            Picasso.with(this).load(mImageUri).into(imageView);    // Load image into ImageView after image has been added
        }
    }

    private String getFileExtension(Uri uri) { // get file extension type  i.e. jpg, png, of the uri provided
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage(Elderly elderly) {
        saveButton.setText("uploading...");

        if(mImageUri == null){
            Toast.makeText(getApplicationContext(),"Please upload an image!", Toast.LENGTH_LONG).show();
        }else {

            StorageReference imgRef = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri)); // Create a "random" image file name

            imgRef.putFile(mImageUri)   // Put image into firebase storage
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Upload", "Img uploaded");
                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // Get Download URL to set in recipe for reference
                                @Override
                                public void onSuccess(Uri uri) {
                                    elderly.setImg(uri.toString());

                                    mAuth = FirebaseAuth.getInstance(); // Get User and set into recipe details
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userID = user.getUid();
                                    fStore.collection("Elderly").document(userID)
                                            .set(elderly)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("TAG", "onSuccess: Elderly user profile updated " + userID);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("TAG", "onFailure: " + e.toString());
                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(), ElderlySettingsActivity.class)); // Return to home page
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Upload", "Img failed to upload");
                        }
                    });

        }

    }
}