package com.example.markbooks.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markbooks.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> datas;

    public PostAdapter(List<Post> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post data = datas.get(position);

        holder.nickname.setText("작성자"+data.getNickname());
        holder.title.setText(data.getTitle());
        holder.timestamp.setText(data.getDate());

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        //private TextView author;
        private TextView nickname;
        private TextView timestamp;
        //private  TextView publish;
        ImageButton popup;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            nickname = itemView.findViewById(R.id.item_post_nickname);
            title = itemView.findViewById(R.id.item_post_title);
            timestamp = itemView.findViewById(R.id.texttime);

            //author = itemView.findViewById(R.id.item_post_author);
            //publish = itemView.findViewById(R.id.item_post_publish);
            popup= itemView.findViewById(R.id.popup_btn);
        }


    }
}
