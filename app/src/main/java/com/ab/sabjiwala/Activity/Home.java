package com.ab.sabjiwala.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.ab.sabjiwala.Fragments.home;
import com.ab.sabjiwala.Fragments.order;
import com.ab.sabjiwala.Fragments.user;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.ActivityHomeBinding;
import com.ab.sabjiwala.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        transiton(new home());
        getWindow().setStatusBarColor(Color.rgb(255,255,255));
        binding.bnav.setItemIconTintList(null);
        binding.bnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        transiton(new home());
                        break;
                    case R.id.order:
                        transiton(new order());
                        break;
                    case R.id.user:
                        transiton(new user());
                        break;
                }
                return true;
            }
        });
    }
    public void transiton(Fragment fragment){
        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.home,fragment).commit();
    }
}