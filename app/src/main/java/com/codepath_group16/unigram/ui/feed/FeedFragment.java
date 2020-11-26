package com.codepath_group16.unigram.ui.feed;

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

public class FeedFragment extends Fragment {

    private FeedViewModel mFeedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFeedViewModel =
                new ViewModelProvider(this).get(FeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_feed, container, false);
        final TextView textView = root.findViewById(R.id.text_feed);
        mFeedViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}