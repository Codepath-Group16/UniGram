package com.codepath_group16.unigram.ui.post;

import android.app.Application;
import android.content.ContentUris;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostViewModel extends AndroidViewModel {

    private final String TAG = getClass().getSimpleName();
    private final MutableLiveData<List<MediaStoreImage>> mImages = new MutableLiveData<>();
    private final MutableLiveData<MediaStoreImage> selectedImage = new MutableLiveData<>();
    private ContentObserver contentObserver = null;

    public PostViewModel(Application application) {
        super(application);
    }

    public LiveData<List<MediaStoreImage>> getImages() {
        return mImages;
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
            List<MediaStoreImage> imageList = queryImages();
            mImages.postValue(imageList);
        };

        new Handler(Looper.getMainLooper()).post(runnable);


        if (contentObserver == null) {

            contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadImages();
                }
            };

            getApplication().getContentResolver().registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,
                    contentObserver
            );
        }
    }


    private List<MediaStoreImage> queryImages() {
        ArrayList<MediaStoreImage> images = new ArrayList<>();

        /*
         * Add the open the camera item as the first item
         */
        images.add(new MediaCameraItem());

        /*
         * A key concept when working with Android {@link ContentProvider}s is something called
         * "projections". A projection is the list of columns to request from the provider,
         * and can be thought of (quite accurately) as the "SELECT ..." clause of a SQL
         * statement.
         *
         * It's not _required_ to provide a projection. In this case, one could pass `null`
         * in place of `projection` in the call to [ContentResolver.query], but requesting
         * more data than is required has a performance impact.
         *
         * For this sample, we only use a few columns of data, and so we'll request just a
         * subset of columns.
         */
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };

        /*
         * Sort order to use. This can be null, which will use the default sort
         * order. For {@link MediaStore.Images}, the default sort order is ascending by date taken.
         */
        String sortOrder = String.format("%s DESC", MediaStore.Images.Media.DATE_ADDED);

        Cursor cursor = getApplication().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        );

        /*
         * In order to retrieve the data from the {@link Cursor} that's returned, we need to
         * find which index matches each column that we're interested in.
         *
         * There are two ways to do this. The first is to use the method
         * {@link Cursor.getColumnIndex} which returns -1 if the column ID isn't found. This
         * is useful if the code is programmatically choosing which columns to request,
         * but would like to use a single method to parse them into objects.
         *
         * In our case, since we know exactly which columns we'd like, and we know
         * that they must be included (since they're all supported from API 1), we'll
         * use {@link Cursor.getColumnIndexOrThrow}. This method will throw an
         * {@link IllegalArgumentException} if the column named isn't found.
         *
         * In either case, while this method isn't slow, we'll want to cache the results
         * to avoid having to look them up for each row.
         */
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

        Log.i(TAG, String.format("Found %d images", cursor.getCount()));
        while (cursor.moveToNext()) {

            // Here we'll use the column indexes that we found above.
            long id = cursor.getLong(idColumn);
            Date dateModified =
                    new Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)));
            String displayName = cursor.getString(displayNameColumn);


            /*
             * This is one of the trickiest parts:
             *
             * Since we're accessing images (using
             * {@link MediaStore.Images.Media.EXTERNAL_CONTENT_URI}, we'll use that
             * as the base URI and append the ID of the image to it.
             *
             * This is the exact same way to do it when working with {@link MediaStore.Video} and
             * {@link MediaStore.Audio} as well. Whatever `Media.EXTERNAL_CONTENT_URI` you
             * query to get the items is the base, and the ID is the document to
             * request there.
             */
            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
            );

            MediaStoreImage image = new MediaStoreImage(id, displayName, dateModified, contentUri);
            images.add(image);

            // For debugging, we'll output the image objects we create to logcat.
            Log.v(TAG, "Added image: " + image);
        }

        Log.v(TAG, String.format("Found %d images", images.size()));
        cursor.close();

        return images;
    }

    public void selectImage(MediaStoreImage image) {
        selectedImage.setValue(image);
    }

    public LiveData<MediaStoreImage> getSelectedImage() {
        return selectedImage;
    }
}