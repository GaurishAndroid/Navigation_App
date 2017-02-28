package com.example.abhisheki.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by ABHISHEKI on 31-01-2017.
 */

public class MyDbHandler extends SQLiteOpenHelper {

    private static final int     DATABASE_VERSION  = 1;
    private static final String  DATABASE_NAME     = "routes.db";
    private static final String  TABLE_NAME        = "route";
    private static final String  COLUMN_ID         = "Id";
    private static final String  COLUMN_Origin     = "Origin";
    private static final String  COLUMN_Destination     = "Destination";
    private static final String  COLUMN_OriginLat       = "OriginLat";
    private static final String  COLUMN_OriginLong      = "OriginLong";
    private static final String  COLUMN_DestLat         = "DestLat";
    private static final String  COLUMN_DestLong        = "DestLong";
    private static final String  COLUMN_Route           = "Route";


    public MyDbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COLUMN_Origin + " TEXT, "
                + COLUMN_Destination + " TEXT,"
                + COLUMN_OriginLat   + " REAL,"
                + COLUMN_OriginLong  + " REAL,"
                + COLUMN_DestLat     + " REAL,"
                + COLUMN_DestLong    + " REAL,"
                + COLUMN_Route       + " TEXT"
                + " ) ";

        db.execSQL(table1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void AddRoute(Route route)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_Origin,route.getOrigin());
        values.put(COLUMN_Destination,route.getDestination());
        values.put(COLUMN_OriginLat,route.getOriginLat());
        values.put(COLUMN_OriginLong,route.getOriginLong());
        values.put(COLUMN_DestLat,route.getDestLat());
        values.put(COLUMN_DestLong,route.getDestLong());
        values.put(COLUMN_Route,route.getRoute());
        SQLiteDatabase db= getWritableDatabase();
        db.insert(TABLE_NAME,null,values);
        db.close();
    }
    public Route getRouteInfo(Context v,String Id)
    {
        String dbString = "";
        try {

            SQLiteDatabase db = getWritableDatabase();
            String Query = "SELECT * FROM " + TABLE_NAME + " WHERE ID = " + Id;
           // Toast.makeText(v, "dbtostr", Toast.LENGTH_SHORT).show();
            Route rt = new Route();

            Cursor c = db.rawQuery(Query, null);
            c.moveToFirst();
            do
            {
                rt.setOrigin(c.getString(1));

                rt.setDestination(c.getString(2));

                rt.setOriginLat(Double.parseDouble(c.getString(3)));

                rt.setOriginLong(Double.parseDouble(c.getString(4)));

                rt.setDestLat(Double.parseDouble(c.getString(5)));

                rt.setDestLong(Double.parseDouble(c.getString(6)));

                rt.setRoute(c.getString(7));


            }while(c.moveToNext());
            return rt;

        }
        catch(Exception exp)
        {
            Toast.makeText(v, exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    public String[] GetOfflineRoutes(Context v)
    {
        String[] dbString = null;
        try {

            SQLiteDatabase db = getWritableDatabase();
            String Query = "SELECT * FROM " + TABLE_NAME + " WHERE 1";


            Cursor c = db.rawQuery(Query, null);
            dbString =  new String[c.getCount()];
            int i=0;
            c.moveToFirst();
            do
            {
                dbString[i++] =  c.getString(0) + "] "  + c.getString(1) + " - "  + c.getString(2) ;

            }while(c.moveToNext());
            return dbString;

        }
        catch(Exception exp)
        {
            Toast.makeText(v, exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return dbString;
    }
}
