package com.company.econatia.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.econatia.BuildConfig;
import com.company.econatia.CommentsActivity;
import com.company.econatia.FollowersActivity;
import com.company.econatia.Fragment.PostDetailragment;
import com.company.econatia.Fragment.ProfileFragment;
import com.company.econatia.Model.Notification;
import com.company.econatia.Model.Post;
import com.company.econatia.Model.User;
import com.company.econatia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context mContext;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item , viewGroup , false);
        return new PostAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(i);

        //apply(new RequestOptions().placeholder(R.drawable.grey_bg))

        if(post.getType().equals("image")){
            viewHolder.post_image.setVisibility(View.VISIBLE);
            viewHolder.post_video.setVisibility(View.GONE);
            Glide.with(mContext).load(post.getPostimage())
                    .apply(new RequestOptions().override(1000 , 1000))
                    .into(viewHolder.post_image);

        }else{

            viewHolder.post_image.setVisibility(View.GONE);
            viewHolder.post_video.setVisibility(View.VISIBLE);
            viewHolder.post_video.setVideoURI(Uri.parse(post.getPostimage()));
            viewHolder.post_video.seekTo(200);

            final GestureDetector mGestureDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if(viewHolder.post_video.isPlaying())
                        viewHolder.post_video.pause();
                    else
                        viewHolder.post_video.start();
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });

            /*viewHolder.post_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            MediaController mc = new MediaController(mContext);
                            viewHolder.post_video.setMediaController(mc);
                            mc.setAnchorView(viewHolder.post_video);
                        }
                    });
                }
            });*/

            viewHolder.post_video.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            //viewHolder.post_video.start();
            //viewHolder.post_video.setVideoURI(Uri.parse(post.getPostimage()));

        }



        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String date_today = formatter.format(todayDate);
        String post_uploaded = post.getDate();

        Date date_today1;
        Date date_uploaded;

        String dayDifference = null;

        try {
            date_today1 = formatter.parse(date_today);
            date_uploaded = formatter.parse(post_uploaded);

            long difference = Math.abs(date_today1.getTime() - date_uploaded.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);

            //Convert long to String
            dayDifference = Long.toString(differenceDates);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dayDifference.equals("0")){
            viewHolder.date.setText("Uploaded Today");
        }else if(dayDifference.equals("1")){
            viewHolder.date.setText("Uploaded 1 day ago");
        }else if(dayDifference.equals("2")){
            viewHolder.date.setText("Uploaded 2 days ago");
        }else if(dayDifference.equals("3")){
            viewHolder.date.setText("Uploaded 3 days ago");
        }else{
            viewHolder.date.setText("Uploaded:"+post_uploaded);
        }


        if(post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);
        }else{
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile , viewHolder.username , viewHolder.publisher , post.getPublisher());
        isLiked(post.getPostid(),viewHolder.like);
        nrLikes(viewHolder.likes,post.getPostid());
        getComments(post.getPostid(),viewHolder.comments);

        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        viewHolder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailragment()).commit();
            }
        });

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPublisher(),post.getPostid());

                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CommentsActivity.class);
                ContextCompat.startForegroundService(mContext,intent);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(intent);
            }
        });
        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CommentsActivity.class);
                ContextCompat.startForegroundService(mContext,intent);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        viewHolder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);
            }
        });

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.getType().equals("image")) {
                    shareItem(post.getPostimage(), post.getDescription());
                }
                else{
                    String description = post.getDescription();
                    String download = " " +
                            "Download Econatia https://play.google.com/store/apps/details?id=com.company.econatia";
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.putExtra(Intent.EXTRA_TEXT , description + download);
                    mContext.startActivity(Intent.createChooser(i, "Share Image"));
                }
            }
        });

        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext , v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                editPost(post.getPostid());
                                return true;


                            case R.id.delete:
                                Query query = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid())
                                        .orderByChild("postid").equalTo(post.getPostid());
                                query.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        dataSnapshot.getRef().setValue(null);
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid()).removeValue();
                                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                                editor.putString("profileid",firebaseUser.getUid());
                                editor.apply();

                                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new ProfileFragment()).commit();

                                return true;

                            case R.id.report:
                                Toast.makeText(mContext , "Reported" , Toast.LENGTH_SHORT).show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if(!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image , like , comment , more ,share;
        public TextView username , likes , comments , publisher , description , date;
        public VideoView post_video;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.more);
            share = itemView.findViewById(R.id.share);
            date = itemView.findViewById(R.id.date);
            post_video = itemView.findViewById(R.id.post_video);

        }
    }

    private void getComments(String postid, final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View "+dataSnapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void isLiked(String postid, final ImageView imageView){

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked1);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like1);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addNotifications(String userid , String postid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);

        reference.push().setValue(hashMap);
    }

    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void publisherInfo(final ImageView image_profile , final TextView username , final TextView publisher , String useid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(useid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void editPost(final String postid){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid , editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("description" , editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts").child(postid).updateChildren(hashMap);

                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    private void getText(String postid , final EditText editText){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void shareItem(String url , final String description) {
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                String download = " " +
                        "Download Econatia https://play.google.com/store/apps/details?id=com.company.econatia";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.setType("image/*");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.putExtra(Intent.EXTRA_TEXT , description + download);
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                mContext.startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(mContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}


