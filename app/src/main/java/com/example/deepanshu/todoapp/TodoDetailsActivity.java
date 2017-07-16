package com.example.deepanshu.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    final static int ADD_TODO = 0;
    final static int EDIT_TODO = 1;


    ArrayAdapter<CharSequence> adapter;
    List<Category> categoryList = new ArrayList<>();
    TodoDao todoDao;
    CategoryDao categoryDao;
    Todo caseTodo;
    int reqCode;

    private int yearTemp = 0;
    private int monthTemp = -1;
    private int dateTemp = -1;

    EditText todoNameEditText;
    Spinner todoCategorySpinner;
    EditText todoDateEditText;
    EditText todoTimeEditText;
    Switch todoAlarmSwitch;
    EditText todoDescEditText;
    RatingBar todoPriorityRatingBar;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);

        if(reqCode == ADD_TODO){
            setTitle("Add Todo");
        }else{
            setTitle("Edit Todo");
        }

        todoNameEditText = (EditText) findViewById(R.id.todo_name_edittext);
        todoCategorySpinner = (Spinner) findViewById(R.id.todo_category_spinner);
        todoDateEditText = (EditText) findViewById(R.id.todo_date_edittext);
        todoTimeEditText = (EditText) findViewById(R.id.todo_time_edittext);
        todoAlarmSwitch = (Switch) findViewById(R.id.todo_alarm_switch);
        todoDescEditText = (EditText) findViewById(R.id.todo_desc_edittext);
        todoPriorityRatingBar = (RatingBar) findViewById(R.id.todo_priority_ratingbar);
        submitButton = (Button) findViewById(R.id.todo_submit_button);

        adapter = ArrayAdapter.createFromResource(this,R.array.category_spinner,android.R.layout.simple_spinner_item);
//        TodoDatabase database = TodoDatabase.getInstance(this);
//        categoryDao = database.categoryDao();
//        new AsyncTask<Void,Void,Void>(){
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                categoryList = categoryDao.getAllCategories();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                for(int i=0;i<categoryList.size();i++){
//                    adapter.add(categoryList.get(i).getCategory());
//                    adapter.notifyDataSetChanged();
//                }
//
//            }
//        }.execute();
        //temp work
        String[] array = getResources().getStringArray(R.array.category_spinner);
        for(int i=0;i<array.length;i++){
            categoryList.add(new Category(array[i]));
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        todoCategorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        reqCode = intent.getIntExtra(IntentConstants.REQ_CODE,-1);

        if(reqCode==EDIT_TODO){
            caseTodo = (Todo) intent.getSerializableExtra(IntentConstants.TODO);
            todoNameEditText.setText(caseTodo.getTodoName());
            //got caseTodo category position from categorylist
            int index = categoryList.indexOf(caseTodo.getTodoCategory());
            todoCategorySpinner.setSelection(index,true);

            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(caseTodo.getTodoDate());
            yearTemp = calendar.get(Calendar.YEAR);
            monthTemp = calendar.get(Calendar.MONTH);
            dateTemp = calendar.get(Calendar.DATE);
            String dateString = dateFormat.format(caseTodo.getTodoDate());
            todoDateEditText.setText(dateString);

            DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            String timeString = timeFormat.format(new Date(caseTodo.getTodoTime()));
            todoTimeEditText.setText(timeString);

            todoAlarmSwitch.setChecked(caseTodo.isTodoSetAlarm());
            todoDescEditText.setText(caseTodo.getTodoDesc());
            todoPriorityRatingBar.setRating(caseTodo.getTodoPriority());

        }else if(reqCode == ADD_TODO) {
            caseTodo = new Todo();
        }

        todoDateEditText.setOnClickListener(this);
        todoTimeEditText.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id==R.id.todo_date_edittext){
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            showDatePicker(this,year,month,date);

        }else if(id == R.id.todo_time_edittext){
            if((yearTemp==0 || monthTemp == -1 || dateTemp==-1) || todoDateEditText.getText().toString().equals("")){
                todoDateEditText.setError("Required Field");
                return;
            }

            Calendar newCalendar = Calendar.getInstance();
            int initialHour = newCalendar.get(Calendar.HOUR_OF_DAY);
            int initialMin = newCalendar.get(Calendar.MINUTE);
            showTimePicker(TodoDetailsActivity.this,initialHour,initialMin);

        }else if(id== R.id.todo_submit_button) {
            String name = todoNameEditText.getText().toString();
            if (name.equals("")) {
                todoNameEditText.setError("Required field");
                return;
            }

            if(todoDateEditText.getText().toString().equals("")){
                todoDateEditText.setError("Required field");
                return;
            }

            if(todoTimeEditText.getText().toString().equals("")){
                setTime(0,0);
            }

            caseTodo.setTodoName(name);

            int categoryPosition = todoCategorySpinner.getSelectedItemPosition();
            if(categoryPosition == 0 ){
                Category category = new Category(DbConstants.CATEGORY_OTHERS);
                caseTodo.setTodoCategory(category);
            }else{
                caseTodo.setTodoCategory(categoryList.get(categoryPosition));
            }

            caseTodo.setTodoDesc(todoDescEditText.getText().toString());
            caseTodo.setTodoSetAlarm(todoAlarmSwitch.isChecked());

            Intent intent = new Intent(TodoDetailsActivity.this,MainActivity.class);

            if(reqCode==ADD_TODO){
                TodoDatabase database = TodoDatabase.getInstance(this);
                todoDao = database.todoDao();
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        todoDao.insertInDb(caseTodo);
                        return null;
                    }
                }.execute();
            }
            else if(reqCode==EDIT_TODO){
                TodoDatabase database = TodoDatabase.getInstance(this);
                todoDao = database.todoDao();
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        todoDao.updateDb(caseTodo);
                        return null;
                    }
                }.execute();
            }

            setResult(RESULT_OK,intent);
            finish();
        }
    }

    private void showDatePicker(Context context, int initialYear, int initialMonth,
                                int initialDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth);
                yearTemp = year;
                monthTemp = month;
                dateTemp = dayOfMonth;

                caseTodo.setTodoDate(calendar.getTime());

                DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                String dateString = dateFormat.format(caseTodo.getTodoDate());

                todoDateEditText.setText(dateString);
            }
        }, initialYear, initialMonth, initialDate);
        datePickerDialog.show();
    }

    private void showTimePicker(Context context, int initialHour, int initialMin) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTime(hourOfDay,minute);
            }
        }, initialHour, initialMin, false);

        timePickerDialog.show();
    }

    public void setTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearTemp,monthTemp,dateTemp,hourOfDay,minute);
        caseTodo.setTodoTime(calendar.getTimeInMillis());
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeString = timeFormat.format(new Date(caseTodo.getTodoTime()));
        todoTimeEditText.setText(timeString);
    }
}
