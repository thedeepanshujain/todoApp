package com.example.deepanshu.todoapp;

import android.app.AlarmManager;
import android.arch.persistence.room.Dao;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static int EDIT_TODO = 1;
    public final static int ADD_TODO = 0;
    public static Date todayDate;
    public static Date tomDate;
    TodoDao todoDao;
    CategoryDao categoryDao;
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

        SharedPreferences appData = getSharedPreferences("todo_shared_preference",MODE_PRIVATE);
        boolean isFirstTime = appData.getBoolean("isFirstTime",true);

        if(isFirstTime){
//            SharedPreferences.Editor editor = appData.edit();
            appData.edit().putBoolean("isFirstTime",false).commit();
            Log.i("TAG", "onCreate: " +appData.getBoolean("isFirstTime",true) );

            TodoDatabase database = TodoDatabase.getInstance(this);
            categoryDao = database.categoryDao();
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_CHOOSE));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_PERSONAL));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_WORK));
                    categoryDao.newCat(new Category(DbConstants.CATEGORY_OTHERS));
                    // TODO: 16/7/17 custom category work
                    //dao.newCat("Custom");
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute();
        }

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
                showDialog(todayTodoArrayList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeTodayTodo(position);

            }
        });
        mUpcomingRecyclerAdapter = new RecyclerAdapter(this, upcomingTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDialog(upcomingTodoArrayList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeUpcomingTodo(position);
            }
        });
        mDoneRecyclerAdapter = new RecyclerAdapter(this, doneTodoArrayList, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDialog(doneTodoArrayList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                removeDoneTodo(position);
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

        ItemTouchHelper todayItemTouchHelper = new ItemTouchHelper(
               new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP,ItemTouchHelper.DOWN|ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.UP){
                    editTodayTodo(viewHolder.getAdapterPosition());
                }else if(direction == ItemTouchHelper.DOWN) {
                    removeTodayTodo(viewHolder.getAdapterPosition());
                }
            }
        });

        ItemTouchHelper upcomingItemTouchHelper = new ItemTouchHelper(
               new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP,ItemTouchHelper.DOWN|ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.UP){
                    editUpcomingTodo(viewHolder.getAdapterPosition());
                }else if(direction == ItemTouchHelper.DOWN) {
                    removeUpcomingTodo(viewHolder.getAdapterPosition());
                }
            }
        });

        ItemTouchHelper doneItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP,ItemTouchHelper.DOWN|ItemTouchHelper.UP){

            @Override
            public boolean onMove(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.UP){
                    editDoneTodo(viewHolder.getAdapterPosition());
                }else if(direction == ItemTouchHelper.DOWN) {
                    removeDoneTodo(viewHolder.getAdapterPosition());
                }
            }
        });

        todayItemTouchHelper.attachToRecyclerView(mTodayRecyclerView);
        upcomingItemTouchHelper.attachToRecyclerView(mUpComingRecyclerView);
        doneItemTouchHelper.attachToRecyclerView(mDoneRecyclerView);

        updateLists();
    }

    private void showDialog(final Todo todo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(todo.getTodoName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view,null);
        TextView dialogCat = (TextView) view.findViewById(R.id.dialog_category);
        TextView dialogDesc = (TextView) view.findViewById(R.id.dialog_desc);
        TextView dialogDate = (TextView) view.findViewById(R.id.dialog_date);
        TextView dialogTime = (TextView) view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = (AppCompatRatingBar) view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = (SwitchCompat) view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    "+todo.getTodoCategory().getCategory());
        dialogDesc.setText(todo.getTodoDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String dateString = dateFormat.format(todo.getTodoDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeString = timeFormat.format(new Date(todo.getTodoTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(todo.getTodoPriority());
        dialogSwitch.setChecked(todo.isTodoSetAlarm());

        builder.setView(view);

        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,TodoDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE,EDIT_TODO);
                intent.putExtra(IntentConstants.TODO,todo.getTodoId());
                startActivityForResult(intent,EDIT_TODO);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


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
        mTodayRecyclerAdapter.notifyDataSetChanged();
        upcomingTodoArrayList.clear();
        mUpcomingRecyclerAdapter.notifyDataSetChanged();
        doneTodoArrayList.clear();
        mDoneRecyclerAdapter.notifyDataSetChanged();

        TodoDatabase database = TodoDatabase.getInstance(this);
        todoDao = database.todoDao();
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                todoList = todoDao.getAllTodo();
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

    private void setAlarm(Todo todo) {
        //TODO alarms work
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


    private void removeDoneTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Remove TODO");
        builder.setCancelable(false);
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpcomingRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TodoDatabase database = TodoDatabase.getInstance(MainActivity.this);
                todoDao = database.todoDao();
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        Todo todo = doneTodoArrayList.get(position);
                        todoDao.deleteFromDb(todo);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        doneTodoArrayList.remove(position);
                        mDoneRecyclerAdapter.notifyItemRemoved(position);
                    }
                }.execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeUpcomingTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Remove TODO");
        builder.setCancelable(false);
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpcomingRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TodoDatabase database = TodoDatabase.getInstance(MainActivity.this);
                todoDao = database.todoDao();
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        Todo todo = upcomingTodoArrayList.get(position);
                        todoDao.deleteFromDb(todo);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        upcomingTodoArrayList.remove(position);
                        mUpcomingRecyclerAdapter.notifyItemRemoved(position);
                    }
                }.execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void removeTodayTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Remove TODO");
        builder.setCancelable(false);
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTodayRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TodoDatabase database = TodoDatabase.getInstance(MainActivity.this);
                todoDao = database.todoDao();
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        Todo todo = todayTodoArrayList.get(position);
                        todoDao.deleteFromDb(todo);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        todayTodoArrayList.remove(position);
                        mTodayRecyclerAdapter.notifyItemRemoved(position);
                    }
                }.execute();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void editTodayTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Todo todo = todayTodoArrayList.get(position);
        builder.setTitle(todo.getTodoName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view,null);
        TextView dialogCat = (TextView) view.findViewById(R.id.dialog_category);
        TextView dialogDesc = (TextView) view.findViewById(R.id.dialog_desc);
        TextView dialogDate = (TextView) view.findViewById(R.id.dialog_date);
        TextView dialogTime = (TextView) view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = (AppCompatRatingBar) view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = (SwitchCompat) view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    "+todo.getTodoCategory().getCategory());
        dialogDesc.setText(todo.getTodoDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String dateString = dateFormat.format(todo.getTodoDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeString = timeFormat.format(new Date(todo.getTodoTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(todo.getTodoPriority());
        dialogSwitch.setChecked(todo.isTodoSetAlarm());

        builder.setView(view);

        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTodayRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,TodoDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE,EDIT_TODO);
                intent.putExtra(IntentConstants.TODO,todo.getTodoId());
                startActivityForResult(intent,EDIT_TODO);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void editUpcomingTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final Todo todo = upcomingTodoArrayList.get(position);
        builder.setTitle(todo.getTodoName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view,null);
        TextView dialogCat = (TextView) view.findViewById(R.id.dialog_category);
        TextView dialogDesc = (TextView) view.findViewById(R.id.dialog_desc);
        TextView dialogDate = (TextView) view.findViewById(R.id.dialog_date);
        TextView dialogTime = (TextView) view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = (AppCompatRatingBar) view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = (SwitchCompat) view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    "+todo.getTodoCategory().getCategory());
        dialogDesc.setText(todo.getTodoDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String dateString = dateFormat.format(todo.getTodoDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeString = timeFormat.format(new Date(todo.getTodoTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(todo.getTodoPriority());
        dialogSwitch.setChecked(todo.isTodoSetAlarm());

        builder.setView(view);

        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpcomingRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,TodoDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE,EDIT_TODO);
                intent.putExtra(IntentConstants.TODO,todo.getTodoId());
                startActivityForResult(intent,EDIT_TODO);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void editDoneTodo(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Todo todo = doneTodoArrayList.get(position);
        builder.setTitle(todo.getTodoName());
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dailog_view,null);
        TextView dialogCat = (TextView) view.findViewById(R.id.dialog_category);
        TextView dialogDesc = (TextView) view.findViewById(R.id.dialog_desc);
        TextView dialogDate = (TextView) view.findViewById(R.id.dialog_date);
        TextView dialogTime = (TextView) view.findViewById(R.id.dialog_time);
        AppCompatRatingBar dialogPriority = (AppCompatRatingBar) view.findViewById(R.id.dialog_priority);
        SwitchCompat dialogSwitch = (SwitchCompat) view.findViewById(R.id.dialog_alarm);

        dialogCat.setText("    "+todo.getTodoCategory().getCategory());
        dialogDesc.setText(todo.getTodoDesc());

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String dateString = dateFormat.format(todo.getTodoDate());
        dialogDate.setText(dateString);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeString = timeFormat.format(new Date(todo.getTodoTime()));
        dialogTime.setText(timeString);

        dialogPriority.setRating(todo.getTodoPriority());
        dialogSwitch.setChecked(todo.isTodoSetAlarm());

        builder.setView(view);

        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDoneRecyclerAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,TodoDetailsActivity.class);
                intent.putExtra(IntentConstants.REQ_CODE,EDIT_TODO);
                intent.putExtra(IntentConstants.TODO,todo.getTodoId());
                startActivityForResult(intent,EDIT_TODO);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

}