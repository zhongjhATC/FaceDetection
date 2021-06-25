/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.idl.face.main.finance.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库创建工具
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_START_SQL = "CREATE TABLE IF NOT EXISTS ";
    private static final String CREATE_TABLE_PRIMIRY_SQL = " integer primary key autoincrement,";

    /**
     * 数据库名称
     */
    private static final String DB_NAME = "face.db";
    /**
     * 数据库版本
     */
    private static final int VERSION = 1;
    /**
     * 人脸特征表
     */
    // public static final String TABLE_FEATURE = "feature";
    /**
     * 用户组表
     */
    public static final String TABLE_USER_GROUP = "user_group";
    /**
     * 用户表
     */
    public static final String TABLE_USER = "user";

    /**
     * 识别记录表
     */
    public static final String TABLE_RECORDS = "records";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_GROUP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
            onCreate(db);
        }
    }

    public synchronized void createTables(SQLiteDatabase db) {
        if (db == null || db.isReadOnly()) {
            db = getWritableDatabase();
        }

        // 创建人脸特征表的SQL语句
//        StringBuffer featureSql = new StringBuffer();
//        featureSql.append(CREATE_TABLE_START_SQL).append(TABLE_FEATURE).append(" ( ");
//        featureSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
//        featureSql.append(" face_token").append(" varchar(128) default \"\" ,");
//        featureSql.append(" group_id").append(" varchar(32) default \"\" ,");
//        featureSql.append(" user_id").append(" varchar(32) default \"\" ,");
//        featureSql.append(" feature").append(" blob   ,");
//        featureSql.append(" image_name").append(" varchar(64) default \"\"  ,");
//        featureSql.append(" ctime").append(" long ,");
//        featureSql.append(" update_time").append(" long )");

        // 创建用户组表的SQL语句
        StringBuffer groupSql = new StringBuffer();
        groupSql.append(CREATE_TABLE_START_SQL).append(TABLE_USER_GROUP).append(" ( ");
        groupSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
        groupSql.append(" group_id").append(" varchar(32) default \"\" ,");
        groupSql.append(" desc").append(" varchar(32) default \"\"  ,");
        groupSql.append(" ctime").append(" long ,");
        groupSql.append(" update_time").append(" long )");

        // 创建用户表的SQL语句
        StringBuffer userSql = new StringBuffer();
        userSql.append(CREATE_TABLE_START_SQL).append(TABLE_USER).append(" ( ");
        userSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
        userSql.append(" user_id").append(" varchar(32) default \"\"   ,");
        userSql.append(" user_name").append(" varchar(32) default \"\"   ,");
        userSql.append(" user_info").append(" varchar(32) default \"\"   ,");
        userSql.append(" group_id").append(" varchar(32) default \"\"   ,");
        userSql.append(" face_token").append(" varchar(128) default \"\" ,");
        userSql.append(" feature").append(" blob   ,");
        userSql.append(" image_name").append(" varchar(64) default \"\"  ,");
        userSql.append(" ctime").append(" long ,");
        userSql.append(" update_time").append(" long )");

        // 创建识别记录的SQL语句
        StringBuffer recordSql = new StringBuffer();
        recordSql.append(CREATE_TABLE_START_SQL).append(TABLE_RECORDS).append(" ( ");
        recordSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
        recordSql.append(" deviceid").append(" varchar(32) default \"\"   ,");
        recordSql.append(" user_id").append(" varchar(32) default \"\"   ,");
        recordSql.append(" user_name").append(" varchar(32) default \"\"   ,");
        recordSql.append(" group_id").append(" varchar(32) default \"\"   ,");
        recordSql.append(" face_token").append(" varchar(128) default \"\" ,");
        recordSql.append(" time").append(" datetime  ,");
        recordSql.append(" records").append(" varchar(32) default \"\"   ,");
        recordSql.append(" longId").append(" varchar(32) default \"\"   ,");
        recordSql.append(" score").append(" varchar(32) default \"\"   )");

        try {
            db.execSQL(groupSql.toString());
            db.execSQL(userSql.toString());
            db.execSQL(recordSql.toString());
            // db.execSQL(featureSql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
