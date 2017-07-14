package com.example.deepanshu.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Date;

public class TodoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    final static int ADD_TODO = 0;
    final static int EDIT_TODO = 1;

    Todo caseTodo;
    int position;
    int reqCode;

    private int yearTemp = 0;
    private int monthTemp = -1;
    private int datetemp = -1;

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

        todoNameEditText = (EditText) findViewById(R.id.todo_name_edittext);
        todoCategorySpinner = (Spinner) findViewById(R.id.todo_category_spinner);
        todoDateEditText = (EditText) findViewById(R.id.todo_date_edittext);
        todoTimeEditText = (EditText) findViewById(R.id.todo_time_edittext);
        todoAlarmSwitch = (Switch) findViewById(R.id.todo_alarm_switch);
        todoDescEditText = (EditText) findViewById(R.id.todo_desc_edittext);
        todoPriorityRatingBar = (RatingBar) findViewById(R.id.todo_priority_ratingbar);
        submitButton = (Button) findViewById(R.id.todo_submit_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.category_spinner,android.R.layout.simple_spinner_item);
//        adapter.add("");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        todoCategorySpinner.setAdapter(adapter);

        Intent intent = getIntent();
        reqCode = intent.getIntExtra("reqCode",-1);

        if(reqCode==EDIT_TODO){
            //TODO
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
            if((yearTemp==0 || monthTemp == -1 || datetemp==-1) || todoDateEditText.getText().toString().equals("")){
                todoTimeEditText.setError("Select Date");
                return;
            }

            Calendar newCalendar = Calendar.getInstance();
            int initialHour = newCalendar.get(Calendar.HOUR_OF_DAY);
            int initialMin = newCalendar.get(Calendar.MINUTE);
            showTimePicker(TodoDetailsActivity.this,initialHour,initialMin);

        }else if(id==R.id.todo_submit_button) {
            String name = todoNameEditText.getText().toString();
            if (name.equals("")) {
                todoNameEditText.setError("Required field");
                return;
            }
            caseTodo.setTodoName(name);
            //TODO category work
            caseTodo.setTodoDesc(todoDescEditText.getText().toString());
            caseTodo.setTodoSetAlarm(todoAlarmSwitch.isChecked());
            todoPriorityRatingBar.getRating();

            Intent intent = new Intent(TodoDetailsActivity.this,MainActivity.class);

            if(reqCode==ADD_TODO){
                TodoDatabase database = TodoDatabase.getInstance(this);
                final TodoDao dao = database.todoDao();
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        dao.insertInDb(caseTodo);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        return;
                    }
                }.execute();
            }else if(reqCode==EDIT_TODO){
                //TODO
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
                datetemp = dayOfMonth;

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
                Calendar calendar = Calendar.getInstance();
                calendar.set(yearTemp,monthTemp,datetemp,hourOfDay,minute);
                caseTodo.setTodoTime(calendar.getTimeInMillis());
                DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String timeString = timeFormat.format(new Date(caseTodo.getTodoTime()));
                todoTimeEditText.setText(timeString);
            }
        }, initialHour, initialMin, false);

        timePickerDialog.show();
    }

}
