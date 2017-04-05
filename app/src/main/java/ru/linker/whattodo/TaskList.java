package ru.linker.whattodo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.linker.whattodo.Model.Task;
import ru.linker.whattodo.Model.TaskStorageModel;

public class TaskList extends AppCompatActivity {

    private TaskStorageModel storage;

    private ListView lv;

    private Context context;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Task task = (Task) parent.getItemAtPosition(position);

            final Intent intent = new Intent(context, AddTask.class);

            intent.putExtra("TASK_MODIFY", task);

            startActivity(intent);

        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

            new AlertDialog.Builder(context)
                    .setMessage(R.string.dialog_is_done_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
                            anim.setDuration(300);

                            parent.getChildAt(position).startAnimation(anim);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    storage.deleteTask((Task) parent.getItemAtPosition(position));
                                }
                            }, anim.getDuration());

                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        System.out.println("View opened");

        //try {
        storage = TaskStorageModel.getInstance(this);
        //} catch (NoContextException e) {
        //    e.printStackTrace();
        //}

        lv = (ListView) findViewById(R.id.task_list_lv);

        switch (getIntent().getIntExtra("LV_DISPLAY", 3)) {

            case 0:
                lv.setAdapter(new TaskAdapter(this, new ArrayList<Task>(), storage));
                storage.addHighPriorityListener((TaskAdapter) lv.getAdapter());
                break;
            case 1:
                lv.setAdapter(new TaskAdapter(this, new ArrayList<Task>(), storage));
                storage.addMidPriorityListener((TaskAdapter) lv.getAdapter());
                break;
            case 2:
                lv.setAdapter(new TaskAdapter(this, new ArrayList<Task>(), storage));
                storage.addLowPriorityListener((TaskAdapter) lv.getAdapter());
                break;
            default:
                finish();

        }

        lv.setOnItemClickListener(onItemClickListener);
        lv.setOnItemLongClickListener(onItemLongClickListener);

        storage.syncListeners();

        context = this;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        storage.removeListener((ActionListener) lv.getAdapter());
    }
}
