package com.codepath_group16.unigram.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.codepath_group16.unigram.R;

public class PostFragment extends Fragment {

    private PostViewModel mPostViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mPostViewModel =
                new ViewModelProvider(this).get(PostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_post, container, false);
        final TextView textView = root.findViewById(R.id.text_post);
        mPostViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}