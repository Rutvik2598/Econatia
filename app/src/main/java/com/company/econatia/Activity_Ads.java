package com.company.econatia;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.econatia.Model.Post;
import com.company.econatia.Model.Rewards;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Activity_Ads extends AppCompatActivity implements RewardedVideoAdListener {
Button adbutton;
TextView posts;
    int econs;
RewardedVideoAd mAd;
    String profileid;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = this.getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid" , "none");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__ads);

        AdView adView=(AdView)findViewById(R.id.adView);
        AdRequest adRequest= new AdRequest.Builder().build();

        adView.loadAd(adRequest);





        adbutton=findViewById(R.id.adreward);
        posts = findViewById(R.id.posts);
        adbutton.setEnabled(false);

        MobileAds.initialize(this,"ca-app-pub-5719688149111472~1558005042");
        mAd=MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);

        mAd.loadAd("ca-app-pub-5719688149111472/3538506402", new AdRequest.Builder().build());


        adbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adbutton.setEnabled(false);
                if(mAd.isLoaded()){
                    mAd.show();
                }

            }
        });

        getNrPosts();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        adbutton.setEnabled(true);

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(getApplicationContext(),"You received 1 Econ",Toast.LENGTH_SHORT).show();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rewards").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Rewards rewards = dataSnapshot.getValue(Rewards.class);
                econs = rewards.getEcons();

                Rewards rewards1 = new Rewards(econs + 1);
                reference.setValue(rewards1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

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
}
