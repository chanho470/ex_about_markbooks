package com.example.markbooks.highlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.markbooks.R;
import com.example.markbooks.common.Book;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.MyViewHolder> {

    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;


    Context context;
    ArrayList<Book> items = new ArrayList<Book>();
    HighlightAdapter.OnItemClickListener listener;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userID = fAuth.getCurrentUser().getUid();
    Activity activity;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();

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
        TextView texthighlight;
        TextView time;
        ImageButton delete;
        ImageView img;
        CardView cardView;

        HighlightAdapter.OnItemClickListener listener;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_hl_title);
            texthighlight = itemView.findViewById(R.id.texthighlight);
            time = itemView.findViewById(R.id.texttime);
            delete = itemView.findViewById(R.id.delete_btn);
            img = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.item_cardview);

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
            texthighlight.setText(item.getHighlight());
            storageRef.child("Book img/" + item.getTitle() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context.getApplicationContext())
                            .load(uri)
                            .into(img);
                }
            });
            System.out.println("시간   : "+item.getTime());
            time.setText(item.getTime());
        }





        //클릭이벤트처리
        public void setOnItemClickListener(HighlightAdapter.OnItemClickListener listener){
            this.listener = listener;
        }

    }

}
