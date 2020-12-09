package com.codepath_group16.unigram.ui.post;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath_group16.unigram.databinding.FragmentCompletePostBinding;

public class CompletePostFragment extends Fragment {

    private FragmentCompletePostBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentCompletePostBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Uri imageUri = CompletePostFragmentArgs.fromBundle(getArguments()).getImageUri();

        Glide.with(requireContext())
                .load(imageUri)
                .centerCrop()
                .into(mBinding.selectedImage);
    }
}