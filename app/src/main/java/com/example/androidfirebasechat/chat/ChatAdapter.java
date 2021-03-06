package com.example.androidfirebasechat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfirebasechat.MemoryData;
import com.example.androidfirebasechat.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatList> chatList;
    private final Context context;
    private String userMobile;


    public ChatAdapter(List<ChatList> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        this.userMobile = MemoryData.getData(context);
    }


    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ChatList list2 = chatList.get(position);

        if (list2.getMobile().equals(userMobile)){
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppoLayout.setVisibility(View.GONE);

            holder.myMsg.setText(list2.getMessage());
            holder.myTime.setText(list2.getTime());
        }else {
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.VISIBLE);

            holder.oppoMsg.setText(list2.getMessage());
            holder.oppoTime.setText(list2.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChatList(List<ChatList> chatLists){
        this.chatList = chatLists;
    }
    static class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout oppoLayout, myLayout;
        private TextView oppoMsg, myMsg, oppoTime, myTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oppoLayout = itemView.findViewById(R.id.oppoLayout);
            myLayout = itemView.findViewById(R.id.myLayout);
            oppoMsg = itemView.findViewById(R.id.oppoMessage);
            myMsg = itemView.findViewById(R.id.myMessage);
            oppoTime = itemView.findViewById(R.id.oppoMsgTime);
            myTime = itemView.findViewById(R.id.myMsgTime);

        }
    }
}
