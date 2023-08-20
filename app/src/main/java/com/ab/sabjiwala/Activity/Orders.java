package com.ab.sabjiwala.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.Fragments.Constants;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.ActivityOrdersBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class Orders extends AppCompatActivity implements PaymentResultWithDataListener {
    FirebaseDB firebaseDB,orders_calculation,quantity_reduction,quantity_reduction2,quantity_reduction3,user_orders,delevery;
    DataSnapshot snapshot,snapshot2,snapshot3,snapshot4;
    DatabaseReference reference,availablity;
    ActivityOrdersBinding binding;
    Checkout checkout;
    ProgressDialog dialog;
    RazorpayClient razorpay;
    getOrderId orderId;
    List<Address> addresses;
    Boolean flag=false;
    AlertDialog.Builder alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOrdersBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(Color.rgb(255,255,255));
        setContentView(binding.getRoot());
        Checkout.preload(getApplicationContext());
        checkout=new Checkout();
        dialog=new ProgressDialog(Orders.this);
        dialog.setCancelable(false);
        dialog.setMessage(Constant.order_progress_dialog_msg);
        dialog.create();
        alertDialog=new AlertDialog.Builder(Orders.this);
        alertDialog.setMessage("Do you want to retry ?");
        alertDialog.setCancelable(false);
        checkout.setImage(R.mipmap.icon);
        orderId=new getOrderId(Orders.this,getIntent().getStringExtra(Constant.ORDERID),getIntent().getStringExtra("key"));
        orderId.start();
        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              //  orderId=new getOrderId(Orders.this,getIntent().getStringExtra("order_id"),getIntent().getStringExtra("key"));
                //dialog.show();
                try {
                        orderId=new getOrderId(Orders.this,getIntent().getStringExtra("order_id"),getIntent().getStringExtra("key"));
                        orderId.start();
                }catch (IllegalThreadStateException e){
                    Log.d("execption","e");
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                transition();

            }
        });
        addresses=getIntent().getParcelableArrayListExtra("location");
     /*   binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderId=new getOrderId(Orders.this,getIntent().getStringExtra("order_id"),getIntent().getStringExtra("key"));
                dialog.show();
                try {
                    if (orderId.isAlive()) {
                        orderId.interrupt();
                        orderId=new getOrderId(Orders.this,getIntent().getStringExtra("order_id"),getIntent().getStringExtra("key"));
                        orderId.start();
                    }else{
                        orderId.start();
                    }
                }catch (IllegalThreadStateException e){
                    Log.d("execption","e");
                }
            }
        });*/
        //checkout.setKeyID(getIntent().getStringExtra("order_id"));
        checkout.setKeyID(getIntent().getStringExtra("order_id"));
        user_orders=new FirebaseDB("reduction/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        quantity_reduction=new FirebaseDB(Constant.QUANTITY_REDUCTION);
        quantity_reduction2=new FirebaseDB(Constant.QUANTITY_REDUCTION2);
        quantity_reduction3=new FirebaseDB(Constant.QUANTITY_REDUCTION3);
      //  delevery=new FirebaseDB("orders/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        quantity_reduction3.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                setSnapshot2(snapshot2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        quantity_reduction2.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                setSnapshot3(snapshot2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        quantity_reduction.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                setSnapshot(snapshot2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Orders.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        availablity=FirebaseDatabase.getInstance().getReference("available");
        reference= FirebaseDatabase.getInstance().getReference("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()));
        firebaseDB=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()).concat("/ordercompleted"));
        orders_calculation=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).concat("/order_calculation"));
       // getLocation();
        availablity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot avail) {
                setSnapshot4(avail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
         dialog.dismiss();
        setFlag(true);
        delevery=new FirebaseDB("orders/".concat(getIntent().getStringExtra("total_order")).concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child(Constant.UID).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("payment_data").setValue(paymentData.getData().toString());
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("name").setValue(getIntent().getStringExtra("name"));
        setValue();
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("phone").setValue(getIntent().getStringExtra("phone"));
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("landmark").setValue(getIntent().getStringExtra("landmark"));
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("houseno").setValue(getIntent().getStringExtra("houseno"));
        try {
            String location = "Locality:- " + addresses.get(0).getLocality() + " postal code:- " + addresses.get(0).getPostalCode()
                    + " admin:- " + addresses.get(0).getAdminArea() + " sub admin:- " + addresses.get(0).getSubAdminArea() + " addressline:- " +
                    addresses.get(0).getAddressLine(0);
            firebaseDB.getReference()
                    .child(getIntent().getStringExtra("total_order"))
                    .child("location").setValue(location);
        }catch (NullPointerException exception){
            Log.d("exction","ex");
        }
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("total").setValue(getIntent().getStringExtra("total_amount"));
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("delevery_status").setValue("not Delevered");
        delevery.getReference().child("total")
                .setValue(String.valueOf(Integer.parseInt((getIntent().getStringExtra("total_order")))));
        delevery.getReference().child("uid")
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("total_order").setValue(String.valueOf(Integer.parseInt((getIntent().getStringExtra("total_order")))+1));
        firebaseDB.getReference()
                .child(getIntent().getStringExtra("total_order"))
                .child("method").setValue(getIntent().getStringExtra("method")).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.animation.setVisibility(View.VISIBLE);
                binding.animation.playAnimation();
                Toast.makeText(Orders.this, "Order successfully placed", Toast.LENGTH_SHORT).show();
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       transition();
                    }
                },3000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Orders.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        dialog.dismiss();
        alertDialog.show();
        setFlag(false);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            transition();
        } catch (IllegalStateException e) {
            Log.d("e", "e");
        }
    }
    class getOrderId extends Thread{
        Context context;
        String id,key;
        getOrderId(Context context,String id,String key){
            this.context=context;
            this.id=id;
            this.key=key;
        }
        @Override
        public void run() {
            try {
                razorpay = new RazorpayClient(id, key);
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", Integer.parseInt(getIntent().getStringExtra("total_amount").concat("00").substring(1)));
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_11");
                Order order = razorpay.orders.create(orderRequest);
                if (order.has("id")) {
                    Intent i = new Intent(context, Orders.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("data", order.get("id").toString());
                    i.putExtra("amount", order.get("amount").toString());
                    context.startActivity(i);
                }
            } catch (JSONException | RazorpayException e) {
                Log.d("data", e.getMessage());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            JSONObject options = new JSONObject();
            options.put("name","Grockit");
            options.put("description", "Online payment for ordering vegetable and fruits");
           // options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("order_id",intent.getStringExtra("data"));//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount",intent.getStringExtra("amount"));//pass amount in currency subunits
            options.put("prefill.email", "grockit.delivery22@gmail.com");
            options.put("refill.contact",getIntent().getStringExtra("phone"));
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(Orders.this, options);

        } catch(Exception e) {
            Log.e("tag", "Error in starting Razorpay Checkout", e);
        }
    }

    public DataSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(DataSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public DataSnapshot getSnapshot2() {
        return snapshot2;
    }

    public void setSnapshot2(DataSnapshot snapshot2) {
        this.snapshot2 = snapshot2;
    }

    public DataSnapshot getSnapshot3() {
        return snapshot3;
    }

    public void setSnapshot3(DataSnapshot snapshot3) {
        this.snapshot3 = snapshot3;
    }

    public DataSnapshot getSnapshot4() {
        return snapshot4;
    }

    public void setSnapshot4(DataSnapshot snapshot4) {
        this.snapshot4 = snapshot4;
    }
    public void setValue(){
        user_orders.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reduced_data) {
                if (getFlag()) {
                    for (DataSnapshot snap : reduced_data.getChildren()) {
                        if (getSnapshot().child(snap.getKey()).exists()){
                            String r=String.valueOf( Integer.parseInt(getSnapshot().child(snap.getKey()).child(Constant.GET_QUANTITY).getValue().toString())-Integer.parseInt(reduced_data.child(snap.getKey()).getValue().toString()));
                            if (Integer.parseInt(r)>=0) {
                                // quantity_reduction.getReference().child(snap.getKey()).child("quantity").setValue(reduced_data.child(snap.getKey()).getValue().toString());
                                quantity_reduction.getReference().child(snap.getKey()).child(Constant.GET_QUANTITY).setValue(r);
                            }
                        } else if (getSnapshot2().child(snap.getKey()).exists()) {
                            String r=String.valueOf( Integer.parseInt(getSnapshot2().child(snap.getKey()).child(Constant.GET_QUANTITY).getValue().toString())-Integer.parseInt(reduced_data.child(snap.getKey()).getValue().toString()));
                            if (Integer.parseInt(r)>=0) {
                                quantity_reduction3.getReference().child(snap.getKey()).child(Constant.GET_QUANTITY).setValue(r);
                            }
                            //  quantity_reduction3.getReference().child(snap.getKey()).child("quantity").setValue(reduced_data.child(snap.getKey()).getValue().toString());
                        }else if (getSnapshot3().child(snap.getKey()).exists()){
                            String r=String.valueOf( Integer.parseInt(getSnapshot3().child(snap.getKey()).child(Constant.GET_QUANTITY).getValue().toString())-Integer.parseInt(reduced_data.child(snap.getKey()).getValue().toString()));
                            if (Integer.parseInt(r)>=0) {
                                quantity_reduction2.getReference().child(snap.getKey()).child(Constant.GET_QUANTITY).setValue(r);
                            }
                            // quantity_reduction2.getReference().child(snap.getKey()).child("quantity").setValue(reduced_data.child(snap.getKey()).getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }
    private  void transition(){
        Intent intent=new Intent(Orders.this,Home.class);
        startActivity(intent);
        finish();
    }
}