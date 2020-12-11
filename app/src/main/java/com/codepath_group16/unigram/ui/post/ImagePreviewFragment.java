package com.codepath_group16.unigram.ui.post;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.codepath_group16.unigram.R;
import com.codepath_group16.unigram.databinding.FragmentImagePreviewBinding;


public class ImagePreviewFragment extends Fragment {

    private FragmentImagePreviewBinding mBinding;
    private ImageView mCapturedImage;
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
        mBinding = FragmentImagePreviewBinding.inflate(inflater, container, false);
        mCapturedImage = mBinding.capturedImage;

        mImageUri = ImagePreviewFragmentArgs.fromBundle(requireArguments()).getImageUri();

        mBinding.retakePictureButton.setOnClickListener(v -> Navigation.findNavController(mBinding.getRoot()).navigate(
                ImagePreviewFragmentDirections.actionNavigationImagePreviewToNavigationCaptureImage()
        ));
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(requireContext())
                .load(mImageUri)
                .into(mCapturedImage);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.post_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_next) {
            Navigation.findNavController(mBinding.getRoot())
                    .navigate(
                            ImagePreviewFragmentDirections.actionNavigationImagePreviewToNavigationCompletePost(mImageUri)
                    );
        }
        return super.onOptionsItemSelected(item);
    }
}