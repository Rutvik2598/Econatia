package com.company.econatia.Adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.company.econatia.Fragment.PostDetailragment;
import com.company.econatia.Fragment.ProfileFragment;
import com.company.econatia.Model.Notification;
import com.company.econatia.Model.Post;
import com.company.econatia.Model.User;
import com.company.econatia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mnotification;

    public NotificationAdapter(Context mContext, List<Notification> mnotification) {
        this.mContext = mContext;
        this.mnotification = mnotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item, viewGroup,false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Notification notification= mnotification.get(i);
        viewHolder.text.setText(notification.getText());

        getUserInfo(viewHolder.image_profile, viewHolder.username,notification.getUserid());

        if(notification.isIspost()){
            viewHolder.post_image.setVisibility(View.VISIBLE);
            getPostImage(viewHolder.post_image, notification.getPostid());

        }else{
            viewHolder.post_image.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isIspost()){
                    SharedPreferences.Editor editor =mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailragment()).commit();
                }else{

                    SharedPreferences.Editor editor =mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notification.getUserid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mnotification.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        public ImageView image_profile,post_image;

        public TextView username,  text;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            image_profile=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            username=itemView.findViewById(R.id.username);
            text=itemView.findViewById(R.id.comment);

        }



    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getPostImage(final ImageView imageView, final String postid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post=dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
