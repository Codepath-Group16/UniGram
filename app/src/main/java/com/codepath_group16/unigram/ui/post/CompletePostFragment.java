package com.codepath_group16.unigram.ui.post;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.codepath_group16.unigram.R;
import com.codepath_group16.unigram.data.models.Post;
import com.codepath_group16.unigram.databinding.FragmentCompletePostBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class CompletePostFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentCompletePostBinding mBinding;
    private Uri mImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        mImageUri = CompletePostFragmentArgs.fromBundle(getArguments()).getImageUri();

        Glide.with(requireContext())
                .load(mImageUri)
                .centerCrop()
                .into(mBinding.selectedImage);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.complete_post_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_post) {
            postImage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void postImage() {
        Post post = new Post();
        post.setCaption(mBinding.captionInput.getEditText().getText().toString());
        Log.i(TAG, "postImage: " + mImageUri.getPath());
        File imageFile = new File(mImageUri.getPath());
        Log.i(TAG, "postImage: " + imageFile.getName());


        byte[] image;

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), mImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        image = stream.toByteArray();
        ParseFile parseImageFile = new ParseFile("post.png", image);

        parseImageFile.saveInBackground(e -> {
            post.setImage(parseImageFile);
            completePostingImage(post);
        }, percentDone -> Log.i(TAG, "done: " + percentDone));

    }

    private void completePostingImage(Post post) {
        post.setAuthor(ParseUser.getCurrentUser());

        Log.i(TAG, "completePostingImage: ");

        post.saveEventually(e -> {
            if (e == null) {
                Navigation.findNavController(mBinding.getRoot()).navigate(
                        CompletePostFragmentDirections.actionNavigationCompletePostToNavigationFeed()
                );
            } else {
                Log.e(TAG, "postImage: " + e.getLocalizedMessage(), e);
            }
        });
    }
}