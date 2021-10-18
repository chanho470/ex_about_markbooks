package com.example.markbooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> implements OnBookItemClickListener {

    private Context context;
    private ArrayList<Book> items = new ArrayList<>();
    OnBookItemClickListener listener;

    public ResultAdapter(Context context, ArrayList<Book> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Book item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.author.setText(item.getAuthor());
        holder.genre.setText(item.getGenre());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClicklistener(OnBookItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(MyViewHolder holder, View view, int position) {
        if(listener != null){
            listener.onItemClick(holder,view,position);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;
        TextView genre;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(ResultAdapter.MyViewHolder.this, v, position);
                    }
                }
            });


            title = itemView.findViewById(R.id.item_title);
            author = itemView.findViewById(R.id.item_author);
            genre = itemView.findViewById(R.id.item_genre);
        }
    }
}
