package com.android.base.file;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gg on 2017/4/3.
 * SQLite管理类
 */
public class SQLiteUtils {

    private SQLiteDatabase database;

    /**
     * @param helper 构造函数(context, "dbName.db", null, 1)
     *               onCreate()数据库创建时，才会调用,db.execSQL("创建数据库的语句")
     *               onUpgrade()数据库更新时，才会调用
     */
    public SQLiteUtils(SQLiteOpenHelper helper) {
        // 如果数据库不存在，则先创建，再打开
        // 如果数据库存在，直接打开
        // 数据库空间不足，直接返回只读的数据库
        database = helper.getWritableDatabase();
    }

    /**
     * 记得关闭数据库连接
     */
    public void closeDatabase() {
        database.close();
    }

    /**
     * 开启事务,在执行 增删改查 前 调用
     */
    public void beginTransaction() {
        database.beginTransaction();
    }

    /**
     * 设置事务执行成功，在执行 增删改查 后 调用
     */
    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    /**
     * 关闭事务，同时commit,一般在finally里执行
     * 如果已经成功设置事务执行成功，那么操作执行成功 , 反之，sql语句回滚
     */
    public void endTransaction() {
        database.endTransaction();
    }

    /**
     * @return 成功则返回行id ???，失败返回-1
     */
    public long insert(String table, ContentValues values) {
        return database.insert(table, null, values);
    }

    /**
     * @param whereClause 就是where后面的条件语句
     * @param whereArgs   填充占位符
     * @return 受影响的行数
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        return database.delete(table, whereClause, whereArgs);
    }

    /**
     * @param values      新数据
     * @param whereClause 条件 (example：当字段 = 占位符)
     * @param whereArgs   填充占位符
     * @return 受影响的行数
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return database.update(table, values, whereClause, whereArgs);
    }

    /**
     * @param distinct      消重
     * @param columns       查询的字段
     * @param selection     查询语句
     * @param selectionArgs 填充占位符
     * @param groupBy       分组
     * @param having        分组整理
     * @param orderBy       排序
     * @param limit         分页查询
     * @return 记得关闭游标
     */
    public Cursor select(String table, boolean distinct, String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

        return database.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * @param sql      insert into 表名 (字段，字段) values (？，？)
     *                 delete from 表名 where 字段 = ？
     *                 update 表名 set 字段 = ？ where 字段 = ?
     * @param bindArgs 填充占位符的数组
     */
    public void execSQL(String sql, Object[] bindArgs) {
        if (bindArgs == null || bindArgs.length == 0)
            database.execSQL(sql);
        else
            database.execSQL(sql, bindArgs);
    }

    /**
     * @param sql           查询语句
     * @param selectionArgs 填充占位符
     * @return 记得关闭游标
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return database.rawQuery(sql, selectionArgs);
    }

    /**
     * @return 字段长度
     */
    public int getColumnCount(Cursor cursor) {
        return cursor.getColumnCount();
    }

    /**
     * @return 字段名数组
     */
    public String[] getColumnNames(Cursor cursor) {
        return cursor.getColumnNames();
    }

    /**
     * @return 用while来判断
     */
    public boolean cursorNext(Cursor cursor) {
        return cursor.moveToNext();
    }

    /**
     * @return 获取columnName字段的String值
     */
    public String getCursorString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    /**
     * @return 获取索引字段的String值
     */
    public String getCursorString(Cursor cursor, int columnIndex) {
        return cursor.getString(columnIndex);
    }

    /**
     * @return 获取columnName字段的int值
     */
    public int getCursorInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public int getCursorInt(Cursor cursor, int columnIndex) {
        return cursor.getInt(columnIndex);
    }

    /**
     * @return 获取columnName字段的long值
     */
    public long getCursorLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

    public long getCursorLong(Cursor cursor, int columnIndex) {
        return cursor.getLong(columnIndex);
    }

    /**
     * @return 获取columnName字段的float值
     */
    public float getCursorFloat(Cursor cursor, String columnName) {
        return cursor.getFloat(cursor.getColumnIndex(columnName));
    }

    public float getCursorFloat(Cursor cursor, int columnIndex) {
        return cursor.getFloat(columnIndex);
    }

    /**
     * @return 获取columnName字段的double值
     */
    public double getCursorDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndex(columnName));
    }

    public double getCursorDouble(Cursor cursor, int columnIndex) {
        return cursor.getDouble(columnIndex);
    }
}
