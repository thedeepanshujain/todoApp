package com.example.deepanshu.todoapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by deepanshu on 14/7/17.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TodoViewHolder> {
    private Context mContext;
    private ArrayList<Todo> mTodoArrayList;
    private ItemClickListener mListener;

    public RecyclerAdapter(Context mContext, ArrayList<Todo> mTodoArrayList,ItemClickListener listener) {
        this.mContext = mContext;
        this.mTodoArrayList = mTodoArrayList;
        this.mListener = listener;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_todo_item,parent,false);
        return new TodoViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {

        Todo todo = mTodoArrayList.get(position);
        holder.mTodoIconTextView.setText(todo.getIcon());
        holder.mTodoNameTextView.setText(todo.getTodoName());
        holder.mTodoCategoryTextView.setText(todo.getTodoCategory());
    }

    @Override
    public int getItemCount() {
        return mTodoArrayList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout layout;
        TextView mTodoIconTextView;
        TextView mTodoNameTextView;
        TextView mTodoCategoryTextView;
        ItemClickListener itemClickListener;

        public TodoViewHolder(View itemView,ItemClickListener itemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.itemClickListener = itemClickListener;

            layout = (LinearLayout) itemView.findViewById(R.id.todo_layout);
            layout.getLayoutParams().width = (int) (Utils.getScreenWidth(itemView.getContext()) / 3.15);
            mTodoIconTextView = (TextView) itemView.findViewById(R.id.todo_icon_textview);
            mTodoNameTextView = (TextView) itemView.findViewById(R.id.todo_name_textview);
            mTodoCategoryTextView = (TextView) itemView.findViewById(R.id.todo_category_textview);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

            if(position!=RecyclerView.NO_POSITION){

                if(id == R.id.todo_layout){
                    itemClickListener.onItemClick(view,position);
                    itemClickListener.onItemLongClick(view,position);
                }
            }
        }
    }
}

interface ItemClickListener{
    void onItemClick(View view,int position);
    void onItemLongClick(View view,int position);
}
