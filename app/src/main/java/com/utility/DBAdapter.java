package com.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Carlos VÉLEZ on 01/11/2016.
 */

public class DBAdapter {
    //estos son los nombres de las columnas
    public static final String COL_ID = "_id";
    public static final String COL_KEY = "key_param";
    public static final String COL_VALUE = "key_value";
    public static final String COL_TYPE = "key_type";

    //estos son los índices correspoindientes
    public static final int INDEX_ID = 0;
    public static final int INDEX_PARAM = INDEX_ID + 1;
    public static final int INDEX_VALUE = INDEX_ID + 2;
    public static final int INDEX_TYPE = INDEX_ID + 3;

    // usado for logging
    private static final String TAG = "SettingsAppArrendatur";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "DBAppArrendatur";
    private static final String TABLE_NAME = "app_settings";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    //declaración SQL usada para crear la base de datos
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_KEY + " TEXT, " +
                    COL_VALUE + " TEXT, " +
                    COL_TYPE + " TEXT );";

    public DBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    //abrir
    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }

    //cerrar
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    //CREATE
    //ten en cuenta que la id será creada automáticamente
    public void addSettings(String keyName, String keyValue, String keyType) {
        ContentValues values = new ContentValues();
        values.put(COL_KEY, keyName);
        values.put(COL_VALUE, keyValue);
        values.put(COL_TYPE, keyType);

        // Insertar en la bd
        mDb.insert(TABLE_NAME, null, values);
    }

    //sobrecargado para tomar un aviso
    public long addSettings(SettingsObj dataObj) {
        ContentValues values = new ContentValues();
        values.put(COL_KEY, dataObj.getKeyName());
        values.put(COL_VALUE, dataObj.getKeyValue());
        values.put(COL_TYPE, dataObj.getKeyType());

        // Insertar fila
        return mDb.insert(TABLE_NAME, null, values);
    }

    //Obtener una fila por id
    public SettingsObj fetchSettingById(int id) {
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_KEY, COL_VALUE, COL_TYPE}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );

        if (cursor != null)
            cursor.moveToFirst();

        return new SettingsObj(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_PARAM),
                cursor.getString(INDEX_VALUE),
                cursor.getString(INDEX_TYPE),
                cursor.getCount()
        );
    }

    public SettingsObj fetchSettingBykey(String keyName) {
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_KEY, COL_VALUE, COL_TYPE}, COL_KEY + "=?",
                new String[]{keyName}, null, null, null, null
        );

        if (cursor != null)
            cursor.moveToFirst();

        if ((cursor != null) && (cursor.getCount() > 0)) {
            return new SettingsObj(
                    cursor.getInt(INDEX_ID),
                    cursor.getString(INDEX_PARAM),
                    cursor.getString(INDEX_VALUE),
                    cursor.getString(INDEX_TYPE),
                    cursor.getCount()
            );
        } else {
            // Nada...
            return new SettingsObj(
                    0, "", "", "", 0
            );
        }
    }

    // Obtener todas los campos
    public Cursor fetchAllSettings() {
        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_KEY, COL_VALUE, COL_TYPE},
                null, null, null, null, null
        );

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    //UPDATE
    public void updateSetting(SettingsObj settingsObj) {
        ContentValues values = new ContentValues();
        values.put(COL_KEY, settingsObj.getKeyName());
        values.put(COL_VALUE, settingsObj.getKeyValue());
        values.put(COL_TYPE, settingsObj.getKeyType());

        mDb.update(TABLE_NAME, values,
                COL_ID + "=?", new String[]{String.valueOf(settingsObj.getId())});
    }

    // DELETE
    public void deleteSettingById(int nId) {
        mDb.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(nId)});
    }

    // Eliminar todos
    public void deleteAllSettings() {
        mDb.delete(TABLE_NAME, null, null);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}