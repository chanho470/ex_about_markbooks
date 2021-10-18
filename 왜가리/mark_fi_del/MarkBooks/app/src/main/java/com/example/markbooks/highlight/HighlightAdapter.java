package com.example.markbooks.highlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.markbooks.R;
import com.example.markbooks.common.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.MyViewHolder> {

    Context context;
    ArrayList<Book> items = new ArrayList<Book>();
    HighlightAdapter.OnItemClickListener listener;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    Activity activity;

    public interface OnItemClickListener{
        void onItemClick(HighlightAdapter.MyViewHolder holder, View view, int position);
    }

    public HighlightAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public HighlightAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_highlight, viewGroup, false);
        return new HighlightAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HighlightAdapter.MyViewHolder holder, int position) {
        Book item = items.get(position);
        holder.setItem(item);
        holder.setOnItemClickListener(listener);
        holder.delete.setTag(holder.getAdapterPosition());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("삭제 하시겠습니까?");
                dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int pos = (int) v.getTag();
                        items.remove(pos);
                        fStore.collection("user").document(userID).collection("highlight").document(item.getTitle()).delete();
                        Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(activity, "취소되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setTitle("삭제 알림");
                dialog.show();
            }
        });
    }

    public void addItems(ArrayList<Book> items){
        this.items = items;
    }

    public void addItem(Book item){
        items.add(item);
    }

    public void setOnItemClickListener(HighlightAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public Book getItem(int position){
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageButton delete;

        HighlightAdapter.OnItemClickListener listener;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_hl_title);
            delete = itemView.findViewById(R.id.delete_btn);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(HighlightAdapter.MyViewHolder.this, itemView, position);
                    }
                }
            });
        }

        public void setItem(Book item) {
            title.setText(item.getTitle());
        }

        //클릭이벤트처리
        public void setOnItemClickListener(HighlightAdapter.OnItemClickListener listener){
            this.listener = listener;
        }

    }
}
