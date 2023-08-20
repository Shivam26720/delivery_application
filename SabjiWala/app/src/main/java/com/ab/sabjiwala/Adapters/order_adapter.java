package com.ab.sabjiwala.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.Fragments.orderlist;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.model.orderlist_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class order_adapter extends FirebaseRecyclerAdapter<orderlist_model,order_adapter.Viewholder> {
    FirebaseDB firebaseDB,user_total_order,orders,reduce;
    int grandtotal=0;
    public order_adapter(@NonNull FirebaseRecyclerOptions<orderlist_model> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull order_adapter.Viewholder holder, int position, @NonNull orderlist_model model) {
        firebaseDB=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).concat("/order_calculation"));
        user_total_order=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()));
        orders=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().concat("/ordercompleted")));
        reduce=new FirebaseDB("reduction/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        holder.title.setText(model.getTitle());
     //   holder.price.setText(model.getPrice());
      //  holder.total.setText(holder.total.getText().toString());
        firebaseDB.getReference().child(model.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("total").exists()&&snapshot.child("quantity").exists()) {
                    holder.total.setText("₹" + snapshot.child("total").getValue().toString());
                    holder.quantity.setText(snapshot.child("quantity").getValue().toString());

                }else{
                    holder.total.setText("₹0");
                    holder.quantity.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.price.setText( "₹" +model.getPrice() +"/"+model.getUnit().substring(4));
       // holder.total.setText("₹" + holder.total.getText().toString());
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int q=Integer.parseInt(holder.quantity.getText().toString())+1;

                if (q<=Integer.parseInt(model.getQuantity())) {
                    holder.quantity.setText(String.valueOf(q));

                    int total = Integer.parseInt(model.getPrice()) * q;

                    String totalprice = "₹" + total;


                    holder.total.setText(totalprice);

                    grandtotal = grandtotal + Integer.parseInt(model.getPrice());

                    firebaseDB.getReference().child(model.getTitle()).child("title").setValue(model.getTitle());
                    firebaseDB.getReference().child(model.getTitle()).child("quantity").setValue(String.valueOf(q));
                    firebaseDB.getReference().child(model.getTitle()).child("total").setValue(String.valueOf(total));
                    firebaseDB.getReference().child("total").setValue(String.valueOf(grandtotal));

                    holder.total_items.setText("₹".concat(String.valueOf(grandtotal)));
                    holder.total_price.setText("₹".concat(String.valueOf(grandtotal + 2)));
                 //   reduce.getReference().child(model.getTitle()).setValue(String.valueOf(Integer.parseInt(model.getQuantity())-Integer.parseInt(holder.quantity.getText().toString())));
                    reduce.getReference().child(model.getTitle()).setValue(holder.quantity.getText().toString());
                    user_total_order.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("total_order").exists()){
                                 orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                         .child(model.getTitle()).child("title").setValue(model.getTitle());
                                orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                        .child(model.getTitle()).child("quantity").setValue(model.getQuantity());
                                orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                        .child(model.getTitle()).child("total").setValue(String.valueOf(total));
                                orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                        .child(model.getTitle()).child(model.getUnit()).setValue(holder.quantity.getText().toString());

                            }else{
                                user_total_order.getReference().child("total_order").setValue("0");
                                orders.getReference().child("0")
                                        .child(model.getTitle()).child("title").setValue(model.getTitle());
                                orders.getReference().child("0")
                                        .child(model.getTitle()).child("quantity").setValue(model.getQuantity());
                                orders.getReference().child("0")
                                        .child(model.getTitle()).child("total").setValue(String.valueOf(total));
                                orders.getReference().child("0")
                                   .child(model.getTitle()).child(model.getUnit()).setValue(holder.quantity.getText().toString());

                             //   orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                     //   .child(model.getTitle()).child("kilo").setValue(holder.quantity.getText().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(holder.itemView.getContext(), "Sorry! we have no more stocks", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(holder.quantity.getText().toString())>0) {
                    int q = Integer.parseInt(holder.quantity.getText().toString()) - 1;

                    holder.quantity.setText(String.valueOf(q));

                    int total = Integer.parseInt(model.getPrice()) * q;

                    String totalprice = "₹" + total;

                    holder.total.setText(totalprice);

                    grandtotal=grandtotal-Integer.parseInt(model.getPrice());

                    firebaseDB.getReference().child(model.getTitle()).child("title").setValue(model.getTitle());
                    firebaseDB.getReference().child(model.getTitle()).child("quantity").setValue(String.valueOf(q));
                    firebaseDB.getReference().child(model.getTitle()).child("total").setValue(String.valueOf(total));
                  //  firebaseDB.getReference().child(model.getTitle()).child("quantity").setValue();
                   // reduce.getReference().child(model.getTitle()).setValue(String.valueOf(Integer.parseInt(model.getQuantity())-Integer.parseInt(holder.quantity.getText().toString())));
                    reduce.getReference().child(model.getTitle()).setValue(holder.quantity.getText().toString());
                    firebaseDB.getReference().child("total").setValue(String.valueOf(grandtotal));

                    holder.total_items.setText("₹".concat(String.valueOf(grandtotal)));
                    holder.total_price.setText("₹".concat(String.valueOf(grandtotal+2)));
                    user_total_order.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("total_order").exists()){
                                orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                        .child(model.getTitle()).child("title").setValue(model.getTitle());
                                orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                        .child(model.getTitle()).child("quantity").setValue(model.getQuantity());
                                orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                        .child(model.getTitle()).child("total").setValue(String.valueOf(total));
                                orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                        .child(model.getTitle()).child(model.getUnit()).setValue(holder.quantity.getText().toString());

                            }else{
                                user_total_order.getReference().child("total_order").setValue("0");
                                orders.getReference().child("0")
                                        .child(model.getTitle()).child("title").setValue(model.getTitle());
                                orders.getReference().child("0")
                                        .child(model.getTitle()).child("quantity").setValue(model.getQuantity());
                                orders.getReference().child("0")
                                        .child(model.getTitle()).child("total").setValue(String.valueOf(total));
                                orders.getReference().child("0")
                                  .child(model.getTitle()).child(model.getUnit()).setValue(holder.quantity.getText().toString());

                              //  orders.getReference().child(snapshot.child("total_order").getValue().toString())
                                     //   .child(model.getTitle()).child("kilo").setValue(holder.quantity.getText().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    @NonNull
    @Override
    public order_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list,parent,false));
    }
    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title,price,total,quantity,total_items,total_price;
        ImageView plus,minus;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.textView9);
            price=itemView.findViewById(R.id.price);
            total=itemView.findViewById(R.id.pricetotal);
            quantity=itemView.findViewById(R.id.quantity);
            plus=itemView.findViewById(R.id.plus);
            minus=itemView.findViewById(R.id.minus);
            FragmentActivity activity=(FragmentActivity) itemView.getContext();
            total_items=activity.findViewById(R.id.total_items);
            total_price=activity.findViewById(R.id.total);
        }
    }
}
