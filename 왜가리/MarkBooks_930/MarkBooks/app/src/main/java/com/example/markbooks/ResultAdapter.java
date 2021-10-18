package com.example.markbooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {

    Context context;
    ArrayList<Book> items = new ArrayList<Book>();
    OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(MyViewHolder holder, View view, int position);
    }

    public ResultAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_result, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Book item = items.get(position);
        holder.setItem(item);
        holder.setOnItemClickListener(listener);
    }

    public void addItems(ArrayList<Book> items){
        this.items = items;
    }

    public void addItem(Book item){
        items.add(item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public Book getItem(int position){
        return  items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;
        TextView genre;

        OnItemClickListener listener;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);
            author = itemView.findViewById(R.id.item_author);
            genre = itemView.findViewById(R.id.item_genre);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(MyViewHolder.this, itemView, position);
                    }
                }
            });
        }

        public void setItem(Book item) {
            title.setText(item.getTitle());
            author.setText(item.getAuthor());
            genre.setText(item.getGenre());
        }

        //클릭이벤트처리
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

    }

}
