package com.ab.sabjiwala.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.sabjiwala.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ab.sabjiwala.model.user_total_order_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class user_total_order_adapter extends FirebaseRecyclerAdapter<user_total_order_model,user_total_order_adapter.Viewholder> {
    public user_total_order_adapter(@NonNull FirebaseRecyclerOptions<user_total_order_model> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull user_total_order_adapter.Viewholder holder, int position, @NonNull user_total_order_model model) {
    try {
        holder.name.setText("Name: ".concat(model.getName()));
        holder.method.setText("Payment Method: ".concat(model.getMethod().replaceFirst("\\(cod\\)", "")));
        holder.status.setText("Delevery status: ".concat(model.getDelevery_status()));
        holder.price.setText("Total price: ".concat(model.getTotal()));
    }catch (NullPointerException exception){
        Log.d("execptoin",exception.getMessage());
    }
    }

    @NonNull
    @Override
    public user_total_order_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_orders,parent,false));
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView name,method,status,price;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textView2);
            method=itemView.findViewById(R.id.method);
            status=itemView.findViewById(R.id.status);
            price=itemView.findViewById(R.id.price);
        }
    }
}
