package edu.phystech.stethoscope.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String PERSON_TABLE = "person";
    public static final String AUDIO_TABLE = "audio";
    public static final String POINT = "point";
    public static final String IS_HEART = "is_heart";
    public static final String NUMBER = "number";
    public static final String FILE_PATH = "file_path";
    public static final String ID = "id";
    public static final String PERSON_ID = "person_id";
    public static final String DB_NAME = "st_db";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String AGE = "age";
    public static final String DATE = "date";
    public static final String COMMENT = "comment";
        
    private static final int DB_VERSION = 1;
    private static final String CREATE_PERSON_STATEMENT =
            "CREATE TABLE " + PERSON_TABLE +" (" +
                    ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    FIRST_NAME + " text NOT NULL, " +
                    LAST_NAME + " text NOT NULL," +
                    AGE + " integer NOT NULL," +
                    DATE + " text NOT NULL," +
                    COMMENT + " text NOT NULL" + ");";
    private static final String CREATE_AUDIO_STATEMENT =
            "CREATE TABLE " + AUDIO_TABLE +" (" +
                    ID + " integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    PERSON_ID + " integer NOT NULL, " +
                    POINT + " integer NOT NULL, " +
                    IS_HEART + " integer NOT NULL," +
                    FILE_PATH + " text NOT NULL," +
                    NUMBER + " integer NOT NULL" +
                    ");";

    @Inject
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PERSON_STATEMENT);
        db.execSQL(CREATE_AUDIO_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
