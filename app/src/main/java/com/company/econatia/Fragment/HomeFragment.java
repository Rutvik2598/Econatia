package com.company.econatia.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.company.econatia.Adapter.PostAdapter;
import com.company.econatia.Model.Post;
import com.company.econatia.NotificationActivity;
import com.company.econatia.PostSelectActivity;
import com.company.econatia.R;
import com.company.econatia.Request_Pickup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private ImageView notification;
    private ImageView add_post;
    private ImageView request_pickup;

    private RecyclerView recyclerView_story;
    /*private StoryAdapter storyAdapter;
    private List<Story> storyList;*/

    private List<String> followingList;

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view =  inflater.inflate(R.layout.fragment_home, container, false);

        notification = view.findViewById(R.id.notification);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , NotificationActivity.class);
                ContextCompat.startForegroundService(getContext() , intent);
                startActivity(intent);
            }
        });

        add_post = view.findViewById(R.id.add_post);

        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , PostSelectActivity.class);
                ContextCompat.startForegroundService(getContext() , intent);
                startActivity(intent);
            }
        });

        request_pickup=view.findViewById(R.id.request_pickup);

        request_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , Request_Pickup.class);
                ContextCompat.startForegroundService(getContext() , intent);
                startActivity(intent);
            }
        });


        recyclerView = view.findViewById(R.id.recycler_view12);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , postLists);
        recyclerView.setAdapter(postAdapter);


        /*recyclerView_story = view.findViewById(R.id.recycler_view_story1);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext() , LinearLayoutManager.HORIZONTAL , false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext() , storyList);
        recyclerView_story.setAdapter(storyAdapter);*/

        progressBar = view.findViewById(R.id.progress_circular);

        checkFollowing();

        return view;
    }

    private void checkFollowing(){

        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                readPosts();
               // readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readPosts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for(String id : followingList){
                        if(post.getPublisher().equals(id)){
                            postLists.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                //storyList.clear();
                storyList.add(new Story("" , 0 , 0 , "" , FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for(String id : followingList){
                    int countStory = 0;
                    Story story = null;
                    for(DataSnapshot snapshot : dataSnapshot.child(id).getChildren()){
                        story = snapshot.getValue(Story.class);
                        if(timecurrent > story.getTimeStart() && timecurrent < story.getTimeOut()){
                            countStory++;
                        }
                    }
                    if(countStory > 0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
*/
}


