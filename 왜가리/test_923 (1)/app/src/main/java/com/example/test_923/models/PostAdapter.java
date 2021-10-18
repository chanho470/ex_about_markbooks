package com.example.test_923.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_923.R;

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

        //holder.nicname.setText("작성자"+data.getNicname());
        holder.title.setText("책 제목 :"+data.getTitle());

        //holder.author.setText("작가 :"+data.getAuthor());
        //holder.publish.setText("출판사 :"+data.getPublish());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView time;
        private TextView author;
        private TextView nicname;
        private  TextView publish;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            //nicname = itemView.findViewById(R.id.item_post_nicname);
            title = itemView.findViewById(R.id.item_post_title);
            time = itemView.findViewById(R.id.item_post_time);
            //author = itemView.findViewById(R.id.item_post_author);
            //publish = itemView.findViewById(R.id.item_post_publish);
        }
    }
}
