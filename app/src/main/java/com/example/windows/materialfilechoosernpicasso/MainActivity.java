package com.example.windows.materialfilechoosernpicasso;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.BTN_PICK)
    Button buttonPick;
    @BindView(R.id.BTN_UPLOAD)
    Button buttonUpload;
    @BindView(R.id.TXTVIEW_PATH)
    TextView path;
    @BindView(R.id.img_view)
    ImageView imageView;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            // Do anything with file
            path.setText(filePath);
            File f = new File(filePath);
            Picasso.get().load(f).into(imageView);
        }
    }

    @OnClick(R.id.BTN_PICK)
    public void pickOnClick(View view)
    {
       checkPermissionsAndOpenFilePicker();
    }

    @OnClick(R.id.BTN_UPLOAD)
    public void uploadOnClick(View view)
    {
        Toast.makeText(MainActivity.this, "Upload Clicked", Toast.LENGTH_SHORT).show();
    }
    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {

            new MaterialFilePicker()
                    .withActivity(MainActivity.this)
                    .withRequestCode(1000)
                    .withHiddenFiles(true) // Show hidden files and folders
                    .start();
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new MaterialFilePicker()
                            .withActivity(MainActivity.this)
                            .withRequestCode(1000)
                            .withHiddenFiles(true) // Show hidden files and folders
                            .start();

                } else {
                    showError();
                }
            }
        }
    }
}
