package com.company.econatia.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.company.econatia.Activity_Ads;
import com.company.econatia.Adapter.RewardsAdapter;
import com.company.econatia.Model.Post;
import com.company.econatia.Model.Product;
import com.company.econatia.Model.Rewards;
import com.company.econatia.OptionsAcitivity;
import com.company.econatia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RewardsFragment extends Fragment {
    TextView posts;
    FirebaseUser firebaseUser;
    String profileid;
    int econs;
    RecyclerView recyclerView;
    private List<Product> productList;
    private Button mbutton;
    private RewardsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_rewards, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid" , "none");
        recyclerView = view.findViewById(R.id.econs_redeem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new RewardsAdapter(getContext() , productList);
        recyclerView.setAdapter(adapter);
        posts = view.findViewById(R.id.posts);
        mbutton=view.findViewById(R.id.get_rewards);
       /* String prodcutName = "Sony PS4 500 GB Slim Console";
        String prodcutDesc = "The ultimate home entertainment centre starts with PlayStation. Whether you’re into gaming, HD movies, " +
                "television, music or all of the above, PlayStation offers something great for everyone.\n" +
                "\n" +
                "Vibrant HDR Gaming\n" +
                "HDR-enabled PS4 games burst into life with incredible colour and clarity on an HDR TV, d" +
                "elivering a more vibrant, realistic spectrum of colours."
                ;
        String prodcutCost = "60000 Econs";
        String productUrl = "https://firebasestorage.googleapis.com/v0/b/insta-7eb98.appspot.com/o/products%2Fplaystation4.jpg?alt=media&token=3deee161-72b2-470a-acb7-40e44bb5aa80";

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Products");

        HashMap<String , Object> hashMap = new HashMap<>();

        String id = firebaseDatabase.push().getKey();

        Product product = new Product(prodcutName , prodcutDesc , prodcutCost , productUrl , id);
        firebaseDatabase.child(id).setValue(product);*/

        getNrPosts();

         mbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getContext() , Activity_Ads.class);
                 ContextCompat.startForegroundService(getContext() , intent);
                 startActivity(intent);
             }
         });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getNrPosts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rewards").child(firebaseUser.getUid());

        /*Rewards rewards1 = new Rewards(0);
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Rewards").child("12ryxQ5K0gfZmqXw0RR5zTYTCI32");
        reference1.setValue(rewards1);
        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference("Rewards").child("2iUbQCsRZLUxEo7N2GFHSaAHDDu1");
        reference2.setValue(rewards1);
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Rewards").child("4NliYFjkB6YtN3865rfkVlgjcxe2");
        reference3.setValue(rewards1);
        DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("Rewards").child("7DEqbbNtRxOs2L67zogyPjO4EMS2");
        reference4.setValue(rewards1);
        DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference("Rewards").child("9m2xkIxFD6OjySTYTbNdCLGIwNT2");
        reference5.setValue(rewards1);
        DatabaseReference reference6 = FirebaseDatabase.getInstance().getReference("Rewards").child("FiMXFDt2oser98dCLpmzHMgzR1m2");
        reference6.setValue(rewards1);
        DatabaseReference reference7 = FirebaseDatabase.getInstance().getReference("Rewards").child("Gsdm3AjymlQk0YSNxN7hzoOvKbf2");
        reference7.setValue(rewards1);
        DatabaseReference reference8 = FirebaseDatabase.getInstance().getReference("Rewards").child("IdIWlMHoGdbqk1WaLTZtJwj5mjP2");
        reference8.setValue(rewards1);
        DatabaseReference reference9 = FirebaseDatabase.getInstance().getReference("Rewards").child("JMTnghBeCRbF3oQe44t2WIYZM2D3");
        reference9.setValue(rewards1);
        DatabaseReference reference10 = FirebaseDatabase.getInstance().getReference("Rewards").child("KvHErfusnoRQcquj9EHyfqafHRq1");
        reference10.setValue(rewards1);
        DatabaseReference reference11 = FirebaseDatabase.getInstance().getReference("Rewards").child("VgEnPgzZ7KXTNRg8fReB6jvDXkB2");
        reference11.setValue(rewards1);
        DatabaseReference reference12 = FirebaseDatabase.getInstance().getReference("Rewards").child("WpTvoiVyGOYxbmELz52kU3pbT3y1");
        reference12.setValue(rewards1);
        DatabaseReference reference13 = FirebaseDatabase.getInstance().getReference("Rewards").child("bBMW6g7fzlQVur7XVwhtjqPq0tq2");
        reference13.setValue(rewards1);
        DatabaseReference reference14 = FirebaseDatabase.getInstance().getReference("Rewards").child("dWwBTZGvebPanYjnxRnGqbSelsa2");
        reference14.setValue(rewards1);
        DatabaseReference reference15 = FirebaseDatabase.getInstance().getReference("Rewards").child("dhcRsIMw5VVaHvbzwUfwVFuvZUl1");
        reference15.setValue(rewards1);
        DatabaseReference reference16 = FirebaseDatabase.getInstance().getReference("Rewards").child("exvb5DApndf8vVqoUUo5k7LnFmz1");
        reference16.setValue(rewards1);
        DatabaseReference reference17 = FirebaseDatabase.getInstance().getReference("Rewards").child("fEhJ2MesYMbjQKLFySJrISCJ3kB2");
        reference17.setValue(rewards1);
        DatabaseReference reference18 = FirebaseDatabase.getInstance().getReference("Rewards").child("g1dqYbzJxxRTuVdVeOvHzX200E13");
        reference18.setValue(rewards1);
        DatabaseReference reference19 = FirebaseDatabase.getInstance().getReference("Rewards").child("h9JmnmpUPjdawgxA30PUR53jGe42");
        reference19.setValue(rewards1);
        DatabaseReference reference20 = FirebaseDatabase.getInstance().getReference("Rewards").child("jOyjphbNnZQFwajG0HlqfhP44jx2");
        reference20.setValue(rewards1);
        DatabaseReference reference21 = FirebaseDatabase.getInstance().getReference("Rewards").child("m0Tg8O6nnrUhnWGch7UWXyvauWy1");
        reference21.setValue(rewards1);
        DatabaseReference reference22 = FirebaseDatabase.getInstance().getReference("Rewards").child("nTwrPbfJGxWgq3mpkehFRbY6cug2");
        reference22.setValue(rewards1);
        DatabaseReference reference23 = FirebaseDatabase.getInstance().getReference("Rewards").child("ntvGVrd0VPd20NZlVvoJhu8DjAz1");
        reference23.setValue(rewards1);
        DatabaseReference reference24 = FirebaseDatabase.getInstance().getReference("Rewards").child("rB6VRlZ7X8N2rbx2Bssm4YsGYqk1");
        reference24.setValue(rewards1);
        DatabaseReference reference25 = FirebaseDatabase.getInstance().getReference("Rewards").child("xz6Zn0HHipXZzJs0Tg4tS2LInFu2");
        reference25.setValue(rewards1);
        DatabaseReference reference26 = FirebaseDatabase.getInstance().getReference("Rewards").child("yXwnYXn54WhhqIXBXmh36GQSdtY2");
        reference26.setValue(rewards1);
        DatabaseReference reference27 = FirebaseDatabase.getInstance().getReference("Rewards").child("yllsiSzhVpPlYB0IW28bjmcli4S2");
        reference27.setValue(rewards1);*/

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
