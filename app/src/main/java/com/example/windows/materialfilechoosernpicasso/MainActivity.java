package com.example.windows.materialfilechoosernpicasso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
{

    @BindView(R.id.BTN_PICK)
    Button buttonPick;
    @BindView(R.id.BTN_UPLOAD)
    Button buttonUpload;
    @BindView(R.id.TXTVIEW_PATH)
    TextView path;
    @BindView(R.id.img_view)
    ImageView imageView;
    @BindView(R.id.BTN_SHOWLIST)
    Button buttonShowList;
    @BindView(R.id.ET_TITLE)
    EditText img_title;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("images");
        ButterKnife.bind(this);
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener()
                {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data))
        {
            String image = ImagePicker.getFirstImageOrNull(data).getPath();
            path.setText(image);
            showImage(image);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showImage(path.getText().toString().trim());
    }

    private void showImage(String imgPath)
    {
        if(imgPath != null)
        {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.CYAN)
                    .borderWidthDp(1)
                    .oval(true)
                    .build();
            File f = new File(imgPath);
            Picasso.get().load(f).transform(transformation).into(imageView);
        }
    }

    @OnClick(R.id.BTN_PICK)
    public void pickOnClick(View view)
    {
        ImagePicker.create(MainActivity.this)
                .returnMode(ReturnMode.ALL)
                .toolbarImageTitle("Select an image")
                .toolbarArrowColor(Color.BLACK)
                .single()
                .enableLog(false)
                .start();
    }

    @OnClick(R.id.BTN_UPLOAD)
    public void uploadOnClick(View view)
    {
        uploadFile(path.getText().toString().trim());
    }

    @OnClick(R.id.BTN_SHOWLIST)
    public void showListOnClick(View view)
    {
        Intent showList = new Intent(MainActivity.this, ImageListActivity.class);
        startActivity(showList);
    }
    private void uploadFile(String imgPath)
    {
        Uri imgPathUri = Uri.fromFile(new File(imgPath));
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
        StorageReference ref = mStorageRef.child(UUID.randomUUID().toString());
        ref.putFile(imgPathUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        ImagesEntity ie = new ImagesEntity(img_title.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());
                        String imgID = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(imgID).setValue(ie);
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    }
                });
    }

}
