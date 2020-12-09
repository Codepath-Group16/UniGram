package com.codepath_group16.unigram.ui.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.codepath_group16.unigram.R;
import com.codepath_group16.unigram.data.models.Post;
import com.codepath_group16.unigram.databinding.FragmentCompletePostBinding;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class CompletePostFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentCompletePostBinding mBinding;
    private Uri mImageUri;
    private ProgressBar mProgressBar;

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

        mImageUri = CompletePostFragmentArgs.fromBundle(requireArguments()).getImageUri();

        mProgressBar = mBinding.progressBar;

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mBinding.getRoot().getWindowInsetsController().hide(WindowInsets.Type.ime());
            } else {
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mBinding.getRoot().getWindowToken(), 0);
            }
            postImage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void postImage() {
        Post post = new Post();
        post.setCaption(Objects.requireNonNull(mBinding.captionInput.getEditText()).getText().toString());

        mProgressBar.setVisibility(View.VISIBLE);
        byte[] image;

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), mImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Objects.requireNonNull(bitmap).compress(Bitmap.CompressFormat.PNG, 100, stream);
        image = stream.toByteArray();
        ParseFile parseImageFile = new ParseFile("post.png", image);

        if (isConnected()) {
            parseImageFile.saveInBackground(e -> {
                if (e == null) {
                    post.setImage(parseImageFile);
                    completePostingImage(post);
                } else {
                    Snackbar.make(mBinding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }, percentDone -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mProgressBar.setProgress(percentDone - 10, true);
                } else {
                    mProgressBar.setProgress(percentDone - 10);
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            Snackbar.make(mBinding.getRoot(), R.string.no_connection, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void completePostingImage(Post post) {
        post.setAuthor(ParseUser.getCurrentUser());

        Log.i(TAG, "completePostingImage: ");

        if (isConnected()) {
            post.saveInBackground(e -> {
                if (e == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mProgressBar.setProgress(100, true);
                    } else {
                        mProgressBar.setProgress(100);
                    }
                    Navigation.findNavController(mBinding.getRoot()).navigate(
                            CompletePostFragmentDirections.actionNavigationCompletePostToNavigationFeed()
                    );
                } else {
                    Snackbar.make(mBinding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            Snackbar.make(mBinding.getRoot(), R.string.no_connection, Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}