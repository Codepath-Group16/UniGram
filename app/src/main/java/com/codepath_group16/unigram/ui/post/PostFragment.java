package com.codepath_group16.unigram.ui.post;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.codepath_group16.unigram.R;
import com.codepath_group16.unigram.databinding.FragmentPostBinding;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.Objects;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class PostFragment extends Fragment {

    private static PostViewModel mPostViewModel;
    private final String TAG = getClass().getSimpleName();
    /**
     * The request code for requesting Manifest.permission.READ_EXTERNAL_STORAGE permission.
     */
    private final int READ_EXTERNAL_STORAGE_REQUEST = 0x1045;
    private FragmentPostBinding mBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mPostViewModel =
                new ViewModelProvider(this).get(PostViewModel.class);

        mBinding = FragmentPostBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);

        GalleryAdapter galleryAdapter = new GalleryAdapter(requireContext());
        mBinding.gallery.setAdapter(galleryAdapter);

        // Prevent flickering when item is clicked
        SimpleItemAnimator animator = (SimpleItemAnimator) mBinding.gallery.getItemAnimator();
        Objects.requireNonNull(animator).setSupportsChangeAnimations(false);

        mBinding.gallery.setLayoutManager(new GridLayoutManager(getContext(), 4));

        mPostViewModel.getImages().observe(requireActivity(), mediaStoreImages -> {
            galleryAdapter.submitList(mediaStoreImages);
            if (mediaStoreImages.size() > 1) {
                if (mBinding != null) {
                    mBinding.emptyGallery.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mBinding != null) {
                    mBinding.emptyGallery.setVisibility(View.VISIBLE);
                }
            }
        });
        mPostViewModel.getSelectedImage().observe(requireActivity(), this::showSelectedImage);

        mBinding.openAlbum.setOnClickListener(v -> openMediaStore());
        mBinding.grantPermissionButton.setOnClickListener(v -> openMediaStore());

        if (!haveStoragePermission()) {
            mBinding.welcomeView.setVisibility(View.VISIBLE);
            mBinding.emptyGallery.setVisibility(View.GONE);
            mBinding.gallery.setVisibility(View.GONE);
        } else {
            showImages();
        }

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.post_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_next).setVisible(haveStoragePermission());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_next) {
            Navigation.findNavController(mBinding.getRoot())
                    .navigate(
                            PostFragmentDirections.actionNavigationPostToNavigationCompletePost(
                                    Objects.requireNonNull(mPostViewModel.getSelectedImage().getValue()).contentUri
                            )
                    );
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                requireActivity().invalidateOptionsMenu();
                showImages();
            } else {
                // If we weren't granted the permission, check to see if we should show
                // rationale for the permission.
                boolean showRationale = shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                );

                /*
                  If we should show the rationale for requesting storage permission, then
                  we'll show {@link ActivityMainBinding.permissionRationaleView} which does this.

                  If `showRationale` is false, this means the user has not only denied
                  the permission, but they've clicked "Don't ask again". In this case
                  we send the user to the settings page for the app so they can grant
                  the permission (Yay!) or uninstall the app.
                 */
                if (showRationale) {
                    showNoAccess();
                } else {
                    goToSettings();
                }
            }
        }
    }

    private void showNoAccess() {
        mBinding.welcomeView.setVisibility(View.GONE);
        mBinding.emptyGallery.setVisibility(View.GONE);
        mBinding.gallery.setVisibility(View.GONE);
        mBinding.permissionRationaleView.setVisibility(View.VISIBLE);
    }

    private void goToSettings() {
        Intent i = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireContext().getPackageName()))
                .addCategory(Intent.CATEGORY_DEFAULT)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void openMediaStore() {
        if (haveStoragePermission()) {
            showImages();
        } else {
            requestPermission();
        }
    }

    private void showImages() {
        mBinding.welcomeView.setVisibility(View.GONE);
        mBinding.permissionRationaleView.setVisibility(View.GONE);
        mBinding.gallery.setVisibility(View.VISIBLE);
        mPostViewModel.loadImages();
    }


    private void showSelectedImage(MediaStoreImage image) {
        Uri imageUri = null;
        if (!(image == null)) {
            imageUri = image.contentUri;
        }
        if (mBinding != null) {
            Glide.with(requireContext())
                    .load(imageUri)
                    .centerCrop()
                    .into(mBinding.selectedImage);
        }
    }

    /**
     * Convenience method to check if Manifest.permission.READ_EXTERNAL_STORAGE permission
     * has been granted to the app.
     */
    private boolean haveStoragePermission() {
        return PermissionChecker.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED;
    }

    /**
     * Convenience method to request Manifest.permission.READ_EXTERNAL_STORAGE permission.
     */
    private void requestPermission() {
        if (!haveStoragePermission()) {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestPermissions(permissions, READ_EXTERNAL_STORAGE_REQUEST);
        }
    }

    /**
     * A {@link ListAdapter for {@link MediaStoreImage}s.
     */
    private static class GalleryAdapter extends ListAdapter<MediaStoreImage, RecyclerView.ViewHolder> {

        final int IMAGE_VIEW_TYPE = 0;
        final int OPEN_CAMERA_VIEW_TYPE = 1;
        private final Context mContext;

        protected GalleryAdapter(Context context) {
            super(MediaStoreImage.DiffCallback);
            mContext = context;
        }

        @Override
        public int getItemViewType(int position) {
            if (MediaCameraItem.class.equals(getItem(position).getClass())) {
                return OPEN_CAMERA_VIEW_TYPE;
            }
            return IMAGE_VIEW_TYPE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case OPEN_CAMERA_VIEW_TYPE:
                    View openCameraView = layoutInflater.inflate(R.layout.gallery_open_camera_layout, parent, false);
                    return new NewImageViewHolder(openCameraView);
                case IMAGE_VIEW_TYPE:
                default:
                    View view = layoutInflater.inflate(R.layout.gallery_layout, parent, false);
                    return new ImageViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            switch (holder.getItemViewType()) {
                case OPEN_CAMERA_VIEW_TYPE:
                    break;
                case IMAGE_VIEW_TYPE:
                default:
                    MediaStoreImage mediaStoreImage = getItem(position);
                    ImageViewHolder h = (ImageViewHolder) holder;
                    h.getRootView().setTag(mediaStoreImage);

                    Glide.with(h.getImageView())
                            .load(mediaStoreImage.contentUri)
                            .thumbnail(0.33f)
                            .centerCrop()
                            .into(h.getImageView());

                    if (position == mPostViewModel.getCurrentSelectedImagePosition()) {
                        h.selectedBg();
                    } else {
                        h.defaultBg();
                    }
            }

        }

        /**
         * Basic {@link RecyclerView.ViewHolder} for our gallery.
         */
        class ImageViewHolder extends RecyclerView.ViewHolder {

            View mRootView;
            ImageView mImageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                mRootView = itemView;
                mImageView = Objects.requireNonNull(itemView).findViewById(R.id.image);

                mImageView.setOnClickListener(v -> {
                    MediaStoreImage image = (MediaStoreImage) mRootView.getTag();
                    mPostViewModel.selectImage(image, getAdapterPosition());

                    if (mPostViewModel.getPreviousSelectedImagePosition() != -1) {
                        notifyItemChanged(mPostViewModel.getPreviousSelectedImagePosition());
                    }
                    mPostViewModel.setPreviousSelectedImagePosition(mPostViewModel.getCurrentSelectedImagePosition());
                    notifyItemChanged(mPostViewModel.getCurrentSelectedImagePosition());
                });
            }

            public ImageView getImageView() {
                return mImageView;
            }

            public View getRootView() {
                return mRootView;
            }

            void defaultBg() {
                mRootView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_image_unselected));
            }

            void selectedBg() {
                mRootView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_image_selected));
            }
        }
    }
}

/**
 * Basic {@link RecyclerView.ViewHolder} for our new image button.
 */
class NewImageViewHolder extends RecyclerView.ViewHolder {

    public NewImageViewHolder(@NonNull View itemView) {
        super(itemView);
        MaterialButton btnNewImage = itemView.findViewById(R.id.new_image);
        btnNewImage.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_navigation_post_to_captureImageFragment));
    }

}