package com.example.windows.materialfilechoosernpicasso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

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
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        ButterKnife.bind(this);
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener()
                {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "Permission needed to use application", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK)
        {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            path.setText(filePath);
            File f = new File(filePath);
            Picasso.get().load(f).into(imageView);
        }
    }

    @OnClick(R.id.BTN_PICK)
    public void pickOnClick(View view)
    {
        new MaterialFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(1000)
                .withHiddenFiles(true)
                .start();
    }

    @OnClick(R.id.BTN_UPLOAD)
    public void uploadOnClick(View view)
    {
        uploadFile(path.getText().toString().trim());
    }

    private void uploadFile(String imgPath)
    {
        Uri imgPathUri = Uri.fromFile(new File(imgPath));
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
        StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());
        ref.putFile(imgPathUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
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
