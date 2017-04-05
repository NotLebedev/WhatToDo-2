package ru.linker.whattodo.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import ru.linker.whattodo.ActionListener;

/**
 * Created by root on 2/5/17.
 * Licensed under Attribution-NonCommercial 3.0 Unported
 */

public class TaskStorageModel {

    //Singleton:
    private static TaskStorageModel taskStorageModel = null;
    //List views listening for added/deleted/modifyed tasks
    private ArrayList<Task> highPriority; //Over 80 priority
    private ArrayList<Task> midPriority; //51-80 priority
    private ArrayList<Task> lowPriority; //less than 51
    private ArrayList<ActionListener> highPriorityListeners;
    private ArrayList<ActionListener> midPriorityListeners;
    private ArrayList<ActionListener> lowPriorityListeners;
    //Database stuff
    private TaskDbHelper mDbHelper;


    private TaskStorageModel(Context context) {

        highPriority = new ArrayList<>();
        midPriority = new ArrayList<>();
        lowPriority = new ArrayList<>();

        highPriorityListeners = new ArrayList<>();
        midPriorityListeners = new ArrayList<>();
        lowPriorityListeners = new ArrayList<>();

        mDbHelper = null;
        //TaskStorageModel.context = context;
        mDbHelper = new TaskDbHelper(context);

    }

    /*public static TaskStorageModel getInstance() throws NoContextException {

        if (context == null) {

            throw new NoContextException();

        } else if (taskStorageModel == null) {

            taskStorageModel = new TaskStorageModel(TaskStorageModel.context);

        }

        return taskStorageModel;

    }*/

    public static TaskStorageModel getInstance(Context context) {

        if (taskStorageModel == null) {

            taskStorageModel = new TaskStorageModel(context);

        }

        return taskStorageModel;

    }
    //Singleton

    //TODO: refresh this add policy
    //This all was created to prevent duplicating tasks in db on star sync
    private void add(Task task) {

        //System.out.println("Java: addTask called with task : descr " + task.getTaskDescription() + " prior " + task.getPriority());

        Comparator<Task> comparator = new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {

                if (o1.getPriority() < o2.getPriority()) {
                    return 1;
                } else {
                    return -1;
                }

            }
        };

        if (task.getPriority() > 80) {

            highPriority.add(task);

            Collections.sort(highPriority, comparator);

        } else if (task.getPriority() > 50) {

            midPriority.add(task);

            Collections.sort(midPriority, comparator);


        } else {

            lowPriority.add(task);

            Collections.sort(lowPriority, comparator);


        }


    }

    private void addSync(Task task) {
        add(task);
        syncTableAddTask(task);
    }

    private void delete(Task task) {

        Comparator<Task> comparator = new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {

                if (o1.getPriority() < o2.getPriority()) {
                    return 1;
                } else {
                    return -1;
                }

            }
        };

        if (task.getPriority() > 80) {

            highPriority.remove(task);

            Collections.sort(highPriority, comparator);

        } else if (task.getPriority() > 50) {

            midPriority.remove(task);

            Collections.sort(midPriority, comparator);


        } else {

            lowPriority.remove(task);

            Collections.sort(lowPriority, comparator);


        }

    }

    private void deleteSync(Task task) {

        delete(task);
        syncTableDeleteTask(task);

    }

    //Sends actionPerformed to all listeners
    public void syncListeners() {
        for (ActionListener highPriorityListener : highPriorityListeners) {
            highPriorityListener.actionPerformed(0);
        }
        for (ActionListener midPriorityListener : midPriorityListeners) {
            midPriorityListener.actionPerformed(1);
        }
        for (ActionListener lowPriorityListener : lowPriorityListeners) {
            lowPriorityListener.actionPerformed(2);
        }
    }

    public void addTask(Task task) {

        addSync(task);
        syncListeners();

    }

    public void addTasks(ArrayList<Task> tasks) {

        for (Task task : tasks) {
            addSync(task);
        }

        syncListeners();

    }

    public void deleteTask(Task task) {

        deleteSync(task);
        syncListeners();

    }

    public void updateTask(Task oldTask, Task newTask) {

        delete(oldTask);
        add(newTask);
        syncTableUpdateTask(oldTask, newTask);
        syncListeners();

    }

    public void addHighPriorityListener(ActionListener listener) {
        highPriorityListeners.add(listener);
    }

    public void addMidPriorityListener(ActionListener listener) {
        midPriorityListeners.add(listener);
    }

    public void addLowPriorityListener(ActionListener listener) {
        lowPriorityListeners.add(listener);
    }

    public void removeListener(ActionListener listener) {
        highPriorityListeners.remove(listener);
        midPriorityListeners.remove(listener);
        lowPriorityListeners.remove(listener);
    }

    public ArrayList<Task> getList(Integer params) {

        if (params.compareTo(0) == 0)
            return highPriority;
        else if (params.compareTo(1) == 0)
            return midPriority;
        else if (params.compareTo(2) == 0)
            return lowPriority;
        else
            return null;

    }

    public void syncModel() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = TaskDbHelper.TaskTable.projectionAll;

        Cursor cursor = db.query(TaskDbHelper.TaskTable.TABLE_NAME, projection, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {

                Task task = null;

                if (cursor.getInt(cursor.getColumnIndexOrThrow(TaskDbHelper.TaskTable.COLUMN_NAME_IS_DATED)) == 0) {
                    task = (new Task(cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.TaskTable.COLUMN_NAME_DESCRIPTION)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(TaskDbHelper.TaskTable.COLUMN_NAME_PRIORITY))));
                } else {
                    try {
                        task = new DatedTask(cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.TaskTable.COLUMN_NAME_DESCRIPTION)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(TaskDbHelper.TaskTable.COLUMN_NAME_PRIORITY)),
                                formCalendar(cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.TaskTable.COLUMN_NAME_DATE))),
                                formCalendar(cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.TaskTable.COLUMN_NAME_DATE_START))));
                    } catch (DateBeforeTodayException e) {
                        e.printStackTrace();
                    }
                }

                add(task);

            } while (cursor.moveToNext());

            syncListeners();

        }

        cursor.close();
        db.close();

    }

    private void syncTableAddTask(Task task) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.TaskTable.COLUMN_NAME_IS_DATED, 0);
        values.put(TaskDbHelper.TaskTable.COLUMN_NAME_DESCRIPTION, task.getTaskDescription());
        values.put(TaskDbHelper.TaskTable.COLUMN_NAME_PRIORITY, task.getNonDatedPriority());

        if (task instanceof DatedTask) {
            values.put(TaskDbHelper.TaskTable.COLUMN_NAME_IS_DATED, 1);
            values.put(TaskDbHelper.TaskTable.COLUMN_NAME_DATE, formDatetime(((DatedTask) task).getDateEnd()));
            values.put(TaskDbHelper.TaskTable.COLUMN_NAME_DATE_START, formDatetime(((DatedTask) task).getDateStart()));
        }

        db.insert(TaskDbHelper.TaskTable.TABLE_NAME, null, values);

        db.close();

    }

    private void syncTableDeleteTask(Task task) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection =
                TaskDbHelper.TaskTable.COLUMN_NAME_DESCRIPTION
                        + " = ? AND " + TaskDbHelper.TaskTable.COLUMN_NAME_PRIORITY +
                        " = ?";

        String[] selectionArgs = {task.getTaskDescription(), task.getPriority().toString()};

        db.delete(TaskDbHelper.TaskTable.TABLE_NAME, selection, selectionArgs);

    }

    private void syncTableUpdateTask(Task oldTask, Task newTask) {

        syncTableDeleteTask(oldTask);

        syncTableAddTask(newTask);

    }

    private String formDatetime(Calendar date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return dateFormat.format(date.getTime());

    }

    private Calendar formCalendar(String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        java.util.Date dateParsed = null;

        try {
            dateParsed = (java.util.Date) dateFormat.parseObject(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateParsed);

        return calendar;

    }

}
