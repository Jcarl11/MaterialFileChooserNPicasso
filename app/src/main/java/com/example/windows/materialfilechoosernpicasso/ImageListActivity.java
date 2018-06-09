package com.example.windows.materialfilechoosernpicasso;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ImageListActivity extends AppCompatActivity
{
    public RecyclerView recyclerView;

    private ImageAdapter imageAdapter;

    private DatabaseReference mReference;
    private List<ImagesEntity> imagesEntityList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        recyclerView = (RecyclerView) findViewById(R.id.LAYOUT_RECYCLERVIEW);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImageListActivity.this));
        imagesEntityList = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference("images");
        mReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    ImagesEntity ie = postSnapshot.getValue(ImagesEntity.class);
                    imagesEntityList.add(ie);
                }

                imageAdapter = new ImageAdapter(ImageListActivity.this, imagesEntityList);
                recyclerView.setAdapter(imageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(ImageListActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
