package com.ab.sabjiwala.Fragments;

import android.app.ProgressDialog;
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

import com.ab.sabjiwala.Activity.Home;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends Fragment {
    FragmentLoginBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog,dialog2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Login(){
    }
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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
        binding=FragmentLoginBinding.inflate(getLayoutInflater(),container,false);
        requireActivity().getOnBackPressedDispatcher().addCallback(new onbackpress(true));
        binding.signupbtn.setOnClickListener(new loginbtn());
        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(requireContext());
        dialog.setMessage("Logging in...");
        dialog.setCancelable(false);
        dialog.create();
        dialog2=new ProgressDialog(requireContext());
        dialog2.setMessage("Sending Reset Link...");
        dialog2.setCancelable(false);
        dialog2.create();
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.editText.getText())){
                    showsnack("Email is Empty!");
                }else if(TextUtils.isEmpty(binding.editText2.getText())){
                    showsnack("Password is Empty!");
                }else{
                    dialog.show();
                    auth.signInWithEmailAndPassword(binding.editText.getText().toString(),binding.editText2.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Snackbar.make(binding.getRoot(),"Successfully Logged In!",BaseTransientBottomBar.LENGTH_SHORT)
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
            }
        });
        binding.textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.editText2.getText())){
                    showsnack("Email is Empty!");
                }else{
                    dialog2.show();
                    auth.sendPasswordResetEmail(binding.editText2.getText().toString())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showsnack(e.getMessage());
                                    dialog2.dismiss();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Snackbar.make(binding.getRoot(),"Password reset link successfully sent!",BaseTransientBottomBar.LENGTH_SHORT)
                                    .setBackgroundTint(Color.rgb(97, 207, 134)).show();
                            dialog2.dismiss();
                        }
                    });
                }
            }
        });
        return binding.getRoot();
    }
    public void transition(){
        FragmentManager manager=requireActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainlayout,new Signup()).commit();
    }
    public void showsnack(String msg){
        Snackbar.make(binding.getRoot(),msg, BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
    }
    class onbackpress extends OnBackPressedCallback {
        public onbackpress(boolean enabled) {
            super(enabled);
        }
        @Override
        public void handleOnBackPressed() {
            transition();
        }
    }
    class loginbtn implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            transition();
        }
    }
}