package com.company.econatia;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.company.econatia.Model.Rewards;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PostVideoActivity extends AppCompatActivity {

    StorageTask uploadTask;
    StorageReference storageReference;
    FirebaseAuth auth;

    ImageView close ;
    int econs;
    VideoView video_added;
    TextView post;
    EditText description;
    String myUrl = "";

    private Uri videoUri;
    private MediaController mc;
    private static final int PICK_VIDEO_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        close = findViewById(R.id.close);
        video_added = findViewById(R.id.video_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference("posts");
        auth = FirebaseAuth.getInstance();

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent , "Select a video") , PICK_VIDEO_REQUEST);

        mc = new MediaController(this);
        video_added.setMediaController(mc);
        mc.setAnchorView(video_added);
        video_added.start();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostVideoActivity.this , MainActivity.class);
                ContextCompat.startForegroundService(PostVideoActivity.this , intent);
                startActivity(intent);
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();
            }
        });


    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadVideo(){

        final ProgressDialog progressDialog = new ProgressDialog(PostVideoActivity.this);
        progressDialog.setMessage("Posting..");
        progressDialog.show();

        if(videoUri != null){
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(videoUri));

            uploadTask = filereference.putFile(videoUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl= downloadUri.toString();

                        Date todayDate = Calendar.getInstance().getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        String date = formatter.format(todayDate);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();

                        HashMap<String,Object> hashMap = new HashMap<>();

                        hashMap.put("postid", postid);
                        hashMap.put("type", "video");
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("date", date);

                        reference.child(postid).setValue(hashMap);

                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        Toast.makeText(getApplicationContext(),"You received 1 Econ",Toast.LENGTH_SHORT).show();
                        final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Rewards").child(firebaseUser.getUid());
                        reference2.addListenerForSingleValueEvent(new ValueEventListener()  {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Rewards rewards = dataSnapshot.getValue(Rewards.class);
                                econs = rewards.getEcons();

                                Rewards rewards1 = new Rewards(econs + 1);
                                reference2.setValue(rewards1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        progressDialog.dismiss();

                        Intent intent = new Intent(PostVideoActivity.this , MainActivity.class);
                        ContextCompat.startForegroundService(PostVideoActivity.this , intent);
                        startActivity(intent);

                        finish();
                    }else{
                        Toast.makeText(PostVideoActivity.this , "Failed to post" , Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            Toast.makeText(PostVideoActivity.this , "No video found" , Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK  && data != null){

            videoUri = data.getData();
            /*String[] filePathColumn = { MediaStore.Video.Media.DATA };

            Cursor cursor = getContentResolver().query(videoUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();*/

            try {
                if (videoUri != null) {

                    MediaPlayer mp = MediaPlayer.create(this, videoUri);
                    int duration = mp.getDuration();
                    mp.release();

                    if((duration/1000) > 30){
                        Toast.makeText(PostVideoActivity.this , "Please upload video of duration less than 30 seconds" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PostVideoActivity.this , PostVideoActivity.class);
                        ContextCompat.startForegroundService(PostVideoActivity.this , intent);
                        startActivity(intent);
                    }else{
                        video_added.setVideoURI(videoUri);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
