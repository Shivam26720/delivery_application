package com.ab.sabjiwala.Adapters;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.Fragments.orderlist;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.model.fruits_model;
import com.ab.sabjiwala.model.grocery_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class grocessry_adapter extends FirebaseRecyclerAdapter<grocery_model,grocessry_adapter.Viewholder> {
    FirebaseDB db,btn_checked;
    public grocessry_adapter(@NonNull FirebaseRecyclerOptions<grocery_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull grocessry_adapter.Viewholder holder, int position, @NonNull grocery_model model) {
        holder.title.setText(model.getTitle());
        StringBuilder builder=new StringBuilder();
        builder.append("₹");
        builder.append(model.getPrice());
        builder.append(model.getUnit());
        holder.price.setText(builder.toString());
        if (model.getQuantity()!=null) {
            if (model.getQuantity().equals("0")) {
                holder.price.setText("Out of stock");
                holder.add.setVisibility(View.GONE);
            } else {
                holder.price.setText(builder.toString());
                holder.add.setVisibility(View.VISIBLE);
            }
        }
        Picasso.get().load(model.getImg()).placeholder(R.drawable.grockit_logo).into(holder.img);
        holder.addbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.getReference().child("orders").child(model.getTitle()).child("title")
                        .setValue(model.getTitle());
                db.getReference().child("orders").child(model.getTitle()).child("price")
                        .setValue(model.getPrice());
                db.getReference().child("orders").child(model.getTitle()).child("quantity")
                        .setValue(model.getQuantity());
                db.getReference().child("orders").child(model.getTitle()).child("unit")
                        .setValue(model.getUnit());
            }
        });
        btn_checked.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (model.getTitle().equals(snapshot.child(model.getTitle()).child("title").getValue())){
                        holder.check.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.GONE);
                    }else{
                        holder.check.setVisibility(View.GONE);
                        holder.add.setVisibility(View.VISIBLE);
                    }
                    if (model.getQuantity()!=null) {
                        if (model.getQuantity().equals("0")) {
                            holder.price.setText("Out of stock");
                            holder.add.setVisibility(View.GONE);
                        } else {
                            holder.price.setText(builder.toString());
                            holder.add.setVisibility(View.VISIBLE);
                        }
                    }
//                    holder.textView.setText(String.valueOf(snapshot.getChildrenCount()).concat(" Items Added"));
                  //  holder.snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public grocessry_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db=new FirebaseDB("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        btn_checked=new FirebaseDB("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"/orders");
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fruits,parent,false));
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        ImageView img,add,check;
        TextView title,price,textView;
        View view;
        Snackbar snackbar;
        Button button;
        ConstraintLayout addbtn2;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img2);
            title=itemView.findViewById(R.id.title2);
            price=itemView.findViewById(R.id.price2);
            add=itemView.findViewById(R.id.addbtn);
            check=itemView.findViewById(R.id.check);
            addbtn2=itemView.findViewById(R.id.addbtn2);
           /* FragmentActivity activity=(FragmentActivity)itemView.getContext();
            view=LayoutInflater.from(itemView.getContext()).inflate(R.layout.order_dialog,null  );
            snackbar= Snackbar.make(activity.findViewById(R.id.layout1),"", Snackbar.LENGTH_INDEFINITE);
            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbar.setBehavior(null);
            snackbarLayout.setPadding(0, 0, 0, 0);
            snackbarLayout.addView(view);
            button=view.findViewById(R.id.btn);
            textView=view.findViewById(R.id.textView9);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager manager = activity.getSupportFragmentManager();
                    orderlist orderlist=new orderlist();
                    manager.beginTransaction().replace(R.id.home, orderlist).commit();
                    snackbar.dismiss();
                }
            });*/
        }
    }
}
