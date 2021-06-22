package com.company.econatia.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.company.econatia.Adapter.PostAdapter;
import com.company.econatia.Model.Post;
import com.company.econatia.Model.User;
import com.company.econatia.NotificationActivity;
import com.company.econatia.PostSelectActivity;
import com.company.econatia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String SAVED_LAYOUT_MANAGER = "layout_manager";
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ArrayList<Post> postLists;
    private ImageView notification;
    private ImageView add_post;
    private ImageView request_pickup;
    private TextView pls_follow, no_posts;
    private Button click_follow;
    Fragment selectedFragment = null;
    SwipeRefreshLayout swipeRefreshLayout;

    private List<String> followingList;

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        //setRetainInstance(true);
        view =  inflater.inflate(R.layout.fragment_home, container, false);

        notification = view.findViewById(R.id.notification);
        pls_follow = view.findViewById(R.id.pls_follow);
        click_follow = view.findViewById(R.id.click_follow);
        no_posts = view.findViewById(R.id.no_posts);
        //swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


        if(savedInstanceState != null){

        }

        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkFollowing();
            }
        });*/

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

        /*request_pickup=view.findViewById(R.id.request_pickup);

        request_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , Request_Pickup.class);
                ContextCompat.startForegroundService(getContext() , intent);
                startActivity(intent);
            }
        });*/


        recyclerView = view.findViewById(R.id.recycler_view12);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , (List<Post>) postLists);
        recyclerView.setAdapter(postAdapter);

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
                if(followingList.isEmpty()){
                    pls_follow.setVisibility(View.VISIBLE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(getContext() == null){
                                return;
                            }

                            User user = dataSnapshot.getValue(User.class);
                            pls_follow.setText("Hi "+user.getFullname()+", Please follow some pages to see posts.");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    click_follow.setVisibility(View.VISIBLE);
                    no_posts.setVisibility(View.VISIBLE);
                    click_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedFragment = new SearchFragment();
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container ,
                                    selectedFragment).commit();
                        }
                    });
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readPosts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        //swipeRefreshLayout.setRefreshing(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                //swipeRefreshLayout.setRefreshing(false);
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
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}


