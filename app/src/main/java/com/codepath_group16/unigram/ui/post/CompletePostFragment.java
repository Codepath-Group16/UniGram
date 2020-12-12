package com.codepath_group16.unigram.ui.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
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

    private FragmentCompletePostBinding mBinding;
    private Uri mImageUri;
    private ProgressBar mProgressBar;
    private Group mPosting;

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

        AppCompatActivity activity = ((AppCompatActivity) requireActivity());

        activity.setSupportActionBar(mBinding.materialToolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(R.string.title_post);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageUri = CompletePostFragmentArgs.fromBundle(requireArguments()).getImageUri();

        mProgressBar = mBinding.progressBar;
        mPosting = mBinding.posting;

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
            mBinding.getRoot().clearFocus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mBinding.getRoot().getWindowInsetsController().hide(WindowInsets.Type.ime());
            } else {
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mBinding.getRoot().getWindowToken(), 0);
            }

            // Post image in background tasks since it reads image from disk
            Runnable runnable = this::postImage;
            new Thread(runnable).start();


        }
        return super.onOptionsItemSelected(item);
    }

    private void postImage() {
        // Use the handler to update the UI
        Handler handler = new Handler(Looper.getMainLooper());
        Post post = new Post();
        post.setCaption(Objects.requireNonNull(mBinding.captionInput.getEditText()).getText().toString());

        handler.post(() -> mPosting.setVisibility(View.VISIBLE));

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
                    handler.post(() -> mPosting.setVisibility(View.GONE));
                }
            }, percentDone -> setProgressBar(percentDone - 20));
        } else {
            handler.post(() -> mPosting.setVisibility(View.GONE));
            handler.post(() -> Snackbar.make(mBinding.getRoot(), R.string.no_connection, Snackbar.LENGTH_SHORT).show());
        }
    }

    private void setProgressBar(int i) {
        Handler handler = new Handler(Looper.getMainLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            handler.post(() -> mProgressBar.setProgress(i, true));
        } else {
            handler.post(() -> mProgressBar.setProgress(i));
        }
    }

    private void completePostingImage(Post post) {
        post.setAuthor(ParseUser.getCurrentUser());

        Handler handler = new Handler(Looper.getMainLooper());

        if (isConnected()) {
            post.saveInBackground(e -> {
                if (e == null) {
                    setProgressBar(100);
                    Navigation.findNavController(mBinding.getRoot()).navigate(
                            CompletePostFragmentDirections.actionNavigationCompletePostToNavigationFeed()
                    );
                } else {
                    handler.post(() -> {
                        Snackbar.make(mBinding.getRoot(), Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_SHORT).show();
                        mPosting.setVisibility(View.GONE);
                    });
                }
            });
        } else {
            handler.post(() -> {
                mPosting.setVisibility(View.GONE);
                Snackbar.make(mBinding.getRoot(), R.string.no_connection, Snackbar.LENGTH_SHORT).show();
            });
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}