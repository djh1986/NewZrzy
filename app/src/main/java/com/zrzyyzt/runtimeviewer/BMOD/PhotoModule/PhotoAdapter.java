package com.zrzyyzt.runtimeviewer.BMOD.PhotoModule;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zrzyyzt.runtimeviewer.Entity.PhotoEntity;
import com.zrzyyzt.runtimeviewer.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{

    private static final String TAG = "FruitAdapter";

    private Context mContext;

    private List<PhotoEntity> mPhotoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView fruitImage;
        TextView fruitName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            fruitName = (TextView) view.findViewById(R.id.fruit_name);
        }
    }

    public PhotoAdapter(List<PhotoEntity> fruitList) {
        mPhotoList = fruitList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_photo_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                PhotoEntity fruit = mPhotoList.get(position);
                Intent intent = new Intent(mContext, PhotoActivity.class);
                intent.putExtra(PhotoActivity.IMAGE_NAME, fruit.getPhotoName());
                intent.putExtra(PhotoActivity.IMAGE_PATH, fruit.getImagePath());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoEntity photo = mPhotoList.get(position);
        holder.fruitName.setText(photo.getPhotoName());
        Glide.with(mContext).load(photo.getImagePath()).into(holder.fruitImage);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

}
