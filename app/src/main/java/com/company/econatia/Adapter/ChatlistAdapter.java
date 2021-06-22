package com.company.econatia.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.company.econatia.ChatActivity;
import com.company.econatia.Model.User;
import com.company.econatia.R;

import java.util.HashMap;
import java.util.List;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.MyHolder> {

    Context context;
    List<User> userList;
    private HashMap<String, String> lastMessageMap;
    private HashMap<String, Boolean> isSeenMap;

    public ChatlistAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
        isSeenMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        String hisUid = userList.get(i).getId();
        String userImage = userList.get(i).getImageurl();
        String userName = userList.get(i).getFullname();
        String lastMessage = lastMessageMap.get(hisUid);
        //boolean isSeen = isSeenMap.get(hisUid);

        myHolder.nameTv.setText(userName);
        if(lastMessage == null || lastMessage.equals("default")){
            myHolder.lastMessageTv.setVisibility(View.INVISIBLE);
        }
        else{
            myHolder.lastMessageTv.setVisibility(View.VISIBLE);
            myHolder.lastMessageTv.setText(lastMessage);
        }

        /*if(isSeen == false){
            myHolder.notSeen.setVisibility(View.VISIBLE);
            myHolder.lastMessageTv.setTypeface(Typeface.DEFAULT_BOLD);
        }*/

        try{
            Glide.with(context).load(userImage).into(myHolder.profileIv);
        }
        catch (Exception e){
            Glide.with(context).load(R.drawable.ic_user).into(myHolder.profileIv);
        }

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });

    }

    public void setIsSeenMap(String userId, boolean isSeen){
        isSeenMap.put(userId, isSeen);
    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView profileIv, notSeen;
        TextView nameTv, lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            notSeen = itemView.findViewById(R.id.notSeen);

        }
    }

}
