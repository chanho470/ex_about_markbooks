package com.example.markbooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyBookAdapter extends RecyclerView.Adapter<MyBookAdapter.MyViewHolder> {

    Context context;
    ArrayList<Book> items = new ArrayList<Book>();
    OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(MyViewHolder holder, View view, int position);
    }

    public MyBookAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_mybook, viewGroup, false);
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
        //ImageView bookImage;

        OnItemClickListener listener;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.mybook_item_title);
            author = itemView.findViewById(R.id.mybook_item_author);
            genre = itemView.findViewById(R.id.mybook_item_genre);
            //bookImage = itemView.findViewById(R.id.bookImage);

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
            //bookImage.setImageURI(Uri.parse(item.getImg().toString()));
        }

        //클릭이벤트처리
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
