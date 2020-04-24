package com.example.todo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todo.dbt.ToDo
import com.example.todo.dbt.ToDoItem

class DBHandler(val context: Context) :SQLiteOpenHelper (context, DB_NAME,null, DB_VERSION){

    val createToDoItemTable =
        "CREATE TABLE $TABLE_TODO_ITEM (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_TODO_ID integer," +
                "$COL_NAME varchar," +
                "$COL_IS_COMPLETED integer);"

    val createToDoTable = "CREATE TABLE $TABLE_TODO (" +
            "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
            "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
            "$COL_NAME varchar);"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createToDoTable)
        db.execSQL(createToDoItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropToDoItemTable = "DROP TABLE $TABLE_TODO_ITEM"
        val dropToDoTable = "DROP TABLE $TABLE_TODO"
        db.execSQL(dropToDoItemTable)
        db.execSQL(dropToDoTable)
        db.execSQL(createToDoTable)
        db.execSQL(createToDoItemTable)
    }

    fun addToDo(toDo : ToDo) : Boolean{
        var db=writableDatabase
        var cv = ContentValues()
        cv.put(COL_NAME,toDo.name)
        val result = db.insert(TABLE_TODO,null,cv)
        return result!=(-1).toLong()
    }

    fun updateToDo(toDo: ToDo) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, toDo.name)
        db.update(TABLE_TODO,cv,"$COL_ID=?" , arrayOf(toDo.id.toString()))
    }

    fun deleteToDo(toDoId: Long){
        val db =writableDatabase
        db.delete(TABLE_TODO_ITEM, "$COL_TODO_ID=?", arrayOf(toDoId.toString()))
        db.delete(TABLE_TODO,"$COL_ID=?", arrayOf(toDoId.toString()))
    }

    fun updateToDoItemCompletedStatus(toDoId: Long,isComplated: Boolean){
        val db =writableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$toDoId",null)
        if(queryResult.moveToFirst()){
            do{
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId =queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                item.isCompleted = isComplated
                updateToDoItem(item)
            }while(queryResult.moveToNext())
        }
    }

    fun getToDos() :MutableList<ToDo>{
        val result : MutableList<ToDo> =ArrayList()
        val db =readableDatabase
        val queryResult =db.rawQuery("SELECT * FROM $TABLE_TODO",null)
        if(queryResult.moveToFirst()){
            do{
                val todo =ToDo()
                todo.id =queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                todo.name=queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                result.add(todo)
            }while(queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }
    
    fun addToDoItem(item : ToDoItem):Boolean{
        var db=writableDatabase
        var cv = ContentValues()
        cv.put(COL_NAME,item.name)
        cv.put(COL_TODO_ID,item.toDoId)
        cv.put(COL_IS_COMPLETED,item.isCompleted)

        val result = db.insert(TABLE_TODO_ITEM,null,cv)
        return result!=(-1).toLong()
    }

    fun updateToDoItem(item: ToDoItem) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, item.name)
        cv.put(COL_TODO_ID, item.toDoId)
        cv.put(COL_IS_COMPLETED, item.isCompleted)

        db.update(TABLE_TODO_ITEM, cv, "$COL_ID=?", arrayOf(item.id.toString()))
    }

    fun deleteToDoItem(itemId : Long){
        val db = writableDatabase
        db.delete(TABLE_TODO_ITEM,"$COL_ID=?" , arrayOf(itemId.toString()))
    }

    fun getToDoItems(toDoId : Long) :MutableList<ToDoItem>{
        val result : MutableList<ToDoItem> = ArrayList()
        val db = readableDatabase
        val queryResult =db.rawQuery("SELECT * FROM $TABLE_TODO_ITEM WHERE $COL_TODO_ID=$toDoId",null)
        if(queryResult.moveToFirst()){
            do{
                val item = ToDoItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.toDoId =queryResult.getLong(queryResult.getColumnIndex(COL_TODO_ID))
                item.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                item.isCompleted = queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETED)) == 1
                result.add(item)
            }while(queryResult.moveToNext())
        }
        queryResult.close()
        return  result
    }
}
