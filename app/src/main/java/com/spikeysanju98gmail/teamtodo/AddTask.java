package com.spikeysanju98gmail.teamtodo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

public class AddTask extends AppCompatActivity {

    private ImageButton selectImg;
    private EditText ettitle;
    private EditText etdescription;
    private Button uploadBtn;
    private DatabaseReference mTask;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    Uri saveURI;
    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize the inputs
        selectImg = (ImageButton)findViewById(R.id.taskImg);
        ettitle = (EditText)findViewById(R.id.titletv);
        etdescription = (EditText)findViewById(R.id.descriptiontv);
        uploadBtn = (Button)findViewById(R.id.uploadBtn);



        //Initialize the database
        mTask = FirebaseDatabase.getInstance().getReference().child("Tasks");
        mStorage = FirebaseStorage.getInstance().getReference().child("Task_Images");


        //select image method
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });

        //uploading the task
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadTask();
            }
        });






    }

    private void uploadTask() {

        final String title = ettitle.getText().toString();
        final String description = etdescription.getText().toString();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Uploading Task...");
        mProgress.show();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && saveURI != null){

            String randomUID = UUID.randomUUID().toString();

            StorageReference filepath = mStorage.child("Task_Images" +randomUID).child(saveURI.getLastPathSegment());
            filepath.putFile(saveURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadURL = taskSnapshot.getDownloadUrl();

                    DatabaseReference newTask = mTask.push();
                    newTask.child("title").setValue(title);
                    newTask.child("description").setValue(description);
                    newTask.child("image").setValue(downloadURL.toString());
                    mProgress.dismiss();

                    startActivity(new Intent(AddTask.this,MainActivity.class));


                }
            });

        }




    }

    private void selectImage() {
        Intent select = new Intent();
        select.setType("image/*");
        select.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(select,"Select Image"),GALLERY_REQUEST);

    }



    // Image Select and Crop Results are stored in this URI

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() !=null){



            saveURI= data.getData();

            CropImage.activity(saveURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                selectImg.setImageURI(resultUri);
                saveURI = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
