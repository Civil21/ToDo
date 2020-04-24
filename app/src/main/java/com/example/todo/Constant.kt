package com.example.todo

const val DB_NAME = "ToDo_db"
const val DB_VERSION = 2

const val TABLE_TODO = "ToDos"
const val COL_ID = "id"
const val COL_CREATED_AT = "createdAt"
const val COL_NAME = "name"

const val TABLE_TODO_ITEM = "ToDoItems"
//const val COL_ID = "id"
const val COL_TODO_ID = "toDoId"
//const val COL_NAME = "name"
const val COL_IS_COMPLETED = "isCompleted"

const val INTENT_TODO_ID = "TodoId"
const val INTENT_TODO_NAME = "TodoName"