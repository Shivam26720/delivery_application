package com.ab.sabjiwala.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.sabjiwala.R;
import com.ab.sabjiwala.model.image_slider_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class image_slider_adapter extends SliderViewAdapter<SliderViewAdapter.ViewHolder>{
    List<image_slider_model> list;

    public image_slider_adapter(List<image_slider_model> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slide,null));
    }

    @Override
    public void onBindViewHolder(SliderViewAdapter.ViewHolder viewHolder, int position) {
        ImageView imageView=viewHolder.itemView.findViewById(R.id.image);
        Picasso.get().load(list.get(position).getUrl()).into(imageView);
    }
    @Override
    public int getCount() {
        return list.size();
    }
    class ViewHolder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
        }
    }
}
