package com.ab.sabjiwala.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.model.fruits_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class fruits_adapter extends FirebaseRecyclerAdapter<fruits_model,fruits_adapter.Viewholder> {
    FirebaseDB db,btn_checked;
    public fruits_adapter(@NonNull FirebaseRecyclerOptions<fruits_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull fruits_adapter.Viewholder holder, int position, @NonNull fruits_model model) {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @NonNull
    @Override
    public fruits_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db=new FirebaseDB("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        btn_checked=new FirebaseDB("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"/orders");
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fruits,parent,false));
    }
    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView img,add,check;
        TextView title,price;
        ConstraintLayout addbtn2;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.img2);
            title=itemView.findViewById(R.id.title2);
            price=itemView.findViewById(R.id.price2);
            add=itemView.findViewById(R.id.addbtn);
            addbtn2=itemView.findViewById(R.id.addbtn2);
            check=itemView.findViewById(R.id.check);
        }
    }
}
