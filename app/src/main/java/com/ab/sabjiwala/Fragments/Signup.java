package com.ab.sabjiwala.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ab.sabjiwala.Activity.Home;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.FragmentSignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends Fragment {
    FragmentSignupBinding binding;
    AlertDialog.Builder alertDialog;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference reference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Signup() {
    }
    public static Signup newInstance(String param1, String param2) {
        Signup fragment = new Signup();
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
        requireActivity().getWindow().setStatusBarColor(Color.parseColor("#F57C00"));
        binding=FragmentSignupBinding.inflate(getLayoutInflater(),container,false);
        auth=FirebaseAuth.getInstance();
        binding.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login login=new Login();
                FragmentManager manager=requireActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mainlayout,login).commit();
            }
        });
        alertDialog=new AlertDialog.Builder(requireContext());
        alertDialog.setMessage("Do you want to exit ?");
        alertDialog.setCancelable(false);
        alertDialog.create();
        dialog=new ProgressDialog(requireContext());
        dialog.setMessage("Registering user...");
        dialog.setCancelable(false);
        dialog.create();
        requireActivity().getOnBackPressedDispatcher().addCallback(new onbackpressed(true));
        alertDialog.setPositiveButton("Yes",new onexit());
        alertDialog.setNegativeButton("No",new dismissdialog());
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.name.getText().toString())){
                    showsnack("Name is Empty!");
                }else if (TextUtils.isEmpty(binding.email.getText().toString())){
                    showsnack("Email is Empty!");
                }else if (TextUtils.isEmpty(binding.phone.getText().toString())){
                    showsnack("Phone Number is Empty!");
                }else if (!(binding.phone.getText().length() ==10)){
                    showsnack(" Wrong Phone number!");
                }else if (TextUtils.isEmpty(binding.password.getText().toString())){
                    showsnack("Password  is Empty!");
                }else {
                    dialog.show();
                    auth.createUserWithEmailAndPassword(binding.email.getText().toString(),binding.password.getText().toString())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    showsnack(e.getMessage());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            reference= FirebaseDatabase.getInstance().getReference("user/"+auth.getCurrentUser().getUid().toString());
                            reference.child("name").setValue(binding.name.getText().toString());
                            reference.child("email").setValue(binding.email.getText().toString());
                            reference.child("phone").setValue(binding.phone.getText().toString());
                            reference.child("password").setValue(binding.password.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Snackbar.make(binding.getRoot(),"Successfully Registered!",BaseTransientBottomBar.LENGTH_SHORT)
                                                    .setBackgroundTint(Color.rgb(97, 207, 134)).show();
                                            dialog.dismiss();
                                            Intent intent=new Intent(requireContext(), Home.class);
                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    requireActivity().startActivity(intent);
                                                    requireActivity().finish();
                                                }
                                            },1500);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showsnack(e.getMessage());
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });
        return binding.getRoot();
    }
    public void showsnack(String msg){
        Snackbar.make(binding.getRoot(),msg, BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
    }
    class transtition implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            FragmentManager manager=requireActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainlayout,new Login()).commit();
        }
    }
    class onbackpressed extends OnBackPressedCallback{
        public onbackpressed(boolean enabled) {
            super(enabled);
        }

        @Override
        public void handleOnBackPressed() {
            alertDialog.show();
        }
    }
    class onexit implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            requireActivity().finish();
           // System.exit(0);
        }
    }
    class dismissdialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }
}