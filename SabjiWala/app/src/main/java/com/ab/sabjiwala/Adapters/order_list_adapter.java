package com.ab.sabjiwala.Adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.sabjiwala.Fragments.Location;
import com.ab.sabjiwala.model.orderlist_model;

import com.ab.sabjiwala.R;
import com.ab.sabjiwala.model.vegitable_detail;
import com.google.android.material.circularreveal.CircularRevealHelper;

import java.util.ArrayList;

public class order_list_adapter extends RecyclerView.Adapter<order_list_adapter.Viewholder>{
    ArrayList<orderlist_model> arrayList;
    ArrayList<vegitable_detail> vegitable_details;
    int total=0;
    public order_list_adapter(ArrayList<orderlist_model> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        vegitable_details=new ArrayList<>();
        vegitable_details.ensureCapacity(arrayList.size());
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list,parent,false));
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
          /*  holder.textView.setText(arrayList.get(position).);
            holder.price.setText("₹"+arrayList.get(position).getPrice()+"/Kg");
         //   holder.pricetotal.setText("₹"+arrayList.get(position).getPrice());
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(holder.quantity.getText().toString())!=0){
                        int q=Integer.parseInt(holder.quantity.getText().toString());
                        q=q-1;
                        holder.quantity.setText(String.valueOf(q));
                        int price=Integer.parseInt(arrayList.get(holder.getAbsoluteAdapterPosition()).getPrice());
                        holder.pricetotal.setText("₹"+price*q);
                        total=total-price;
                        holder.totalitems.setText("₹"+String.valueOf(total));
                        holder.grandtotal.setText("₹"+(total+2));
                        vegitable_details.add(holder.getAbsoluteAdapterPosition(),new vegitable_detail(holder.textView.getText().toString(),holder.price.getText().toString(),
                                holder.quantity.getText().toString(),holder.pricetotal.getText().toString()));
                    }
                }
            });
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    int q=Integer.parseInt(holder.quantity.getText().toString());
                    q=q+1;
                    holder.quantity.setText(String.valueOf(q));
                    int price=Integer.parseInt(arrayList.get(holder.getAbsoluteAdapterPosition()).getPrice());
                    holder.pricetotal.setText("₹"+price*q);
                    total=total+price;
                    holder.totalitems.setText("₹"+String.valueOf(total));
                    holder.grandtotal.setText("₹"+(total+2));
                    vegitable_details.add(holder.getAbsoluteAdapterPosition(),new vegitable_detail(holder.textView.getText().toString(),holder.price.getText().toString(),
                            holder.quantity.getText().toString(),holder.pricetotal.getText().toString()));
                }
            });*/
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView textView,price,pricetotal,quantity,totalitems,grandtotal;
        ImageView plus,minus;
        Button payment;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
         /*   price=itemView.findViewById(R.id.price);
            textView=itemView.findViewById(R.id.textView9);
            plus=itemView.findViewById(R.id.plus);
            minus=itemView.findViewById(R.id.minus);
            pricetotal=itemView.findViewById(R.id.pricetotal);
            quantity=itemView.findViewById(R.id.quantity);
            FragmentActivity activity=(FragmentActivity)itemView.getContext();
            totalitems=activity.findViewById(R.id.total_items);
            grandtotal=activity.findViewById(R.id.total);
            payment=activity.findViewById(R.id.payment);
            payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager manager=activity.getSupportFragmentManager();
                    Bundle bundle=new Bundle();
                    bundle.putString("total",totalitems.getText().toString());
                    bundle.putString("grand_totol",grandtotal.getText().toString());
                    Location location=new Location(vegitable_details);
                    location.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.home,location).addToBackStack(null).commit();
                }
            });*/
        }
    }
}
