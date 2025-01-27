package com.netcore.smarttechdemo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class  DbHelper(context: Context) : SQLiteOpenHelper(context, "User.db", null, 1) {

    //Create Table
    private val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_USER + "(" + COLUMN_USER_ID + " INTEGER_PRIMARY_KEY_AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")")


    //Drop Table
    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_USER"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_USER_TABLE)
        onCreate(db)
    }

    //Create user
    fun addUser(user: User) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_PASSWORD, user.password)

        db.insert(TABLE_USER, null, values)
        db.close()
    }

    //Update user
    fun updateUser(user: User) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_PASSWORD, user.password)

        db.update(TABLE_USER, values, "$COLUMN_USER_ID = ?", arrayOf(user.id.toString()))
        db.close()
    }

    //Delete user
    fun deleteUser(user: User) {
        val db = this.writableDatabase

        db.delete(TABLE_USER, "$COLUMN_USER_ID = ?", arrayOf(user.id.toString()))
        db.close()
    }

    //Check user exists
    fun regCheckUser(name: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase

        val selection = "$COLUMN_USER_NAME = ?"

        val selectionArgs = arrayOf(name)

        val cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null)

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount == 0) {
            return true
        }

        return false
    }

    fun logCheckUser(name: String, password: String): Boolean {
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.readableDatabase

        val selection = "$COLUMN_USER_NAME = ? AND $COLUMN_USER_PASSWORD = ?"

        val selectionArgs = arrayOf(name, password)

        val cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null)

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }

    companion object {
        private val TABLE_USER = "user"
        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_NAME = "user_name"
        private val COLUMN_USER_PASSWORD = "user_password"
    }

}
