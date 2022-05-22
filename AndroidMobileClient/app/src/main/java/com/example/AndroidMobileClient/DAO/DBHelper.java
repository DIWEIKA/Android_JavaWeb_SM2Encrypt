package com.example.AndroidMobileClient.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * 数据库操作帮助类，继承 Android 自带的 SQLiteOpenHelper
 */
public class DBHelper extends SQLiteOpenHelper {

    //创建数据库
    public static final String Create_KeyPair="create table KeyPairTable("
            +"id integer primary key autoincrement,"
            +"IDA text,"
            +"keyPair text)";

    public Context mContext;

    @Override
    public void onCreate(SQLiteDatabase db) {

        //执行sql语句
        db.execSQL(Create_KeyPair);
        //弹窗提示创建成功
        Toast.makeText(mContext,"Create succeed",Toast.LENGTH_SHORT).show();
    }
    //带有全部参数的构造函数，此构造函数是必须需要的。Eclipse和Android Studio均有自动填充功能
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
