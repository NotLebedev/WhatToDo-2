package ru.linker.whattodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ru.linker.whattodo.Model.Task;
import ru.linker.whattodo.Model.TaskStorageModel;

public class MainScreen extends AppCompatActivity {

    TaskStorageModel storage;

    ListView lvHigh;
    ListView lvMid;
    ListView lvLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        storage = TaskStorageModel.getInstance(this);

        lvHigh = (ListView) findViewById(R.id.lvHigh);
        lvMid = (ListView) findViewById(R.id.lvMid);
        lvLow = (ListView) findViewById(R.id.lvMin);

        lvHigh.setAdapter(new TaskAdapter(this, new ArrayList<Task>(), storage));
        lvMid.setAdapter(new TaskAdapter(this, new ArrayList<Task>(), storage));
        lvLow.setAdapter(new TaskAdapter(this, new ArrayList<Task>(), storage));

        storage.addHighPriorityListener((TaskAdapter) lvHigh.getAdapter());
        storage.addMidPriorityListener((TaskAdapter) lvMid.getAdapter());
        storage.addLowPriorityListener((TaskAdapter) lvLow.getAdapter());

        storage.syncModel();

        configureScrolling(lvHigh);
        configureScrolling(lvMid);
        configureScrolling(lvLow);

        final Intent intent = new Intent(this, TaskList.class);

        //TODO: add canceling touch, when finger moved out of view
        lvHigh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    intent.putExtra("LV_DISPLAY", 0);
                    startActivity(intent);
                }
                return false;
            }

        });

        lvMid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    intent.putExtra("LV_DISPLAY", 1);
                    startActivity(intent);
                }
                return false;
            }

        });

        lvLow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    intent.putExtra("LV_DISPLAY", 2);
                    startActivity(intent);
                }
                return false;
            }

        });

    }


    private void configureScrolling(final ListView listView) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                int scrolledTo = 0;


                while (true) {

                    if (listView.getAdapter().getCount() > 1) {

                        if (scrolledTo != 0) {
                            scrolledTo++;
                        } else {
                            scrolledTo = listView.getLastVisiblePosition() + 1;
                        }

                        if (scrolledTo == listView.getAdapter().getCount()) {
                            scrolledTo = 0;
                        }

                        listView.smoothScrollToPosition(scrolledTo);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {

                        scrolledTo = 0;

                    }
                }
            }
        }).start();

        //listView.setEnabled(false);

    }

    public void addElement(View view) {

        Intent intent = new Intent(this, AddTask.class);

        startActivity(intent);

    }


}
