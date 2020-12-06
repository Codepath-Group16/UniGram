package com.codepath_group16.unigram.ui.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath_group16.unigram.Post;
import com.codepath_group16.unigram.PostsAdapter;
import com.codepath_group16.unigram.R;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private FeedViewModel mFeedViewModel;
    private final String TAG = getClass().getSimpleName();

    private RecyclerView mRvPosts;
    private PostsAdapter mPostAdapter;
    private List<Post> mPostList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFeedViewModel =  new ViewModelProvider(this).get(FeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_feed, container, false);

        queryPosts();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvPosts = view.findViewById(R.id.r_view);

        mPostList = new ArrayList<>();
        mPostAdapter = new PostsAdapter(getContext(), mPostList);

        // Steps to use the recycler view:
        // 0. create the layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        mRvPosts.setAdapter(mPostAdapter);
        // 4. set the layout manager on the recycler view
        mRvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        query.findInBackground((posts, e) -> {
            if (e == null) {
                mPostList.addAll(posts);
                mPostAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "done: " + e.getLocalizedMessage(), e);
            }
        });
    }

}

//Recycle
//