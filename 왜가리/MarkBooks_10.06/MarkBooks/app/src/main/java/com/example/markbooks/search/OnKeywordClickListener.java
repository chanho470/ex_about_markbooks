package com.example.markbooks.search;

import android.view.View;

import com.example.markbooks.search.SearchAdapter;

public interface OnKeywordClickListener {
    public void onItemClick(SearchAdapter.MyViewHolder holder, View view, int position);
}
