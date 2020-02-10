package com.example.bpradmin.igisubizo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bpradmin on 10/14/2017.
 */
public class etracking extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "etracking.db";
    public static final int DATABASE_VERSION = 1;
    public static final String LOSTS_TABLE_NAME = "losts";
    public static final String LOSTS_COLUMN_ID = "id";
    public static final String LOSTS_COLUMN_POSTOFFID = "postoff_id";
    public static final String LOSTS_COLUMN_DOCTYPE = "doctype";
    public static final String LOSTS_COLUMN_OWNER = "owner";
    public static final String LOSTS_COLUMN_IDENTIFIER = "identifier";
    public static final String LOSTS_COLUMN_STATUS = "status";
    public static final String LOSTS_COLUMN_REGDATE= "regdate";

    public etracking(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists " + LOSTS_TABLE_NAME +
                        "(" + LOSTS_COLUMN_ID + " integer primary key autoincrement, " + LOSTS_COLUMN_POSTOFFID + " integer, " + LOSTS_COLUMN_OWNER + " text,"+ LOSTS_COLUMN_DOCTYPE + " text," + LOSTS_COLUMN_IDENTIFIER + " text" +LOSTS_COLUMN_STATUS + " integer" + LOSTS_COLUMN_REGDATE + " text");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOSTS_TABLE_NAME);
        onCreate(db);
    }
    public boolean addLocalLosts(String owner,String doctype,String identifier,int distid,int secid){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat sda=new SimpleDateFormat("YYYY-mm-dd");
        Date now=new Date("yyyy-MM-dd");
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOSTS_COLUMN_OWNER, owner);
        contentValues.put(LOSTS_COLUMN_DOCTYPE, doctype);
        contentValues.put(LOSTS_COLUMN_IDENTIFIER, identifier);
        contentValues.put(LOSTS_COLUMN_STATUS, "waiting");
        contentValues.put(LOSTS_COLUMN_REGDATE, now.toString());
        db.insert(LOSTS_TABLE_NAME, null, contentValues);

    return true;
    }
    public ArrayList getNonSentLocalLosts(){
        ArrayList arrayList=new ArrayList<>();
        JSONObject jsonResult=new JSONObject();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from " + LOSTS_TABLE_NAME + " where " + LOSTS_COLUMN_STATUS + "=?", new String[]{"waiting"});
        if (res.moveToFirst()){
            while (!res.isAfterLast()){
                try {
                    jsonResult.put("id", res.getString(res.getColumnIndex(LOSTS_COLUMN_ID)));
                    jsonResult.put("owner", res.getString(res.getColumnIndex(LOSTS_COLUMN_OWNER)));
                    jsonResult.put("doctype", res.getString(res.getColumnIndex(LOSTS_COLUMN_DOCTYPE)));
                    jsonResult.put("identifier", res.getString(res.getColumnIndex(LOSTS_COLUMN_IDENTIFIER)));
                    jsonResult.put("regdate", res.getString(res.getColumnIndex(LOSTS_COLUMN_REGDATE)));
                    arrayList.add(jsonResult);
                    res.moveToNext();
                }catch (JSONException ex){
                    ex.printStackTrace();
                }
            }
        }
        return arrayList;
    }
    public boolean updateLocalLosts(Integer id,String owner,String doctype,String identifier,int distid,int secid){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat sda=new SimpleDateFormat("YYYY-mm-dd");
        Date now=new Date("yyyy-MM-dd");
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOSTS_COLUMN_OWNER, owner);
        contentValues.put(LOSTS_COLUMN_DOCTYPE, doctype);
        contentValues.put(LOSTS_COLUMN_IDENTIFIER, identifier);
        contentValues.put(LOSTS_COLUMN_STATUS, "fail");
        contentValues.put(LOSTS_COLUMN_REGDATE, now.toString());
        db.update(LOSTS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
return true;
    }

    public boolean updateLocalLostsStatus(Integer id,String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOSTS_COLUMN_STATUS, status);
        db.update(LOSTS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public Cursor getAll(String table){
        SQLiteDatabase db=this.getReadableDatabase();
      Cursor data= db.rawQuery("select * from "+table,null);
        return  data;
    }

}
