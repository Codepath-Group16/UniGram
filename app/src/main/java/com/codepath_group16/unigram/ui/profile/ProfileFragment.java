package com.codepath_group16.unigram.ui.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codepath_group16.unigram.R;
import com.codepath_group16.unigram.data.models.Post;
import com.codepath_group16.unigram.databinding.FragmentProfileBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private FragmentProfileBinding mBinding;
    private ParseUser mCurrentUser;
    private TextView mTvBio;
    private ImageView mIvProfile;
    private ProgressBar mProgressBarProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ProfileViewModel profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        setHasOptionsMenu(true);
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);

        mCurrentUser = ParseUser.getCurrentUser();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = ((AppCompatActivity) requireActivity());

        activity.setSupportActionBar(mBinding.profileTopAppBar);

        ActionBar actionBar = Objects.requireNonNull(activity.getSupportActionBar());
        actionBar.setTitle(ParseUser.getCurrentUser().getUsername());

        // find views by id
        ViewPager2 viewPager = mBinding.viewPager;
        TabLayout tabLayout = mBinding.tabLayout;

        DemoCollectionAdapter demoCollectionAdapter;
        demoCollectionAdapter = new DemoCollectionAdapter(this);

        // set adapter on viewpager
        viewPager.setAdapter(demoCollectionAdapter);

        // attach tablayout with viewpager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("OBJECT " + (position + 1))
        ).attach();


        mTvBio = mBinding.tvBio;
        mIvProfile = mBinding.ivProfile;
        mProgressBarProfile = mBinding.progressProfilePicture;
        mProgressBarProfile.setVisibility(View.VISIBLE);

        mCurrentUser.fetchInBackground((object, e) -> {
            if (e == null) {
                updateUserData();
            } else {
                // Error
                Log.e(TAG, "done: ", e);
            }
        });

        showDefaultProfile();
        updateUserData();
    }

    public static class DemoCollectionAdapter extends FragmentStateAdapter {
        public DemoCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            return new DemoObjectFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    // Instances of this class are fragments representing a single
// object in our collection.
    public static class DemoObjectFragment extends Fragment {

        private ProfileViewModel mProfileViewModel;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            mProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
            return inflater.inflate(R.layout.gallery, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            ProfileFragment.GalleryAdapter galleryAdapter = new ProfileFragment.GalleryAdapter();
            RecyclerView gallery = view.findViewById(R.id.gallery);
            gallery.setAdapter(galleryAdapter);

            gallery.setLayoutManager(new GridLayoutManager(getContext(), 3));

            mProfileViewModel.getPosts().observe(requireActivity(), galleryAdapter::submitList);
        }
    }

    private void updateUserData() {
        mTvBio.setText(mCurrentUser.getString("bio"));

        ParseFile profilePicture = mCurrentUser.getParseFile("profilePicture");
        if (profilePicture != null) {
            Glide.with(this)
                    .load(profilePicture.getUrl())
                    .circleCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mProgressBarProfile.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mProgressBarProfile.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .fallback(R.drawable.default_profile)
                    .into(mIvProfile);
        } else {
            // Set up default profile
            showDefaultProfile();
            mProgressBarProfile.setVisibility(View.GONE);
        }
    }

    private void showDefaultProfile() {
        Glide.with(this)
                .load(R.drawable.default_profile)
                .circleCrop()
                .into(mIvProfile);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_log_out) {
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        ParseUser.logOutInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "onViewCreated: Error Logging out", e);
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        Navigation.findNavController(mBinding.getRoot()).navigate(
                ProfileFragmentDirections.actionNavigationProfileToLoginActivity()
        );

        requireActivity().finish();
    }

    /**
     * A {@link ListAdapter for {@link Post }s.
     */
    private static class GalleryAdapter extends ListAdapter<Post, RecyclerView.ViewHolder> {

        protected GalleryAdapter() {
            super(Post.DiffCallback);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.gallery_layout, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            Post post = getItem(position);
            ProfileFragment.GalleryAdapter.ImageViewHolder h = (ProfileFragment.GalleryAdapter.ImageViewHolder) holder;
            h.getRootView().setTag(post);

            Glide.with(h.getImageView())
                    .load(post.getImage().getUrl())
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(h.getImageView());

        }

        /**
         * Basic {@link RecyclerView.ViewHolder} for our gallery.
         */
        static class ImageViewHolder extends RecyclerView.ViewHolder {

            View mRootView;
            ImageView mImageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                mRootView = itemView;
                mImageView = Objects.requireNonNull(itemView).findViewById(R.id.image);
            }

            public ImageView getImageView() {
                return mImageView;
            }

            public View getRootView() {
                return mRootView;
            }

        }
    }

}