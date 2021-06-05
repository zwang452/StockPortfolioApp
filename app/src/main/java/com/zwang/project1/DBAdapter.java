package com.zwang.project1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    static final String KEY_ROWID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_SYMBOL = "symbol";
    static final String KEY_URL = "url";
    static final String KEY_IMAGEURL = "imageUrl";
    static final String KEY_PRICE = "price";
    static final String KEY_CHANGE = "change";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "portfolio";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE =
            "create table portfolio (_id integer primary key autoincrement, "
                    + "name text not null, symbol text not null,url text not null," +
                    "imageUrl text not null, price text not null,change text not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS portfolio");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertStock(String name, String symbol, String url, String imageUrl, String price,
                            String change)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_SYMBOL, symbol);
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_IMAGEURL, imageUrl);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_CHANGE, change);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteStock(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllStocks()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_SYMBOL, KEY_URL, KEY_IMAGEURL,KEY_PRICE,KEY_CHANGE}, null, null, null, null, null);
    }

    //---retrieves a particular contact---
    public Cursor getStock(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                                KEY_SYMBOL, KEY_URL, KEY_IMAGEURL,KEY_PRICE,KEY_CHANGE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateStock(long rowId, String name, String symbol, String url, String imageUrl,
                               String price, String change)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_SYMBOL, symbol);
        args.put(KEY_URL, url);
        args.put(KEY_IMAGEURL, imageUrl);
        args.put(KEY_PRICE, price);
        args.put(KEY_CHANGE, change);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
