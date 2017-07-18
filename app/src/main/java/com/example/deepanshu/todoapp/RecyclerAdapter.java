package com.example.deepanshu.todoapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static View mItemView;

    public RecyclerAdapter(Context mContext, ArrayList<Todo> mTodoArrayList, ItemClickListener listener) {
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
        holder.mTodoCategoryTextView.setText(todo.getTodoCategory().getCategory());
        holder.mTodoCardView.setBackgroundColor(setItemBgColor(todo.getTodoPriority()));

    }

    private int setItemBgColor(int todoPriority) {
        switch (todoPriority){
            case 0: return (ContextCompat.getColor(mContext,R.color.priority0));
            case 1: return (ContextCompat.getColor(mContext,R.color.priority1));
            case 2: return (ContextCompat.getColor(mContext,R.color.priority2));
            case 3: return (ContextCompat.getColor(mContext,R.color.priority3));
            case 4: return (ContextCompat.getColor(mContext,R.color.priority4));
            case 5: return (ContextCompat.getColor(mContext,R.color.priority5));
            default: return (ContextCompat.getColor(mContext,R.color.priority0));
        }
    }

    @Override
    public int getItemCount() {
        return mTodoArrayList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        LinearLayout layout;
        TextView mTodoIconTextView;
        TextView mTodoNameTextView;
        TextView mTodoCategoryTextView;
        ItemClickListener itemClickListener;
        CardView mTodoCardView;

        public TodoViewHolder(View itemView,ItemClickListener itemClickListener) {
            super(itemView);
            RecyclerAdapter.mItemView = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.itemClickListener = itemClickListener;

            layout = (LinearLayout) itemView.findViewById(R.id.todo_layout);
            layout.getLayoutParams().width = (int) (Utils.getScreenWidth(itemView.getContext()) / 3.15);
            mTodoIconTextView = (TextView) itemView.findViewById(R.id.todo_icon_textview);
            mTodoNameTextView = (TextView) itemView.findViewById(R.id.todo_name_textview);
            mTodoCategoryTextView = (TextView) itemView.findViewById(R.id.todo_category_textview);
            mTodoCardView = (CardView) itemView.findViewById(R.id.todo_item_card);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();

            if(position!=RecyclerView.NO_POSITION){

                if(id == R.id.todo_layout){
                    itemClickListener.onItemClick(view,position);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int id = v.getId();
            int position = getAdapterPosition();

            if(position!=RecyclerView.NO_POSITION){

                if(id == R.id.todo_layout){
                    itemClickListener.onItemLongClick(v,position);
                }
            }
            return true;
        }
    }
}

interface ItemClickListener{
    void onItemClick(View view,int position);
    void onItemLongClick(View view,int position);
}
