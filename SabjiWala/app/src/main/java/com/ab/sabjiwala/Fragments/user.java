package com.ab.sabjiwala.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ab.sabjiwala.Activity.MainActivity;
import com.ab.sabjiwala.R;
import com.ab.sabjiwala.databinding.FragmentUserBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class user extends Fragment {

    FragmentUserBinding binding;
    DatabaseReference reference,p_info;
    BottomSheetDialog dialog;
    RelativeLayout term,privacy,refund;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public user() {
    }

    public static user newInstance(String param1, String param2) {
        user fragment = new user();
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
        reference= FirebaseDatabase.getInstance().getReference("share");
        p_info=FirebaseDatabase.getInstance().getReference("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentUserBinding.inflate(getLayoutInflater(),container,false);
        dialog=new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.about_us_dialog);
        dialog.create();
        privacy=dialog.findViewById(R.id.privacy);
        term=dialog.findViewById(R.id.term);
        refund=dialog.findViewById(R.id.refund);
        requireActivity().getWindow().setStatusBarColor(Color.rgb(255,255,255));
        p_info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.textView12.setText(snapshot.child("name").getValue().toString());
                binding.phone.setText(snapshot.child("phone").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, "grockit.delivery22@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Support");
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    requireActivity().startActivity(intent);
                }
            }
        });
        binding.rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                             Uri uri= Uri.parse(snapshot.child("rate").getValue().toString());
                             Intent goToMarket=new Intent(Intent.ACTION_VIEW,uri);
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(snapshot.child("rate").getValue().toString())));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("share").exists()){
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT,snapshot.child("share").getValue().toString());
                            sendIntent.setType("text/plain");
                            requireActivity().startActivity(sendIntent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getContext(), MainActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().finish();
            }
        });
        binding.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
         term.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 launch("https://grockit22termsandcondition.blogspot.com/2022/10/and-condition-by-downloading-or-using.html");
                 dialog.dismiss();
             }
         });
         privacy.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 launch("https://grockit22.blogspot.com/2022/10/privacy-policy-for-grockit.html");
                 dialog.dismiss();
             }
         });
         refund.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 launch("https://www.blogger.com/blog/post/edit/6902868355930357554/550717687754392607");
             }
         });
        return binding.getRoot();
    }
    public void launch(String url){
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }catch (Exception e){
            Log.d("e",e.getMessage());
        }
    }
}