package com.example.test_922;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.MockView;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemClickListner implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener listener;
    private GestureDetector gestureDetector;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view, int position);
    }

    public RecyclerViewItemClickListner(Context context, final RecyclerView recyclerView, final OnItemClickListener listener){
        this.listener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View v = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (v != null && listener != null) {
                    listener.onItemLongClick(v, recyclerView.getChildAdapterPosition(v));
                }
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e){
        View view = rv.findChildViewUnder(e.getX(),e.getY());
        if (view != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(view,rv.getChildAdapterPosition(view));
            return true;
        }
        return false;
    }
    @Override
    public void onTouchEvent(@NonNull RecyclerView rv , @NonNull MotionEvent e){

    }
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){

    }
}
