package com.company.econatia;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.company.econatia.Adapter.CommentsAdapter;
import com.company.econatia.Model.Comments;
import com.company.econatia.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comments> commentsList;

    EditText addcomment;
    ImageView image_profile;
    TextView post;
    String postid;
    String publisherid;

    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        postid=intent.getStringExtra("postid");
        publisherid=intent.getStringExtra("publisherid");

        recyclerView = findViewById(R.id.recycler_view_comment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this , commentsList , postid);
        recyclerView.setAdapter(commentsAdapter);

        addcomment = findViewById(R.id.add_comment);
        image_profile=findViewById(R.id.image_profile);
        post=findViewById(R.id.post);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals("")) {
                    Toast.makeText(CommentsActivity.this,"you can't send empty comment",Toast.LENGTH_SHORT).show();
                }else{
                    addcomment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addcomment(){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid = reference.push().getKey();

        HashMap<String, Object>hashMap= new HashMap<>();
        hashMap.put("comment",addcomment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());
        hashMap.put("commentid",commentid);

        reference.child(commentid).setValue(hashMap);
        addNotifications();
        addcomment.setText("");


    }
    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Commented" + " " +addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(getResources() , R.drawable.ic_heart))
            .setContentTitle("Notification from EcoNatia")
            .setContentText("Commented:" + addcomment.getText().toString());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1 , notificationBuilder.build());

        reference.push().setValue(hashMap);
    }
    private void getImage(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void readComments(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comments comments = snapshot.getValue(Comments.class);
                    commentsList.add(comments);
                }

                commentsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}




