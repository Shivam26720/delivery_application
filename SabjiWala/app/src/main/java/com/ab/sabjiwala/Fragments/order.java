package com.ab.sabjiwala.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ab.sabjiwala.databinding.FragmentOrderBinding;
import com.ab.sabjiwala.databinding.FragmentOrderlistBinding;
import com.ab.sabjiwala.model.user_total_order_model;
import com.ab.sabjiwala.Adapters.user_total_order_adapter;

import com.ab.sabjiwala.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

public class order extends Fragment {
    FragmentOrderBinding binding;
    user_total_order_adapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public order() {
    }
    public static order newInstance(String param1, String param2) {
        order fragment = new order();
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
        binding=FragmentOrderBinding.inflate(getLayoutInflater(),container,false);
        requireActivity().getWindow().setStatusBarColor(Color.rgb(255,255,255));
        try {
            FirebaseRecyclerOptions<user_total_order_model> options = new FirebaseRecyclerOptions.Builder<user_total_order_model>()
                    .setQuery(FirebaseDatabase.getInstance().getReference("user/".concat(FirebaseAuth.getInstance().getCurrentUser().getUid()).concat("/ordercompleted")), user_total_order_model.class).build();
            adapter = new user_total_order_adapter(options);
            binding.order.setHasFixedSize(true);
            binding.order.setItemAnimator(null);
            binding.order.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true));
            binding.order.setNestedScrollingEnabled(true);
            binding.order.setAdapter(adapter);
        }catch (DatabaseException e){
            Log.d("execption",e.getMessage());
        }
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}