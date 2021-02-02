package com.sba.sinhalaphotoeditor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.database.ValueEventListener;
import com.sba.sinhalaphotoeditor.callbacks.OnFirebaseStickerChangedListener;
import com.sba.sinhalaphotoeditor.model.FirebaseStickerCategory;

public class StickerDatabase extends SQLiteOpenHelper
{

    private static final String STICKER_DATABASE_NAME = "Stickers.db";
    private static int STICKER_DATABASE_VERSION = 1;
    private Context context;

    //sticker category table
    private static final String STICKER_CATEGORY_TABLE_NAME = "StickerCategory";
    private static final String STICKER_CATEGORY_TABLE_COL_1 = "Id";
    private static final String STICKER_CATEGORY_TABLE_COL_2 = "Name";

    //Firebase
    private static final String FIREBASE_STICKERS_MAIN_PATH = "Stickers";
    private static final String FIREBASE_STICKERS_CATEGORIES_PATH = "Categories";

    public StickerDatabase(@Nullable Context context)
    {
        super(context, STICKER_DATABASE_NAME, null, STICKER_DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + STICKER_CATEGORY_TABLE_NAME + "(" + STICKER_CATEGORY_TABLE_COL_1+ " INTEGER PRIMARY KEY," + STICKER_CATEGORY_TABLE_COL_2 +" TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("DROP TABLE " + STICKER_CATEGORY_TABLE_NAME);
        createTableAgain(db);
    }
    public boolean createNewCategory(Integer id,String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STICKER_CATEGORY_TABLE_COL_1,id);
        values.put(STICKER_CATEGORY_TABLE_COL_2,name);

        long res = db.insert(STICKER_CATEGORY_TABLE_NAME,null,values);
        return res > 0;
    }
    public boolean findIsCategoryExists(Integer id,String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " +STICKER_CATEGORY_TABLE_NAME + " WHERE " + STICKER_CATEGORY_TABLE_COL_1 + "=" + id;
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor != null)
        {
            int size = cursor.getCount();
            cursor.close();
            if(size > 0)
            {
                return true;
            }
            return false;

        }
        return false;


    }
    public Cursor getAllCategories()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + STICKER_CATEGORY_TABLE_NAME,null);
    }

    private void createTableAgain(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + STICKER_CATEGORY_TABLE_NAME + "(" + STICKER_CATEGORY_TABLE_COL_1+ " INTEGER PRIMARY KEY," + STICKER_CATEGORY_TABLE_COL_2 +" TEXT NOT NULL)");
    }
    public static void sync(final OnFirebaseStickerChangedListener listener)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(FIREBASE_STICKERS_MAIN_PATH).child(FIREBASE_STICKERS_CATEGORIES_PATH);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dss : dataSnapshot.getChildren())
                    {
                       FirebaseStickerCategory category = dss.getValue(FirebaseStickerCategory.class);
                       if(category != null && category.getName() != null && category.getId() != null && listener != null)
                       {
                           listener.onCategoryChanged(category);
                       }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
    public boolean addCategory(FirebaseStickerCategory cat)
    {
        return false;
    }
}
