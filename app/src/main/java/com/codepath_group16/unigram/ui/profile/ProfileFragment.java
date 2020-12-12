package com.codepath_group16.unigram.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codepath_group16.unigram.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mProfileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}