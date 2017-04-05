package ru.linker.whattodo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ru.linker.whattodo.Model.Task;
import ru.linker.whattodo.Model.TaskStorageModel;

/**
 * Created by root on 2/4/17.
 * Licensed under Attribution-NonCommercial 3.0 Unported
 */

public class TaskAdapter extends BaseAdapter implements ActionListener {

    private LayoutInflater layoutInflater;
    private ArrayList<Task> tasks;

    private TaskStorageModel model;

    public TaskAdapter(Context context, ArrayList<Task> tasks, TaskStorageModel model) {

        this.model = model;

        this.tasks = tasks;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    private Task getTask(int position) {

        return ((Task) getItem(position));

    }

    public void addTask(Task task) {

        tasks.add(task);

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item_colored, parent, false);
        }

        Task task = getTask(position);

        ((TextView) view.findViewById(R.id.elementText)).setText(task.getTaskDescription());
        ((ProgressBar) view.findViewById(R.id.elementPriority)).setProgress(task.getPriority());
        ((ProgressBar) view.findViewById(R.id.elementPriority)).getProgressDrawable().setColorFilter(
                Color.rgb(task.getPriority() * 255 / 100, 0, 255 - task.getPriority() * 255 / 100),
                PorterDuff.Mode.SRC_IN);

        return view;
    }


    @Override
    public void actionPerformed(Integer params) {

        System.out.println("Java: actionPerformed");
        tasks = model.getList(params);
        notifyDataSetChanged();

    }
}
