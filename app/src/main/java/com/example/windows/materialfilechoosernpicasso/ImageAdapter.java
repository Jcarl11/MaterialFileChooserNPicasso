package com.example.windows.materialfilechoosernpicasso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Windows on 6/9/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>
{
    private Context context;
    private List<ImagesEntity> imagesEntities;
    private static final String TAG = "ImageAdapter";

    public ImageAdapter(Context context, List<ImagesEntity> imagesEntities) {
        this.context = context;
        this.imagesEntities = imagesEntities;
        Log.e(TAG, "ImageAdapter: Hello");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        ImagesEntity imEntity = imagesEntities.get(position);
        holder.title.setText(imEntity.getTitle());
        Picasso.get().load(imEntity.getImage_url()).fit().centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount()
    {
        return imagesEntities.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.txtview_name);
            image = itemView.findViewById(R.id.image_holder);
        }
    }
}
