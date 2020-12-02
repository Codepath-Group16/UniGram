package com.codepath_group16.unigram.ui.post;



/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Date;

/**
 * Simple data class to hold information about an image included in the device's MediaStore.
 */
class MediaStoreImage {
    final static DiffUtil.ItemCallback<MediaStoreImage> DiffCallback = new DiffUtil.ItemCallback<MediaStoreImage>() {
        @Override
        public boolean areItemsTheSame(@NonNull MediaStoreImage oldItem, @NonNull MediaStoreImage newItem) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MediaStoreImage oldItem, @NonNull MediaStoreImage newItem) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return newItem.equals(oldItem);
        }
    };

    long id;
    String displayName;
    Date dateAdded;
    Uri contentUri;

    public MediaStoreImage(long id, String displayName, Date dateAdded, Uri contentUri) {
        this.id = id;
        this.displayName = displayName;
        this.dateAdded = dateAdded;
        this.contentUri = contentUri;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        MediaStoreImage image = (MediaStoreImage) obj;

        return this.id == image.id && this.displayName.equals(image.displayName) && this.contentUri.equals(image.contentUri);
    }
}

class MediaCameraItem extends MediaStoreImage {

    public MediaCameraItem() {
        super(-1, null, null, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (getClass() != obj.getClass())
            return false;

        MediaCameraItem new_photo = (MediaCameraItem) obj;

        return this.id == new_photo.id;
    }
}