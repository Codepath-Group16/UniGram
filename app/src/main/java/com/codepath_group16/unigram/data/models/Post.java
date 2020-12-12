package com.codepath_group16.unigram.data.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_LIKED_BY = "likedBy";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_LIKES_COUNT = "likesCount";

    public final static DiffUtil.ItemCallback<Post> DiffCallback = new DiffUtil.ItemCallback<Post>() {
        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldItem.getObjectId().equals(newItem.getObjectId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return newItem.equals(oldItem);
        }
    };
    // By default, a post is not liked by the user
    private boolean isLiked = false;

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Post post = (Post) obj;

        return this.getObjectId().equals(post.getObjectId()) && this.getCaption().equals(post.getCaption());
    }

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String description) {
        put(KEY_CAPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public int getLikesCount() {
        return getInt(KEY_LIKES_COUNT);
    }

    public void setIsLiked(boolean liked, boolean isUpdate) {
        if (isUpdate) {
            ParseRelation<ParseObject> relation = this.getRelation(Post.KEY_LIKED_BY);
            if (liked) {
                relation.add(ParseUser.getCurrentUser());
                this.increment(KEY_LIKES_COUNT);
            } else {
                relation.remove(ParseUser.getCurrentUser());
                this.increment(KEY_LIKES_COUNT, -1);
            }
            this.saveEventually();
        }
        isLiked = liked;
    }

    public boolean getIsLiked() {
        return isLiked;
    }
}
