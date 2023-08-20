package com.ab.sabjiwala.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.Half;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.sabjiwala.Adapters.fruits_adapter;
import com.ab.sabjiwala.Adapters.grocessry_adapter;
import com.ab.sabjiwala.Adapters.image_slider_adapter;
import com.ab.sabjiwala.FirebaseDB;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.FragmentHomeBinding;
import com.ab.sabjiwala.model.fruits_model;
import com.ab.sabjiwala.model.grocery_model;
import com.ab.sabjiwala.model.image_slider_model;
import com.ab.sabjiwala.model.vegitablemodel;
import  com.ab.sabjiwala.Adapters.VegatibleAdapter;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class home extends Fragment {

    FragmentHomeBinding binding;
    VegatibleAdapter adapter;
    fruits_adapter fruits_adapter;
    grocessry_adapter grocessry_adapter;
    FirebaseDB order_calculation,getImage;
    Button button;
    Snackbar snackbar;
    View view;
    Snackbar.SnackbarLayout snackbarLayout;
    TextView textView;
    image_slider_adapter image_slider_adapter;
    ArrayList<image_slider_model> list;
    AlertDialog.Builder alertdialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home() {
        // Required empty public constructor
    }
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentHomeBinding.inflate(getLayoutInflater(),container,false);
        alertdialog=new AlertDialog.Builder(getContext(),R.style.AlertDialogStyle);
                alertdialog.setTitle("Order tomorrow")
                .setMessage("Sorry! delivery partner is not available")
                .setCancelable(false)
                .setIcon(R.mipmap.icon_round).create();

        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#dfdfdf"));
        list=new ArrayList<>();
        order_calculation=new FirebaseDB("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).concat("/order_calculation"));
        getImage=new FirebaseDB("image");
       // Toast.makeText(getContext(), ""+time[0], Toast.LENGTH_SHORT).show();
        alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requireActivity().finish();
            }
        });
        alertdialog.create();
        getImage.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                        list.add(new image_slider_model(snapshot.child("img").getValue().toString()));
                        list.add(new image_slider_model(snapshot.child("img2").getValue().toString()));
                        list.add(new image_slider_model(snapshot.child("img3").getValue().toString()));
                    binding.imageslider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                    image_slider_adapter=new image_slider_adapter(list);
                    binding.imageslider.setSliderAdapter(image_slider_adapter);
                    binding.imageslider.setAutoCycle(true);
                    binding.imageslider.startAutoCycle();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        BottomNavigationView navigationView=requireActivity().findViewById(R.id.bnav);
        order_calculation.getReference().removeValue();
        navigationView.setVisibility(View.VISIBLE);
        FirebaseRecyclerOptions<vegitablemodel> options=new FirebaseRecyclerOptions.Builder<vegitablemodel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("vegitable"), vegitablemodel.class).build();
        adapter=new VegatibleAdapter(options);
        binding.vegitable.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.vegitable.setItemAnimator(null);
        binding.vegitable.setHasFixedSize(true);
        binding.vegitable.setNestedScrollingEnabled(true);
        binding.vegitable.setAdapter(adapter);
        FirebaseRecyclerOptions<fruits_model> fruits=new FirebaseRecyclerOptions.Builder<fruits_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference("fruits"), fruits_model.class).build();
        fruits_adapter=new fruits_adapter(fruits);
        binding.fruits.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.fruits.setItemAnimator(null);
        binding.fruits.setHasFixedSize(true);
        binding.fruits.setNestedScrollingEnabled(true);
        binding.fruits.setAdapter(fruits_adapter);

        FirebaseRecyclerOptions<grocery_model> grocessary=new FirebaseRecyclerOptions.Builder<grocery_model>()
                .setQuery(FirebaseDatabase.getInstance().getReference("grocery"), grocery_model.class).build();
        grocessry_adapter=new grocessry_adapter(grocessary);
        binding.grocery.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.grocery.setItemAnimator(null);
        binding.grocery.setHasFixedSize(true);
        binding.grocery.setNestedScrollingEnabled(true);
        binding.grocery.setAdapter(grocessry_adapter);
       FirebaseDB db=new FirebaseDB("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"/orders");
        view=LayoutInflater.from(getContext()).inflate(R.layout.order_dialog,null  );
        button=view.findViewById(R.id.btn);
        snackbar= Snackbar.make(requireActivity().findViewById(R.id.home),"", Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
         snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbar.setBehavior(null);
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(view);
        db.getReference().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(getContext(), "Items removed successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        textView=view.findViewById(R.id.textView9);
      /* db.getReference().addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   textView.setText(String.valueOf(snapshot.getChildrenCount()).concat(" Items Added"));
                   snackbar.show();
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {
           }
       });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                        transction();
                        snackbar.dismiss();
                }catch (IllegalStateException e){
                    Log.d("execptio",e.getMessage());
                }
            }
        });*/
        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()).concat("/ordercompleted"));
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("total_order").exists()){
                    reference2.child(snapshot.child("total_order").getValue().toString()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        timout();
        adapter.startListening();
        fruits_adapter.startListening();
        grocessry_adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        fruits_adapter.stopListening();
        grocessry_adapter.stopListening();
    }
    public void transction(){
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.home, new orderlist()).commit();
    }
    private void timout(){
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String  []time=currentTime.toString().split(":");
        if (Integer.parseInt(time[0])>=19){
            alertdialog.show();
        }
    }
}