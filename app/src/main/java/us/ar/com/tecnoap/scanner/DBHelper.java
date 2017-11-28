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

//        String ticketDetails = "CREATE TABLE " + TICKET_DETAILS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "TICKETS_DETAILS_ID INTEGER , TICKET_TYPE TEXT , NUMBER_OF_TICKETS INTEGER , BARCODE TEXT)";
        sqLiteDatabase.execSQL(createTable);
        //sqLiteDatabase.execSQL(ticketDetails);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
       // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TICKET_DETAILS);
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

   /* public boolean addTicketDetails(String tickets_detail_id, String ticket_type, String number_of_tickets, String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL12, tickets_detail_id);
        contentValues.put(COL13, ticket_type);
        contentValues.put(COL14, number_of_tickets);
        contentValues.put(COL15, barcode);
        long result = db.insert(TICKET_DETAILS, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            Log.d("DB", "true");
            return true;
        }
    }*/

    public void againCreate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY , " +
                " P_NAME TEXT , P_PRICE TEXT , P_DATE TEXT)");
        db.close();
    }

   /* public void againCreateDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE " + TICKET_DETAILS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TICKETS_DETAILS_ID INTEGER , TICKET_TYPE TEXT , NUMBER_OF_TICKETS INTEGER, BARCODE INTEGER)");
        db.close();
    }*/

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
            //Log.i("check",cursor.getString(0) + ";" + cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + "   "+" \n");
            data.add(cursor.getString(0) + ";" + cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + "   "+" \n");
            //builder.append(cursor.getString(0) + ";" + cursor.getString(1) + ";" + cursor.getString(2) + ";" + cursor.getString(3) + "   "+" \n");
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
                try {

                    OutputStreamWriter outStreamWriter = null;
                    FileOutputStream outStream = null;
                    outStream = new FileOutputStream(newfile,true) ;
                    outStreamWriter = new OutputStreamWriter(outStream);
                    for(String d: data){
                        outStreamWriter.append(d);
                    }
                    outStreamWriter.flush();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public void displayInfo(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(" ID: " + cursor.getString(0) + " \n " + "SOLD_ID: " + cursor.getString(1) + " \n " + "EVENT_ID: " + cursor.getString(2) + " \n " + "AMOUNT: " + cursor.getString(3) + " \n " +
                    "NAME: " + cursor.getString(4) + " \n " + "BARCODE: " + cursor.getString(5) + " \n " + "EMAIL: " + cursor.getString(6) + " \n " + "PHONE: " + cursor.getString(7) +
                    " \n " + "PAYMENT TYPE: " + cursor.getString(8) + " \n " + "SCANNED: " + cursor.getString(9) + " \n " + "SCANNED_DATE: " + cursor.getString(10) + " \n \n \n");
        }
    }

    public void amount(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT AMOUNT FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "\n");
        }
    }

    public void getName(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT NAME FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "\n");
        }
    }

    public void getEmail(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT EMAIL FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "\n");
        }
    }

    public void getPhone(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT PHONE FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "\n");
        }
    }

    public void getPaymentType(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT PAYMENT_TYPE FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "\n");
        }
    }

    public void getScanDate(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT DATE_SCANNED FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "\n");
        }
    }

    public void getBar(TextView textView, String code) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT BARCODE FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "\n");
        }
    }

    public String getQRCode(String code) {
        String c="";
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT DISTINCT BARCODE FROM " + TABLE_NAME + " WHERE BARCODE= '" + code + "'", null);
        while (cursor.moveToNext()) {
            c= cursor.getString(0) ;
        }
        return c;
    }*/

    public void getData() {

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
        //textView.setText("");
        while (cursor.moveToNext()) {
            Log.i("database data",cursor.getString(0) + "   " + cursor.getString(1) + "    " + cursor.getString(2) + "   " + cursor.getString(3) + "   ");
//            textView.append(cursor.getString(0) + "   " + cursor.getString(1) + "    " + cursor.getString(2) + "   " + cursor.getString(3) + "   " +
//                    "   " + cursor.getString(4) + "    " + cursor.getString(5) + "   " + cursor.getString(6) + "   " + cursor.getString(7) +
//                    "    " + cursor.getString(8) + " " + cursor.getString(9) + " " + cursor.getString(10) + "  " + cursor.getString(11) + "  " + cursor.getString(12) + "\n");
        }
    }

    /*
    public void getDetailData(TextView textView) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TICKET_DETAILS, null);
        textView.setText("");
        while (cursor.moveToNext()) {
            textView.append(cursor.getString(0) + "    " + cursor.getString(1) + "  " + cursor.getString(2) + "   " + cursor.getString(3) + "    " + cursor.getString(4) + "\n");
        }
    }

    public void updateStatus(String bool, String dateTime, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET SCANNED ='" + bool + "', DATE_SCANNED ='" + dateTime + "' WHERE BARCODE ='" + code + "'");
        db.close();
        Log.i("SCAN db helper", bool+"--"+code);
    }

    public void updatedOnServer(String soldId){
    SQLiteDatabase db = this.getWritableDatabase();
    String status = "Yes";
    db.execSQL("UPDATE " + TABLE_NAME + " SET UPDATEONSERVER ='" + status + "' WHERE SOLD_ID ='" + soldId + "'");
    db.close();
    }

    public Cursor updateOnServer() {
        SQLiteDatabase db = this.getWritableDatabase();
        String scanned = "Yes";
        String updateOnServer = "No";
        Cursor statusOnServer = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL9 + " = '" + scanned + "' AND UPDATEONSERVER = '" + updateOnServer + "' LIMIT 1", null);
        return  statusOnServer;
    }

    public Cursor scannedTickets(){
        SQLiteDatabase db = this.getWritableDatabase();
        String scanned = "Yes";
        Cursor scannedTickets = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL16 + " = '" + scanned + "'", null);
        return scannedTickets;
    }

    public Cursor totalScannedTick(){
        SQLiteDatabase db = this.getWritableDatabase();
        String scanned = "Yes";
        Cursor scannedTickets = db.rawQuery("SELECT TOTAL_NUMBER FROM " + TABLE_NAME + " WHERE " + COL16 + " = '" + scanned + "'", null);
        return scannedTickets;
    }

    public Boolean alreadyYes(String barcode){
        String scanned = "Yes";
        //scanned.equalsIgnoreCase("yes");
        Log.i("SCAN db helper", scanned+"--"+barcode);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor alreadyScanned = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL15 + " = '" + barcode + "' AND UPDATEONSERVER = '" + scanned + "'", null);
        if (alreadyScanned.moveToFirst()) {
            return true;
        }
        return false;
    }

    public Cursor getName(String barcode){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor name = db.rawQuery("SELECT NAME FROM " + TABLE_NAME + " WHERE " + COL15 + " = '" + barcode + "'", null);
        return name;
    }

    public Cursor getSoldId(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor soldId = db.rawQuery("SELECT SOLD_ID FROM " + TABLE_NAME , null);
        soldId.moveToLast();
        return soldId;
    }

    public Cursor getSoldId1(String barcode){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor soldId = db.rawQuery("SELECT SOLD_ID FROM " + TABLE_NAME+ " WHERE " + COL15 + " = '" + barcode + "'", null);
        soldId.moveToLast();
        return soldId;
    }

    public Cursor geteventId(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor soldId = db.rawQuery("SELECT EVENT_ID FROM " + TABLE_NAME , null);
        soldId.moveToLast();
        return soldId;
    }

    public int getTableCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getCount = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        getCount.moveToLast();
        return getCount.getCount();
    }

    public Cursor allTickets(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor allTickets = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        return allTickets;
    }

    public Cursor numberOf(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor allTickets = db.rawQuery("SELECT TOTAL_NUMBER FROM " + TABLE_NAME , null);
        return allTickets;
    }*/
}