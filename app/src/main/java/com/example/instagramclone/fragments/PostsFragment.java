package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";
    private RecyclerView rvPosts;

    protected PostsAdapter adapter;
    protected List<Post> allPosts;

    public PostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);

        // steps to use the recycler view:
        // 0. create layout for one row in the list
        // 1. create the adapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        // 2. create the data source
        // 3. set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // 4. set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }


    // get a post from our posts database
    // setting as protected will allow us to use it in the ProfileFragement.java class
    protected void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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