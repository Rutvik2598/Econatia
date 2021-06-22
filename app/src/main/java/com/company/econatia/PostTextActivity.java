package com.company.econatia;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.econatia.Model.Rewards;
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

public class PostTextActivity extends AppCompatActivity {

    EditText description;
    int econs;
    StorageTask uploadTask;
    StorageReference storageReference;
    FirebaseAuth auth;
    ImageView close;
    TextView post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_text);

        close = findViewById(R.id.close);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference("posts");
        auth = FirebaseAuth.getInstance();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostTextActivity.this , MainActivity.class);
                ContextCompat.startForegroundService(PostTextActivity.this , intent);
                startActivity(intent);
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadText();
            }
        });

    }

    private void uploadText(){

        final ProgressDialog progressDialog = new ProgressDialog(PostTextActivity.this);
        progressDialog.setMessage("Posting..");
        progressDialog.show();

        if(description.getText().toString() != null){

            Date todayDate = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String date = formatter.format(todayDate);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

            String postid = reference.push().getKey();

            HashMap<String,Object> hashMap = new HashMap<>();

            hashMap.put("postid", postid);
            hashMap.put("type", "text");
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

            Intent intent = new Intent(PostTextActivity.this , MainActivity.class);
            ContextCompat.startForegroundService(PostTextActivity.this , intent);
            startActivity(intent);

            finish();
        }else{
            Toast.makeText(PostTextActivity.this , "Cannot post empty text" , Toast.LENGTH_SHORT).show();
        }


    }
}