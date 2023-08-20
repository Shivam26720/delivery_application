package com.ab.sabjiwala.Adapters;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.Fragments.orderlist;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.model.orderlist_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.ab.sabjiwala.model.vegitablemodel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VegatibleAdapter extends FirebaseRecyclerAdapter<vegitablemodel,VegatibleAdapter.Viewholder> {
    int counter=0;
    Bundle bundle;
    FirebaseDB firebaseDB,btn_checked;
    public VegatibleAdapter(@NonNull FirebaseRecyclerOptions<vegitablemodel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VegatibleAdapter.Viewholder holder, int position, @NonNull vegitablemodel model) {
        holder.title.setText(model.getTitle());
        StringBuilder builder=new StringBuilder();
        builder.append("₹");
        builder.append(model.getPrice());
        builder.append(model.getUnit());
        holder.price.setText(builder.toString());
        Picasso.get().load(model.getImg()).placeholder(R.drawable.grockit_logo).into(holder.img);
        if (model.getQuantity()!=null) {
            if (model.getQuantity().equals("0")) {
                holder.price.setText("Out of stock");
                holder.add.setVisibility(View.GONE);
            } else {
                holder.price.setText(builder.toString());
                holder.add.setVisibility(View.VISIBLE);
            }
        }
      /*  holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                holder.textView.setText(String.valueOf(counter)+" items added");
                holder.snackbar.show();
                holder.check.setVisibility(View.VISIBLE);
                holder.add.setVisibility(View.GONE);
                bundle.putStringArray(String.valueOf(counter), new String[]{model.getTitle(), model.getPrice()});
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActivity activity=(FragmentActivity) holder.itemView.getContext();
                FragmentManager manager=activity.getSupportFragmentManager();
                orderlist orderlist=new orderlist();
                bundle.putString("counter",String.valueOf(counter));
                orderlist.setArguments(bundle);
                manager.beginTransaction().replace(R.id.home,orderlist).addToBackStack(null).commit();
                holder.snackbar.dismiss();
            }
        });*/
        holder.addbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDB.getReference().child("orders").child(model.getTitle()).child("title")
                        .setValue(model.getTitle());
                firebaseDB.getReference().child("orders").child(model.getTitle()).child("price")
                        .setValue(model.getPrice());
                firebaseDB.getReference().child("orders").child(model.getTitle()).child("quantity")
                        .setValue(model.getQuantity());
                firebaseDB.getReference().child("orders").child(model.getTitle()).child("unit")
                        .setValue(model.getUnit());
            }
        });
        btn_checked.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child(model.getTitle()).exists()){
                        holder.check.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.GONE);
                    }else {
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
                  /*  if (model.getTitle().equals(snapshot.child(model.getTitle()).child("title").getValue())){
                        holder.check.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.GONE);
                    }*/
                    holder.textView.setText(String.valueOf(snapshot.getChildrenCount()).concat(" Items Added"));
                    holder.snackbar.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @NonNull
    @Override
    public VegatibleAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bundle=new Bundle();
        firebaseDB=new FirebaseDB("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        btn_checked=new FirebaseDB("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"/orders");
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vegitables,parent,false));
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView add,check,img;
        View view;
        Snackbar snackbar;
        TextView textView,title,price;
        Button button,payment;
        ProgressDialog dialog;
        ConstraintLayout addbtn2;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            add=itemView.findViewById(R.id.addbtn);
            check=itemView.findViewById(R.id.check);
            img=itemView.findViewById(R.id.img2);
            title=itemView.findViewById(R.id.title2);
            price=itemView.findViewById(R.id.price2);
            addbtn2=itemView.findViewById(R.id.addbtn2);
            FragmentActivity activity=(FragmentActivity)itemView.getContext();
            view=LayoutInflater.from(itemView.getContext()).inflate(R.layout.order_dialog,null  );
           snackbar= Snackbar.make(activity.findViewById(R.id.layout1),"", Snackbar.LENGTH_INDEFINITE);
           snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbar.setBehavior(null);
            snackbarLayout.setPadding(0, 0, 0, 0);
            snackbarLayout.addView(view);
            button=view.findViewById(R.id.btn);
            textView=view.findViewById(R.id.textView9);
            dialog=new ProgressDialog(itemView.getContext());
            dialog.setMessage("Processing...");
            dialog.setCancelable(false);
            dialog.create();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            FragmentManager manager = activity.getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.home, new orderlist()).commit();
                            snackbar.dismiss();
                        }
                    },1500);

                }
            });
        }
    }

}
