package ru.linker.whattodo.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by root on 2/5/17.
 * Licensed under Attribution-NonCommercial 3.0 Unported
 */

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "Tasks.db";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TaskTable.TABLE_NAME + " (" +
                    TaskTable._ID + " INTEGER PRIMARY KEY, " +
                    TaskTable.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                    TaskTable.COLUMN_NAME_PRIORITY + " INTEGER)";

    private static final String SQL_UPDATE_TABLE_SCHEMA_TO_5 =
            "ALTER TABLE " + TaskTable.TABLE_NAME +
                    " ADD COLUMN " + TaskTable.COLUMN_NAME_IS_DATED + " INTEGER DEFAULT 0";

    private static final String SQL_UPDATE_TABLE_SCHEMA_TO_6 =
            " ALTER TABLE " + TaskTable.TABLE_NAME +
                    " ADD COLUMN " + TaskTable.COLUMN_NAME_DATE + " TEXT";

    private static final String SQL_UPDATE_TABLE_SCHEMA_TO_7 =
            "ALTER TABLE " + TaskTable.TABLE_NAME +
                    " ADD COLUMN " + TaskTable.COLUMN_NAME_DATE_START + " TEXT";

    private static final String[] sql_updates = new String[]{SQL_UPDATE_TABLE_SCHEMA_TO_5, SQL_UPDATE_TABLE_SCHEMA_TO_6, SQL_UPDATE_TABLE_SCHEMA_TO_7};
    private static Integer updatesDone = 0;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        for (String sql_update : sql_updates) {
            db.execSQL(sql_update);
            System.out.println("Execed update: " + sql_update);
            updatesDone++;
        }
        System.out.println("Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = updatesDone; i < sql_updates.length; i++) {
            db.execSQL(sql_updates[i]);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public static class TaskTable implements BaseColumns {

        public static final String TABLE_NAME = "TASKS";
        public static final String COLUMN_NAME_DESCRIPTION = "DESCRIPTION";
        public static final String COLUMN_NAME_PRIORITY = "PRIORITY";
        public static final String COLUMN_NAME_IS_DATED = "IS_DATED";
        public static final String COLUMN_NAME_DATE = "DATE";
        public static final String COLUMN_NAME_DATE_START = "DATE_START";

        public static final String[] projectionAll = {COLUMN_NAME_DESCRIPTION, COLUMN_NAME_PRIORITY, COLUMN_NAME_IS_DATED, COLUMN_NAME_DATE, COLUMN_NAME_DATE_START};

    }

}