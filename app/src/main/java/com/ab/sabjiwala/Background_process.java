package com.ab.sabjiwala;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class Background_process extends Thread{
    ArrayList<String> orders;
    int i=0;
    DataSnapshot dataSnapshot;
    FirebaseDB firebaseDB;
    ProgressDialog dialog;
    public Background_process(ArrayList<String> orders,DataSnapshot dataSnapshot,FirebaseDB firebaseDB,ProgressDialog dialog){
        this.orders=orders;
        this.dataSnapshot=dataSnapshot;
        this.firebaseDB=firebaseDB;
        this.dialog=dialog;
    }
    @Override
    public void run() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
               dialog.show();
            }
        });
        for (setI(0);getI()<orders.size();) {
            if (!orders.get(getI()).equals("total")) {
                String title = dataSnapshot.child(orders.get(getI())).child("title").getValue().toString();
                String quantity = dataSnapshot.child(orders.get(getI())).child("quantity").getValue().toString();
                String total = dataSnapshot.child(orders.get(getI())).child("total").getValue().toString();
                String name = orders.get(getI());
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
                        setI(getI() + 1);
                        dialogdismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setI(getI() + 1);
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (getI()==orders.size()){
            System.exit(0);
        }
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
    public void dialogdismiss(){
        if (getI()==orders.size()-1) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }
}
