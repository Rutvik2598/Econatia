package com.company.econatia.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.company.econatia.Adapter.MyFotoAdapter;
import com.company.econatia.EditProfileActivity;
import com.company.econatia.FollowersActivity;
import com.company.econatia.Model.Post;
import com.company.econatia.Model.Rewards;
import com.company.econatia.Model.User;
import com.company.econatia.OptionsAcitivity;
import com.company.econatia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView image_profile,options;
    int econs;
    TextView posts,followers,following,fullname,bio,username;
    Button edit_profile;

    private List<String> mySaves;

    RecyclerView recyclerView_save;
    MyFotoAdapter myFotoAdapter_save;
    List<Post>postList_saves;


    RecyclerView recyclerView;
    MyFotoAdapter myFotoAdapter;
    List<Post>postList;



    FirebaseUser firebaseUser;
    String profileid;

    ImageButton my_fotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid" , "none");

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        edit_profile = view.findViewById(R.id.edit_profile);
        my_fotos = view.findViewById(R.id.my_fotos);

        recyclerView = view.findViewById(R.id.recycler_view_post);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList=new ArrayList<>();
        myFotoAdapter =new MyFotoAdapter(getContext(),postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerView.setVisibility(View.VISIBLE);


        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();


        if(profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        }else{
            checkFollow();
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if(btn.equals("Edit Profile")){
                    Intent intent = new Intent(getContext() , EditProfileActivity.class);
                    ContextCompat.startForegroundService(getContext() , intent);
                    startActivity(intent);
                }else if(btn.equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications();
                }else if(btn.equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , OptionsAcitivity.class);
                ContextCompat.startForegroundService(getContext() , intent);
                startActivity(intent);
            }
        });

        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_save.setVisibility(View.GONE);

            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","followers");
                startActivity(intent);

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","following");
                startActivity(intent);

            }
        });


        return view;
    }
    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

    private void userInfo(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkFollow(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
        .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("Following");
                }else{
                    edit_profile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowers(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getNrPosts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rewards").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Rewards rewards = dataSnapshot.getValue(Rewards.class);
                econs = rewards.getEcons();

                String econ = String.valueOf(econs);
                posts.setText(econ);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void myFotos(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}




