package com.example.deepanshu.todoapp;

import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static int EDIT_TODO = 1;
    final static int ADD_TODO = 0;
    public static Date todayDate;
    public static Date tomDate;
    Todo currentTodo = null;

    RecyclerView mTodayRecyclerView;
    RecyclerView mUpComingRecyclerView;
    RecyclerView mDoneRecyclerView;

    List<Todo> todoList;
    ArrayList<Todo> todayTodoArrayList;
    ArrayList<Todo> upcomingTodoArrayList;
    ArrayList<Todo> doneTodoArrayList;

    RecyclerAdapter mTodayRecyclerAdapter;
    RecyclerAdapter mUpcomingRecyclerAdapter;
    RecyclerAdapter mDoneRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TodoDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE,ADD_TODO);
                startActivityForResult(intent,ADD_TODO);
            }
        });

        mTodayRecyclerView = (RecyclerView) findViewById(R.id.today_recycler_view);
        mUpComingRecyclerView = (RecyclerView) findViewById(R.id.upcoming_recycler_view);
        mDoneRecyclerView = (RecyclerView) findViewById(R.id.done_recycler_view);

        todoList = new ArrayList<>();
        todayTodoArrayList = new ArrayList<>();
        upcomingTodoArrayList = new ArrayList<>();
        doneTodoArrayList = new ArrayList<>();

        mTodayRecyclerAdapter = new RecyclerAdapter(this, todayTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDailog(todayTodoArrayList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                todayTodoArrayList.remove(position);
                mTodayRecyclerAdapter.notifyItemRemoved(position);

            }
        });
        mUpcomingRecyclerAdapter = new RecyclerAdapter(this, upcomingTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDailog(upcomingTodoArrayList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                upcomingTodoArrayList.remove(position);
                mUpcomingRecyclerAdapter.notifyItemRemoved(position);
            }
        });
        mDoneRecyclerAdapter = new RecyclerAdapter(this, doneTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                doneTodoArrayList.get(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                doneTodoArrayList.remove(position);
                mDoneRecyclerAdapter.notifyItemRemoved(position);
            }
        });

        mTodayRecyclerView.setAdapter(mTodayRecyclerAdapter);
        mUpComingRecyclerView.setAdapter(mUpcomingRecyclerAdapter);
        mDoneRecyclerView.setAdapter(mDoneRecyclerAdapter);

        mTodayRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mUpComingRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mDoneRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        mTodayRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mUpComingRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mDoneRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        mTodayRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUpComingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDoneRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper todayItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP, ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                todayTodoArrayList.remove(position);
                mTodayRecyclerAdapter.notifyItemRemoved(position);
            }
        });

        ItemTouchHelper upcomingItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP, ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                upcomingTodoArrayList.remove(position);
                mUpcomingRecyclerAdapter.notifyItemRemoved(position);
            }
        });

        ItemTouchHelper doneItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP, ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                doneTodoArrayList.remove(position);
                mDoneRecyclerAdapter.notifyItemRemoved(position);
            }
        });

        todayItemTouchHelper.attachToRecyclerView(mTodayRecyclerView);
        upcomingItemTouchHelper.attachToRecyclerView(mUpComingRecyclerView);
        doneItemTouchHelper.attachToRecyclerView(mDoneRecyclerView);

        updateLists();
    }

    private void showDailog(Todo todo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(todo.getTodoName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_CANCELED){
            return;
        }
        else{
            updateLists();
        }
    }

    private void updateLists() {
        setTodayDate();
        setTomDate();
        todoList.clear();
        todayTodoArrayList.clear();
        upcomingTodoArrayList.clear();
        doneTodoArrayList.clear();

        TodoDatabase database = TodoDatabase.getInstance(this);
        final TodoDao dao = database.todoDao();
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                todoList = dao.getAllTodo();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                for(int i=0;i<todoList.size();i++){
                    Todo currentTodo = todoList.get(i);
                    Date currentTodoDate = currentTodo.getTodoDate();
                    setAlarm(currentTodo);

                    if(currentTodoDate.before(todayDate)){

                        int size = upcomingTodoArrayList.size();
                        doneTodoArrayList.add(currentTodo);
                        mDoneRecyclerAdapter.notifyItemInserted(size);

                    }else if(currentTodoDate.after(tomDate)){

                        int size = upcomingTodoArrayList.size();
                        upcomingTodoArrayList.add(currentTodo);
                        mUpcomingRecyclerAdapter.notifyItemInserted(size);

                    }else{
                        int size = todayTodoArrayList.size();
                        todayTodoArrayList.add(currentTodo);
                        mTodayRecyclerAdapter.notifyItemInserted(size);
                    }
                }
            }
        }.execute();
    }

    private void setAlarm(Todo currentTodo) {
        //TODO
    }

    private void setTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);

        calendar.set(year,month,date,0,0,0);

        todayDate = calendar.getTime();
    }

    private void setTomDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);

        calendar.set(year,month,date+1,0,0,0);

        tomDate = calendar.getTime();

    }

}