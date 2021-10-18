package com.example.markbooks;

import android.view.View;

public interface OnBookItemClickListener {
    public void onItemClick(ResultAdapter.MyViewHolder holder, View view, int position);
}
