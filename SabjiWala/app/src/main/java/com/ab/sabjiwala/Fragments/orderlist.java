package com.ab.sabjiwala.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.sabjiwala.Adapters.order_adapter;
import com.ab.sabjiwala.Adapters.order_list_adapter;
import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.FragmentOrderlistBinding;
import com.ab.sabjiwala.getlocation;
import com.ab.sabjiwala.model.orderlist_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class orderlist extends Fragment {
    FragmentOrderlistBinding binding;
    order_adapter adapter;
   // order_list_adapter adapter;
    ArrayList<orderlist_model> list;
    View view;
    Snackbar snackbar;
    ProgressDialog dialog;
    FirebaseDB firebaseDB,total_order,reduce;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public orderlist() {
    }

    public static orderlist newInstance(String param1, String param2) {
        orderlist fragment = new orderlist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDB=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).concat("/order_calculation"));
        total_order=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                reduce.getReference().removeValue();
                try {
                    FragmentManager manager = requireActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.home, new home()).commit();
                }catch (IllegalStateException exception){
                    Log.d("e","e");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderlistBinding.inflate(getLayoutInflater(), container, false);
        requireActivity().getWindow().setStatusBarColor(Color.rgb(255, 255, 255));
        BottomNavigationView navigationView = requireActivity().findViewById(R.id.bnav);
        navigationView.setVisibility(View.GONE);
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Preparing order...");
        dialog.setCancelable(false);
        dialog.create();
        //int size = Integer.parseInt(getArguments().getString("counter"));
      /*
        8binding.orders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.orders.setNestedScrollingEnabled(true);
        list = new ArrayList<>();
        while (size > 0) {
            String name = getArguments().getStringArray(String.valueOf(size))[0];
            String price = getArguments().getStringArray(String.valueOf(size))[1];
            list.add(new orderlist_model(name, price));
            size--;
        }*/

       // adapter = new order_list_adapter(list);
      //  binding.orders.setAdapter(adapter);
        binding.total.setText("₹0");
        binding.totalItems.setText("₹0");
        binding.dileveryCharges.setText("₹2");
        FirebaseRecyclerOptions<orderlist_model> options=new FirebaseRecyclerOptions.Builder<orderlist_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid().toString().concat("/orders")),orderlist_model.class).build();
        adapter=new order_adapter(options);

        binding.orders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.orders.setItemAnimator(null);
        binding.orders.setNestedScrollingEnabled(true);
        binding.orders.setHasFixedSize(true);
        //adapter.notifyAll();
        binding.orders.setAdapter(adapter);
        binding.payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.total.getText().equals("₹0")) {
                    Toast.makeText(getContext(), "order not added", Toast.LENGTH_SHORT).show();
                }
                if (Integer.parseInt(binding.total.getText().toString().substring(1))<69) {
                    Toast.makeText(getContext(), "Total order must be grater than ₹69", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.show();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                    FragmentManager manager=requireActivity().getSupportFragmentManager();
                    Bundle bundle=new Bundle();
                    Location location=new Location();
                    total_order.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            bundle.putString("total",binding.total.getText().toString());
                            bundle.putString("total_order",snapshot.child("total_order").getValue().toString());
                            location.setArguments(bundle);
                            dialog.dismiss();
                            manager.beginTransaction().replace(R.id.home,location).addToBackStack(null)
                                    .commit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                        }
                    },3000);
                 /*   if (check_permission()) {
                        permissio_request.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        permissio_request.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                    } else {
                        getLocation();
                    }*/
                }
            }
        });
        reduce=new FirebaseDB("reduction/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        /*view = LayoutInflater.from(getContext()).inflate(R.layout.current_location_layout, null);
        snackbar = Snackbar.make(requireActivity().findViewById(R.id.layout1), "", BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbar.setBehavior(null);
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(view);*/
        reduce.getReference().removeValue();
        return binding.getRoot();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    public void getTotal(){
        firebaseDB.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.totalItems.setText("₹".concat(snapshot.child("total").getValue().toString()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}