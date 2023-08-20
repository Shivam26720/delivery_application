package com.ab.sabjiwala.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ab.sabjiwala.Fragments.Signup;
import com.ab.sabjiwala.Fragments.Splash;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    AppUpdateManager updateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(255,255,255));
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());
        Handler handler=new Handler();
        fragment_transition(new Splash());
        auth=FirebaseAuth.getInstance();
        updateManager=AppUpdateManagerFactory.create(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    if (auth.getCurrentUser() != null) {
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            fragment_transition(new Signup());
                        } catch (IllegalStateException e) {
                            Log.d("execpiton", e.getMessage());
                        }
                    }
            }
        },3000);
    }
    public void fragment_transition(Fragment fragment){
        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mainlayout,fragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    public void check_updates(){
        updateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE&&appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        updateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,MainActivity.this,100);
                        updateManager.registerListener(new listner());
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }else{
                    updateManager.unregisterListener(new listner());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
               // Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    class listner implements InstallStateUpdatedListener{

        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if (installState.installStatus()== InstallStatus.DOWNLOADED){
                updateManager.completeUpdate();
            }
            if (installState.installStatus()==InstallStatus.DOWNLOADING){
                   showsnack("Downloading update");
            }
        }
    }
    public void showsnack(String msg){
        Snackbar.make(binding.mainlayout,msg, BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setBackgroundTint(Color.parseColor("#ff6d00")).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
              if (resultCode==RESULT_OK){
                  Toast.makeText(MainActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
              }else if (resultCode==RESULT_CANCELED){
                  Toast.makeText(MainActivity.this,"Update cancelled", Toast.LENGTH_SHORT).show();
              }else if (resultCode== ActivityResult.RESULT_IN_APP_UPDATE_FAILED){
                 // check_updates();
              }
        }
    }
}