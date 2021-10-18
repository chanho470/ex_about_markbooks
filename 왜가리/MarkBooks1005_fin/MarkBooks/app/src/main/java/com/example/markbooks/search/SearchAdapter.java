package com.example.markbooks.search;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.markbooks.R;
import com.example.markbooks.common.ResultActivity;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> implements Filterable, OnKeywordClickListener {

    Context context;
    ArrayList<String> unFilteredlist;
    ArrayList<String> filteredList;
    ArrayList<String> records;
    OnKeywordClickListener listener;


    public SearchAdapter(Context context, ArrayList<String> list, ArrayList<String> records) {
        super();
        this.context = context;
        this.unFilteredlist = list;
        this.filteredList = list;
        this.records = records;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new MyViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.search.setText(filteredList.get(position));
    }

    public void setOnItemClicklistener(OnKeywordClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(MyViewHolder holder, View view, int position) {
        if(listener != null){
            listener.onItemClick(holder,view,position);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView search;

        public MyViewHolder(View itemView, final OnKeywordClickListener listener) {
            super(itemView);
            search = (TextView)itemView.findViewById(R.id.item_search);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        Intent intent = new Intent(context, ResultActivity.class);
                        intent.putExtra("검색", filteredList.get(position));
                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty() || charString == null) {
                    filteredList = records;
                } else {
                    ArrayList<String> filteringList = new ArrayList<>();
                    for(String name : unFilteredlist) {
                        if(name.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(name);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<String>)results.values;
                notifyDataSetChanged();
            }
        };
    }
}

