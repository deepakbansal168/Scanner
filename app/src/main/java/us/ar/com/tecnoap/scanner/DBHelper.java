package us.ar.com.tecnoap.scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Usman on 1/21/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "appscanner.db";
    public static final String TABLE_NAME = "appscanner";
    public static final String TICKET_DETAILS = "productdetails";

    public static final String COL1 = "ID";
    public static final String COL2 = "P_NAME";
    public static final String COL3 = "P_PRICE";
    public static final String COL4 = "P_DATE";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 13);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY , " +
                " P_NAME TEXT , P_PRICE TEXT , P_DATE TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public void add_data(ArrayList<Model> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (Model data : list) {
                contentValues.put(COL1, data.getCode());
                contentValues.put(COL2, data.getName());
                contentValues.put(COL3, data.getPrice());
                contentValues.put(COL4, data.getDate());
                db.insert(TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public boolean addData(String p_id, String p_name, String p_price, String p_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, p_id);
        contentValues.put(COL2, p_name);
        contentValues.put(COL3, p_price);
        contentValues.put(COL4, p_date);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            Log.d("DB", "true");
            return true;
        }
    }

    public void getpnameprice(TextView name,TextView price,TextView pricetext,String code){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID= '" + code + "'", null);
        name.setText("");
        price.setText("");
        pricetext.setVisibility(View.VISIBLE);
        while (cursor.moveToNext()) {
            name.setText(" NAME: " + cursor.getString(1));
            price.setText(""+ cursor.getString(2));
        }
    }

    public void againCreate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY , " +
                " P_NAME TEXT , P_PRICE TEXT , P_DATE TEXT)");
        db.close();
    }


    public void deleteAllRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.close();
    }

    public void deleteRows(Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  " + TABLE_NAME);
        db.close();
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setMessage("Please read file again to scan new product");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public boolean getCode(Context context,String code) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor codeCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID= '" + code + "'", null);
            if (codeCursor.moveToFirst()) {
                return true;
            }
            return false;
        }catch (SQLException e){
            AppMsg.makeText((Activity) context,"No Product please read the file again",AppMsg.STYLE_CONFIRM).show();
            return false;
        }

    }

    public void updateStatus( String price, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET P_PRICE ='" + price + "' WHERE ID ='" + code + "'");
        db.close();
        Log.i("SCAN db helper", "--"+code);
    }

    public void syncdatabase(Context context){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME , null);
        int lines=cursor.getCount();
        ArrayList<String> data=new ArrayList<>();
        while (cursor.moveToNext()) {
            data.add(cursor.getString(0) + ";" + cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + "   "+" \n");
        }
        File myDirectory = new File(Environment.getExternalStorageDirectory(), "Scanner");
        File newfile=new File(myDirectory.getAbsolutePath()+"/"+"chkprice.txt");
            if(myDirectory.exists()) {
                writefile(data,newfile);
            }else{
                myDirectory.mkdir();
                writefile(data,newfile);
            }
        Log.i("Check string",lines+"--"+data.size()+"");
        //deleteRows(context);
    }

    void writefile(ArrayList<String> data,File newfile){
        try {
            if(newfile.exists()){
                try {
                    try {

                        OutputStreamWriter outStreamWriter = null;
                        FileOutputStream outStream = null;
                        outStream = new FileOutputStream(newfile,true) ;
                        outStreamWriter = new OutputStreamWriter(outStream);

                        Log.i("database data","working");
                        for(String d: data){
                            outStreamWriter.append(d);
                        }
                        outStreamWriter.flush();
                    }
                    catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }

                } catch (Exception e) {
                    android.util.Log.d("failed to save file", e.toString());
                }
            }else{
                newfile.createNewFile();
                writefile(data, newfile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void getData() {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            Log.i("database data",cursor.getString(0) + "   " + cursor.getString(1) + "    " + cursor.getString(2) + "   " + cursor.getString(3) + "   ");
        }
    }
}