package com.example.instagramclone.fragments;

import android.util.Log;

import com.example.instagramclone.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment {

    // we are overriding the queryPosts method to modify it for showing only the posts created
    // from the logged in user. this is more efficient than creating a new fragment class and
    // copying the code from PostsFragment; reduces code duplication
    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20); // a limit of 20 posts will be loaded
        query.addDescendingOrder(Post.KEY_CREATED_AT); // organise by most recent
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error with retrieving list of posts", e);
                    return ;
                }
                // if reached this point, data fetch is successful, so iterate thru each post
                for (Post post: posts){
                    Log.i(TAG, "Post: " + post.getDescription() +
                            ", Username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
