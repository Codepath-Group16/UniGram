package com.codepath_group16.unigram.ui.profile;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codepath_group16.unigram.data.models.Post;
import com.parse.ParseQuery;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    public static final int QUERY_LIMIT = 20;
    private final String TAG = getClass().getSimpleName();
    private final MutableLiveData<List<Post>> mPostList = new MutableLiveData<>();

    public ProfileViewModel(Application application) {
        super(application);
        loadImages();
    }

    /**
     * Performs a one shot load of images from MediaStore.Images.Media.EXTERNAL_CONTENT_URI into
     * the {@link images} {@link LiveData} above.
     */
    void loadImages() {

        Runnable runnable = () -> {
            /*
             * Working with {@link ContentResolver}s can be slow, so we'll do this off the main
             * thread inside a runnable.
             */
            List<Post> postList = queryPosts();
            mPostList.postValue(postList);
        };

        new Handler(Looper.getMainLooper()).post(runnable);

    }


    private List<Post> queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        if (mPostList.getValue() != null) {
            Post oldestPost = mPostList.getValue().get(mPostList.getValue().size() - 1);
            query.whereLessThanOrEqualTo(Post.KEY_CREATED_AT, oldestPost.getCreatedAt());
            query.whereNotEqualTo(Post.KEY_AUTHOR, oldestPost.getAuthor().getObjectId());
        }

        query.include(Post.KEY_AUTHOR);
        query.include(Post.KEY_LIKED_BY);
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        try {
            List<Post> posts = query.find();
            for (Post post : posts) {
                Log.i(TAG,
                        "Post desc: " + post.getCaption() + ", username: " + post.getAuthor().getUsername()
                );

            }

            if (posts.size() > 0) {
                return posts;
            }

        } catch (Exception e) {
            Log.e(TAG, "done: Issue with getting more posts", e);
        }

        return null;
    }

}