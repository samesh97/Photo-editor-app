package com.sba.sinhalaphotoeditor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.sba.sinhalaphotoeditor.sdk.Methods;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "Images.db";
    private static final String TABLE_NAME = "Images";

    private static final String COL_1 = "Id";
    private static final String COL_2 = "Image";
    private static final String COL_3 = "DateTime";


    private static final String USER_TABLE_NAME = "users";
    private static final String USER_TABLE_COL_1 = "id";
    private static final String USER_TABLE_COL_2 = "image";


    private static final int DATABASE_VERSION = 3;
    private Context context;

    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(Id INTEGER PRIMARY KEY AUTOINCREMENT,Image BLOB,DateTime TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + USER_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,image BLOB NOT NULL)");
    }
    public void createTableAgain(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(Id INTEGER PRIMARY KEY AUTOINCREMENT,Image BLOB,DateTime TEXT NOT NULL)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        createTableAgain(db);
    }
    public boolean AddImage(byte[] image,String date)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2,image);
        values.put(COL_3,date);

        long res = db.insert(TABLE_NAME,null,values);
        if(res < 0)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public boolean AddUserProfilePicture(byte[] image)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_TABLE_COL_2,image);

        long res = db.insert(USER_TABLE_NAME,null,values);
        if(res < 0)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public Cursor GetAllImages()
    {
        //deleteUnnessaryImages();
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_1 + " DESC",null);

    }
    public Cursor GetAllImagesASC()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_1 + " ASC",null);
    }
    public byte[] getBytes(Bitmap bitmap)
    {
        if(bitmap != null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }
        return  null;

    }
    public Bitmap getImage(byte[] image)
    {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    public boolean deleteImage(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TABLE_NAME,"Id = ?",new String[]{String.valueOf(id)});
        if(res > 0)
        {
            return true;
        }
        return false;
    }
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
    public int getNumberOfRows()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        int value =  cursor.getCount();
        cursor.close();
        return value;
    }
    public void deleteUnnessaryImages()
    {
        int recentImageMaxSize = 5;

        if(getNumberOfRows() > recentImageMaxSize)
        {
            int numberOfItemsNeedToDeleted = getNumberOfRows() - recentImageMaxSize;
            Cursor cursor = GetAllImagesASC();

            while (cursor.moveToNext())
            {
                numberOfItemsNeedToDeleted--;
                if(numberOfItemsNeedToDeleted == -1)
                {
                    break;
                }
                deleteImage(cursor.getInt(0));
                Methods.deleteFileFromInternalStorage(context,cursor.getString(2));
            }

            cursor.close();

        }

    }





}
