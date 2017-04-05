package ru.linker.whattodo;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;

import ru.linker.whattodo.Model.DateBeforeTodayException;
import ru.linker.whattodo.Model.DatedTask;
import ru.linker.whattodo.Model.Task;
import ru.linker.whattodo.Model.TaskStorageModel;

public class AddTask extends AppCompatActivity {

    Task taskPassed;
    private EditText editText;
    private SeekBar seekBar;
    private Button submitButton;
    private TaskStorageModel storageModel;
    private Boolean isModifyMode;
    private Calendar date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskPassed = (Task) getIntent().getSerializableExtra("TASK_MODIFY");

        editText = (EditText) findViewById(R.id.description_field);
        TextView hintTextView = (TextView) findViewById(R.id.priority_hint_textView);
        seekBar = (SeekBar) findViewById(R.id.priority_bar);
        submitButton = (Button) findViewById(R.id.add_task_button);

        submitButton.setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    submitButton.setEnabled(false);
                } else {
                    submitButton.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LayerDrawable ld = (LayerDrawable) seekBar.getProgressDrawable();
                //ClipDrawable cd = (ClipDrawable) ld.findDrawableByLayerId(R.id.priority_bar);
                ld.setColorFilter(Color.rgb(progress * 255 / 100, 0, 255 - progress * 255 / 100),
                        PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //try {
        storageModel = TaskStorageModel.getInstance(this);
        //} catch (NoContextException e) {
        //    e.printStackTrace();
        //}


        if (taskPassed != null) {
            isModifyMode = true;

            getSupportActionBar().setTitle(R.string.bar_modify_title);
            hintTextView.setText(R.string.modify_priority_hint);
            submitButton.setText(R.string.modify_button_text);
            editText.setText(taskPassed.getTaskDescription());
            seekBar.setProgress(taskPassed.getPriority());
        } else {
            isModifyMode = false;
            seekBar.setProgress(seekBar.getProgress() + 1); //Updates View to make it right-colored TODO: find a better solution
            getSupportActionBar().setTitle(R.string.bar_add_title);
        }

    }

    public void submit(View view) {

        //Forming task

        Task task;

        if (date == null) {
            task = new Task(editText.getText().toString(), seekBar.getProgress());
        } else {
            try {
                task = new DatedTask(editText.getText().toString(), seekBar.getProgress(), date);
            } catch (DateBeforeTodayException e) {

                new AlertDialog.Builder(this)
                        .setMessage("Please select date after today")
                        .setPositiveButton("Ok", null)
                        .show();

                return;
            }
        }

        if (isModifyMode) {

            storageModel.updateTask(taskPassed, task);

        } else {

            storageModel.addTask(task);

        }

        finish();

    }

    public void addDate(View view) {

        final Calendar tmpDate = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tmpDate.set(Calendar.YEAR, year);
                tmpDate.set(Calendar.MONTH, month);
                tmpDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                date = tmpDate;
            }

        };

        new DatePickerDialog(this, dateSetListener, tmpDate.get(Calendar.YEAR), tmpDate.get(Calendar.MONTH), tmpDate.get(Calendar.DAY_OF_MONTH)).show();

    }

}
