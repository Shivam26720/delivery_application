package com.ab.sabjiwala;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

public class FirebaseDB {
    DatabaseReference reference;
    public  FirebaseDB(String path){
        reference= FirebaseDatabase.getInstance().getReference(path);
    }
    public Boolean insert(String child,String Value){
        final Boolean[] flag = new Boolean[1];
        reference.child(child).setValue(Value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               flag[0] =true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag[0]=false;
            }
        });
       return flag[0];
    }
    public DataSnapshot Read(String child,String Value){
        final DataSnapshot[] snapshot2 = new DataSnapshot[1];
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot2[0] =snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                snapshot2[0] =null;
            }
        });
        return snapshot2[0];
    }
    public DatabaseReference getReference(){
        return reference;
    }
    public String getUserId(){
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().toString();
    }
}
