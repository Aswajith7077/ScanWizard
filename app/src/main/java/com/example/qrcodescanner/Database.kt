package com.example.qrcodescanner

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context): SQLiteOpenHelper(context,DBName,null,DBVersion) {



    companion object{
        val DBName = "ScanWizard.db"
        val DBVersion = 1

        val tableName1 = "Recent"
        val tableName2 = "Arguments"

        val column1Table1 = "Id"
        val column2Table1 = "Type"
        val column3Table1 = "IsFavorite"
        val column4Table1 = "Date"

        val column1Table2 = "Id"
        val column2Table2 = "Args"

    }




    override fun onCreate(db: SQLiteDatabase?) {
        val createTable1 = "CREATE TABLE $tableName1(" +
                "$column1Table1 INTEGER PRIMARY KEY," +
                "$column2Table1 INTEGER NOT NULL," +
                "$column3Table1 VARCHAR(6) NOT NULL," +
                "$column4Table1 VARCHAR(15) NOT NULL" +
                ");"

        val createTable2 = "CREATE TABLE $tableName2("+
            "$column1Table1 INTEGER PRIMARY KEY,"+
            "$column2Table2 VARCHAR(100)"+
            ");"
        db?.execSQL(createTable1)
        db?.execSQL(createTable2)


    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion);
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val query1 = "DROP TABLE $tableName1"
        val query2 = "DROP TABLE $tableName2"

        db.execSQL(query1)
        db.execSQL(query2)

        onCreate(db)

    }


    @SuppressLint("Recycle", "Range")
    fun getData():MutableList<RecentElements>{
        val mutableList:MutableList<RecentElements> = mutableListOf()

        val db = writableDatabase
        val query1 = "SELECT * FROM ${Database.tableName1}"
        val query2 = "SELECT * FROM ${Database.tableName2}"

        val cursor1 = db.rawQuery(query1,null)
        val cursor2 = db.rawQuery(query2,null)



        if(cursor1!=null){
            if(cursor1.moveToFirst()){
                do {
                    val recentElements = RecentElements()
                    recentElements.id = cursor1.getString(cursor1.getColumnIndex(Database.column1Table1)).toInt()
                    recentElements.type = cursor1.getString(cursor1.getColumnIndex(Database.column2Table1)).toInt()
                    recentElements.isFavorite =
                        cursor1.getString(cursor1.getColumnIndex(Database.column3Table1)).toBoolean()
                    recentElements.date = cursor1.getString(cursor1.getColumnIndex(Database.column4Table1))

                    mutableList.add(recentElements)
                }while(cursor1.moveToNext())
                cursor1.close()
            }
        }

        if(cursor2!=null){
            if(cursor2.moveToFirst()){
                do{
                    val id = cursor2.getString(cursor2.getColumnIndex(Database.column1Table2)).toInt()
                    val argument:String = cursor2.getString(cursor2.getColumnIndex(Database.column2Table2))
                    for(i in mutableList.indices){
                        if(mutableList[i].id == id && mutableList[i].arguments==null)
                            mutableList[i].arguments = arrayListOf(argument)
                        else if(mutableList[i].id == id)
                            mutableList[i].arguments?.add(argument)
                    }
                }while(cursor2.moveToNext())
                cursor2.close()
            }
        }

        return mutableList
    }

    fun clearData(){
        val query1 = "DELETE FROM ${Database.tableName1};"
        val query2 = "DELETE FROM ${Database.tableName2};"

        val db = writableDatabase

        db.execSQL(query1)
        db.execSQL(query2)
    }

    fun addData(recentElement:RecentElements):Boolean{
        val db = writableDatabase
        val values1 = ContentValues()
        values1.put(Database.column1Table1,recentElement.id)
        values1.put(Database.column2Table1,recentElement.type)
        values1.put(Database.column3Table1,recentElement.isFavorite)
        values1.put(Database.column4Table1,recentElement.date)

        val values2 = ContentValues()
        for(i in recentElement.arguments!!.indices){
            values2.put(Database.column1Table2,recentElement.id)
            values2.put(Database.column2Table2, recentElement.arguments!![i])
        }

        val result1 = db.insert(Database.tableName1,null,values1)
        val result2 = db.insert(Database.tableName2,null,values2)

        return ((result1.toInt()!=-1) && (result2.toInt()!=-1))
    }



}


