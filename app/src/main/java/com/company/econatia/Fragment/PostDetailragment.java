package com.company.econatia.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.econatia.Adapter.PostAdapter;
import com.company.econatia.Model.Post;
import com.company.econatia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PostDetailragment extends Fragment {

    String postid;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private ShareActionProvider mShareActionProvider;

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_post_detailragment, container, false);


       SharedPreferences preferences=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE);
       postid=preferences.getString("postid","none");

       recyclerView=view.findViewById(R.id.recycler_view);
       recyclerView.setHasFixedSize(true);
       LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
       recyclerView.setLayoutManager(linearLayoutManager);
       postList= new ArrayList<>();
       postAdapter= new PostAdapter(getContext(),postList);
       recyclerView.setAdapter(postAdapter);

       readPosts();


        return view;
    }

    private void readPosts() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                Post post =dataSnapshot.getValue(Post.class);
                postList.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }








}



