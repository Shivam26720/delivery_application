package com.ab.sabjiwala.Fragments;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ab.sabjiwala.Activity.Orders;
import com.ab.sabjiwala.Background_process;
import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.FragmentLocationBinding;
import com.ab.sabjiwala.model.vegitable_detail;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.logging.type.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

import io.grpc.internal.JsonParser;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Location extends Fragment {
    FirebaseDB firebaseDB,orders_calculation,quantity_reduction,quantity_reduction2,quantity_reduction3,user_orders,delevery;
    FragmentLocationBinding binding;
    ActivityResultLauncher<String> permissio_request;
    FusedLocationProviderClient client;
    List<Address> addresses;
    ArrayList<String> orders;
    Boolean flag=false;
    Boolean status=false;
    DataSnapshot snapshot,snapshot2,snapshot3,snapshot4;
    DatabaseReference reference,availablity,order_id;
    ProgressDialog dialog;
    Geocoder geocoder;
    String location_details;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Location() {
        // Required empty public constructor
    }
    public static Location newInstance(String param1, String param2) {
        Location fragment = new Location();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
             /*   orders_calculation.getReference().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user_orders.getReference().removeValue();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
        //    }
      //  });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentLocationBinding.inflate(getLayoutInflater(),container,false);
        setFlag(true);
        user_orders=new FirebaseDB("reduction/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        quantity_reduction=new FirebaseDB(Constants.QUANTITY_REDUCTION);
        quantity_reduction2=new FirebaseDB(Constants.QUANTITY_REDUCTION2);
        quantity_reduction3=new FirebaseDB(Constants.QUANTITY_REDUCTION3);
        orders=new ArrayList<>();
        availablity=FirebaseDatabase.getInstance().getReference("available");
        dialog=new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Checking Availability...");
        dialog.create();
      //  client = LocationServices.getFusedLocationProviderClient(getContext());
        permissio_request = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    getLocation();
                } else {
                    Toast.makeText(requireContext(), "Need location permission to deliver products", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        reference=FirebaseDatabase.getInstance().getReference(Constants.GET_USER.concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()));
        firebaseDB=new FirebaseDB(Constants.GET_USER.concat(FirebaseAuth.getInstance().getCurrentUser().getUid()).concat(Constants.GET_COMPLETED_ORDER));
        orders_calculation=new FirebaseDB(Constants.GET_USER.concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).concat(Constants.GET_ORDER_CALCULATION));
       // getLocation();
        availablity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot avail) {
               setSnapshot4(avail);
                getLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       /* orders_calculation.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()) {
                    if (!snap.getKey().equals("total")){
                        try {
                            firebaseDB.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(snap.getKey())
                                    .child("title").setValue(snapshot.child(snap.getKey()).child("title").getValue().toString());
                            firebaseDB.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(snap.getKey())
                                    .child("quantity").setValue(snapshot.child(snap.getKey()).child("quantity").getValue().toString());
                            firebaseDB.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(snap.getKey())
                                    .child("total").setValue(snapshot.child(snap.getKey()).child("total").getValue().toString());
                          /*  quantity_reduction3.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    //setDataSnapshot4(snapshot);
                                    if (snapshot2.child(snap.getKey()).child("quantity").exists()){
                                        String q=snapshot2.child(snap.getKey()).child("quantity").getValue().toString();
                                        String q2=snapshot.child(snap.getKey()).child("quantity").getValue().toString();
                                        setF(String.valueOf(Integer.parseInt(q)-Integer.parseInt(q2)));
                                        Toast.makeText(getContext(), ""+String.valueOf(getF()), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            quantity_reduction2.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    if (snapshot2.child(snap.getKey()).child("quantity").exists()){
                                        String q=snapshot2.child(snap.getKey()).child("quantity").getValue().toString();
                                        String q2=snapshot.child(snap.getKey()).child("quantity").getValue().toString();
                                        setG(String.valueOf(Integer.parseInt(q)-Integer.parseInt(q2)));
                                        Toast.makeText(getContext(), ""+String.valueOf(getG()), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            quantity_reduction.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) { ;
                                    if (snapshot2.child(snap.getKey()).child("quantity").exists()){
                                        String q=snapshot2.child(snap.getKey()).child("quantity").getValue().toString();
                                        String q2=snapshot.child(snap.getKey()).child("quantity").getValue().toString();
                                        setV(String.valueOf(Integer.parseInt(q)-Integer.parseInt(q2)));
                                        Toast.makeText(getContext(), ""+String.valueOf(getV()), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });*/
                     /*   }catch (NullPointerException exception){
                            Log.d("nullpointer","execption");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        geocoder= new Geocoder(getContext(),Locale.getDefault());
        binding.proceed.setOnClickListener(new View.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.M)
           @Override
           public void onClick(View view) {
               if (TextUtils.isEmpty(binding.name.getText())){
                   showsnack("Name is empty");
               }else if (TextUtils.isEmpty(binding.phone.getText())){
                   showsnack("Phone is empty");
               }else if (!(binding.phone.length() ==10)){
                   showsnack("Invalid Phone number");
               }else if (TextUtils.isEmpty(binding.mark.getText())){
                   showsnack("Landmark is empty");
               }else if (TextUtils.isEmpty(binding.houseno.getText())){
                  showsnack("House no. is empty");
               }else if (!check_permission()){
                  permissio_request.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                  permissio_request.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
               }else if (!check_gps()){
                       Toast.makeText(getContext(), "Kindly enable your location", Toast.LENGTH_SHORT).show();
               }else if (TextUtils.isEmpty(getLocation_details())){
                    getLocation();
                   Toast.makeText(getContext(), "Kindly click on proceed again", Toast.LENGTH_SHORT).show();
               }
               else if (!check_availablity()){
                   Toast.makeText(getContext(), "Not available in your city", Toast.LENGTH_SHORT).show();
                   try {
                       String location = "Locality:- " + addresses.get(0).getLocality() + " postal code:- " + addresses.get(0).getPostalCode()
                               + " admin:- " + addresses.get(0).getAdminArea() + " sub admin:- " + addresses.get(0).getSubAdminArea() + " addressline:- " +
                               addresses.get(0).getAddressLine(0);
                       firebaseDB.getReference()
                               .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                               .child(Constants.GET_LOCATION).setValue(location);
                   }catch (NullPointerException e){
                       Log.d("null","execption");
                       firebaseDB.getReference()
                               .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                               .child(Constants.GET_LOCATION).setValue(getLocation_details());
                   }
               }else{
                       RadioButton button = requireActivity().findViewById(binding.radioGroup.getCheckedRadioButtonId());
                       if (!button.getText().equals(Constants.MODE_ONLINE)) {
                           delevery=new FirebaseDB("orders/".concat(getArguments().getString(Constants.GET_TOTAL_ORDER)).concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child("name").setValue(binding.name.getText().toString());
                           setValue();
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child("phone").setValue(binding.phone.getText().toString());
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child("landmark").setValue(binding.mark.getText().toString());
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child("houseno").setValue(binding.houseno.getText().toString());
                           try {
                               String location = "Locality:- " + addresses.get(0).getLocality() + " postal code:- " + addresses.get(0).getPostalCode()
                                       + " admin:- " + addresses.get(0).getAdminArea() + " sub admin:- " + addresses.get(0).getSubAdminArea() + " addressline:- " +
                                       addresses.get(0).getAddressLine(0);
                               firebaseDB.getReference()
                                       .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                       .child(Constants.GET_LOCATION).setValue(location);
                           }catch (NullPointerException exception){
                               firebaseDB.getReference()
                                       .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                       .child(Constants.GET_LOCATION).setValue(getLocation_details());
                               Log.d("exction","ex");
                           }
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child(Constants.GET_TOTAL).setValue(getArguments().getString(Constants.GET_TOTAL));
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child("delevery_status").setValue(Constants.GET_DELEVERY_STATUS);
                           delevery.getReference().child(Constants.GET_TOTAL)//child(getArguments().getString("total_order")).
                                   .setValue(String.valueOf(Integer.parseInt((getArguments().getString(Constants.GET_TOTAL_ORDER)))));
                           delevery.getReference().child("uid")//child(getArguments().getString("total_order")).
                                   .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                           reference.child(Constants.GET_TOTAL_ORDER).setValue(String.valueOf(Integer.parseInt((getArguments().getString(Constants.GET_TOTAL_ORDER)))+1));
                           firebaseDB.getReference()
                                   .child(getArguments().getString(Constants.GET_TOTAL_ORDER))
                                   .child("method").setValue(button.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void unused) {
                                           binding.animation.setVisibility(View.VISIBLE);
                                           binding.animation.playAnimation();
                                           Toast.makeText(getContext(), "Order successfully placed", Toast.LENGTH_SHORT).show();
                                           Handler handler=new Handler();
                                           handler.postDelayed(new Runnable() {
                                               @Override
                                               public void run() {
                                                   setFlag(false);
                                                   FragmentManager manager = requireActivity().getSupportFragmentManager();
                                                   manager.beginTransaction().replace(R.id.home, new home()).commit();
                                               }
                                           },3000);
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });
                       }else{
                           order_id=FirebaseDatabase.getInstance().getReference("order_id");
                           order_id.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   if (snapshot.exists()){
                                       Intent intent=new Intent(requireContext(), Orders.class);
                                       intent.putExtra("total_order",getArguments().getString("total_order"));
                                       intent.putExtra("order_id",snapshot.child("order_id").getValue().toString());
                                       intent.putExtra("key",snapshot.child("secret").getValue().toString());
                                       intent.putExtra("total_amount",getArguments().getString("total"));
                                       intent.putExtra("phone",binding.phone.getText().toString());
                                       intent.putExtra("method",button.getText().toString());
                                       intent.putExtra("name",binding.name.getText().toString());
                                       intent.putExtra("landmark",binding.mark.getText().toString());
                                       intent.putExtra("houseno",binding.houseno.getText().toString());
                                       intent.putParcelableArrayListExtra(Constants.GET_LOCATION, (ArrayList<? extends Parcelable>) getAddresses());
                                       startActivity(intent);
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {
                                      Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                           });
                         //  requireActivity().startActivity(intent);
                         /*  dialog=new ProgressDialog(getContext());
                           dialog.setMessage("Preparing order...");
                           dialog.setCancelable(false);
                           dialog.create();
                           Background_process process= new Background_process(orders, getDataSnapshot(), firebaseDB,dialog);
                           if (!process.isAlive()){
                               process.start();
                           }*/
                          /* for (setI(0);getI()<orders.size()-1;){
                               if (!orders.get(getI()).equals("total")) {
                                           String title=getDataSnapshot().child(orders.get(getI())).child("title").getValue().toString();
                                           String quantity=getDataSnapshot().child(orders.get(getI())).child("quantity").getValue().toString();
                                           String total=getDataSnapshot().child(orders.get(getI())).child("total").getValue().toString();
                                           String name=orders.get(getI());
                                                   firebaseDB.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                           .child(name)
                                                           .child("title").setValue(title);
                                                   firebaseDB.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                           .child(name)
                                                           .child("quantity").setValue(quantity);
                                                   firebaseDB.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                           .child(name)
                                                           .child("total").setValue(total).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void unused) {
                                                           setI(getI()+1);
                                                       }
                                                   }).addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                           Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                           setI(getI()+1);
                                                       }
                                                   });
                               }
                           }*/
                       }
               }
           }
       });
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setFlag(false);
                try {
                    FragmentManager manager = requireActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.home, new home()).commit();
                } catch (IllegalStateException e) {
                    Log.d("e", "e");
                }
            }
        });
        return binding.getRoot();
    }
    public void showsnack(String msg){
        Snackbar.make(binding.getRoot(),msg, BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public Boolean check_permission(){
        return requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation(){
         if (check_permission()){
             if (check_gps()) {
                 SmartLocation.with(getContext()).location().oneFix().start(new OnLocationUpdatedListener() {
                     @Override
                     public void onLocationUpdated(android.location.Location location) {
                                 try {
                                     try {
                                         getLocationdetails(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
                                         setAddresses(geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1));
                                       //Toast.makeText(getContext(), ""+getAddresses().get(0).getLocality(), Toast.LENGTH_SHORT).show();
                                     } catch (IOException e) {
                                         e.printStackTrace();
                                     }
                                 } catch (NullPointerException e) {
                                     e.printStackTrace();
                                 }
                     }
                 });
             }else{
                 Toast.makeText(getContext(), "Kindly enable your location ", Toast.LENGTH_SHORT).show();
             }
         }else{
             permissio_request.launch(Manifest.permission.ACCESS_FINE_LOCATION);
             permissio_request.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
         }
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    public void reduce(){
        orders_calculation.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getFlag()) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            if (!snap.getKey().equals("total")) {
                                try {
                                    if (getSnapshot().child(snap.getKey()).exists()){
                                        String q = getSnapshot().child(snap.getKey()).child(Constants.GET_QUANTITY).getValue().toString();
                                        String q2 = snapshot.child(snap.getKey()).child(Constants.GET_QUANTITY).getValue().toString();
                                        String s = String.valueOf(Integer.parseInt(q) - Integer.parseInt(q2));
                                        if (Integer.parseInt(s)>0) {
                                            quantity_reduction.getReference().child(snap.getKey()).child(Constants.GET_QUANTITY).setValue(String.valueOf(s));
                                        }
                                    }else if (getSnapshot2().child(snap.getKey()).exists()){
                                        String q = getSnapshot2().child(snap.getKey()).child(Constants.GET_QUANTITY).getValue().toString();
                                        String q2 = snapshot.child(snap.getKey()).child(Constants.GET_QUANTITY).getValue().toString();
                                        String s = String.valueOf(Integer.parseInt(q) - Integer.parseInt(q2));
                                        if (Integer.parseInt(s)>0) {
                                            quantity_reduction3.getReference().child(snap.getKey()).child(Constants.GET_QUANTITY).setValue(String.valueOf(s));
                                        }
                                    }
                                    /*quantity_reduction3.getReference().addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                            if (snapshot2.child(snap.getKey()).child("quantity").exists()) {
                                                String q = snapshot2.child(snap.getKey()).child("quantity").getValue().toString();
                                                String q2 = snapshot.child(snap.getKey()).child("quantity").getValue().toString();
                                                String s = String.valueOf(Integer.parseInt(q) - Integer.parseInt(q2));
                                                quantity_reduction3.getReference().child(snap.getKey()).child("quantity").setValue(String.valueOf(s));
                                               // binding.name.setText(binding.name.getText().toString().concat(s)+" ");
                                                //Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    quantity_reduction2.getReference().addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                            if (snapshot2.child(snap.getKey()).child("quantity").exists()) {
                                                String q = snapshot2.child(snap.getKey()).child("quantity").getValue().toString();
                                                String q2 = snapshot.child(snap.getKey()).child("quantity").getValue().toString();
                                                String s = String.valueOf(Integer.parseInt(q) - Integer.parseInt(q2));
                                                quantity_reduction2.getReference().child(snap.getKey()).child("quantity").setValue(String.valueOf(s));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    final String[] q3 = new String[1];
                                    final String[] q4 = new String[1];
                                    String s = String.valueOf(Integer.parseInt(q3[0]) - Integer.parseInt(q4[0]));
                                    quantity_reduction.getReference().addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                            if (snapshot2.child(snap.getKey()).child("quantity").exists()) {
                                                 q3[0] = snapshot2.child(snap.getKey()).child("quantity").getValue().toString();
                                                 q4[0] = snapshot.child(snap.getKey()).child("quantity").getValue().toString();
                                                quantity_reduction.getReference().child(snap.getKey()).child("quantity").setValue(String.valueOf(s)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });*/
                                } catch (NullPointerException exception) {
                                    Log.d("nullpointer", "execption");
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setValue(){
        user_orders.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reduced_data) {
                if (getFlag()) {
                    for (DataSnapshot snap : reduced_data.getChildren()) {
                         if (getSnapshot().child(snap.getKey()).exists()){
                           String r=String.valueOf( Integer.parseInt(getSnapshot().child(snap.getKey()).child(Constants.GET_QUANTITY).getValue().toString())-Integer.parseInt(reduced_data.child(snap.getKey()).getValue().toString()));
                           if (Integer.parseInt(r)>=0) {
                               // quantity_reduction.getReference().child(snap.getKey()).child("quantity").setValue(reduced_data.child(snap.getKey()).getValue().toString());
                               quantity_reduction.getReference().child(snap.getKey()).child(Constants.GET_QUANTITY).setValue(r);
                           }
                         } else if (getSnapshot2().child(snap.getKey()).exists()) {
                             String r=String.valueOf( Integer.parseInt(getSnapshot2().child(snap.getKey()).child(Constants.GET_QUANTITY).getValue().toString())-Integer.parseInt(reduced_data.child(snap.getKey()).getValue().toString()));
                             if (Integer.parseInt(r)>=0) {
                                 quantity_reduction3.getReference().child(snap.getKey()).child(Constants.GET_QUANTITY).setValue(r);
                             }
                           //  quantity_reduction3.getReference().child(snap.getKey()).child("quantity").setValue(reduced_data.child(snap.getKey()).getValue().toString());
                         }else if (getSnapshot3().child(snap.getKey()).exists()){
                             String r=String.valueOf( Integer.parseInt(getSnapshot3().child(snap.getKey()).child(Constants.GET_QUANTITY).getValue().toString())-Integer.parseInt(reduced_data.child(snap.getKey()).getValue().toString()));
                             if (Integer.parseInt(r)>=0) {
                                 quantity_reduction2.getReference().child(snap.getKey()).child(Constants.GET_QUANTITY).setValue(r);
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
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public Boolean check_gps(){
        return SmartLocation.with(getContext()).location().state().isAnyProviderAvailable();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public Boolean check_availablity(){
        return getSnapshot4().child(getLocation_details()).exists();
      /*  if (getAddresses().get(0).getLocality()==null&&getAddresses().get(0).getSubAdminArea()==null){
            getLocation();
        }*/
      /*  if (getAddresses().get(0).getLocality()!=null)
            return getSnapshot4().child(getAddresses().get(0).getLocality()).exists();
        else if (getAddresses().get(0).getSubAdminArea()!=null){
            return getSnapshot4().child(getAddresses().get(0).getSubAdminArea()).exists();
        }else
            return false;*/
      //  return  (getSnapshot4().child(getAddresses().get(0).getLocality()).exists()||getSnapshot4().child(getAddresses().get(0).getSubAdminArea()).exists());
    }

    public DataSnapshot getSnapshot4() {
        return snapshot4;
    }

    public void setSnapshot4(DataSnapshot snapshot4) {
        this.snapshot4 = snapshot4;
    }
    public void getLocationdetails(String Lattitue,String longitude){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url=getSnapshot4().child("url").getValue().toString()+"lat="+Lattitue+"&lon="+longitude;
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("X-RapidAPI-Key", getSnapshot4().child("key").getValue().toString())
                        .addHeader("X-RapidAPI-Host", getSnapshot4().child("host").getValue().toString())
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        showmag(e.getMessage());
                        Log.d("data",e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            try {
                                JSONArray array=new JSONArray(response.body().string());
                                setLocation_details(array.getJSONObject(0).getString("name"));
                                 } catch (JSONException |NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }
        }).start();
    }
    private void showmag(String msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
               Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getLocation_details() {
        return location_details;
    }

    public void setLocation_details(String location_details) {
        this.location_details = location_details;
    }
}