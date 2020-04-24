package com.example.todo

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.dbt.ToDoItem
import kotlinx.android.synthetic.main.activity_item.*
import java.util.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler : DBHandler
    var toDoId : Long = -1

    var list: MutableList<ToDoItem>? = null
    var adapter : ItemAdapter? = null
    var touchHelper : ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_TODO_NAME)

        toDoId = intent.getLongExtra(INTENT_TODO_ID,-1)
        dbHandler =DBHandler(this)

        rv_item.layoutManager = LinearLayoutManager(this)

        fab_item.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add ToDo")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard,null)
            val itemName =view.findViewById<EditText>(R.id.ev_todo)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int->
                if(itemName.text.isNotEmpty()){
                    val item = ToDoItem()
                    item.name = itemName.text.toString()
                    item.toDoId = toDoId
                    item.isCompleted = false
                    dbHandler.addToDoItem(item)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel"){
                    _: DialogInterface, _: Int ->
            }
            dialog.show()
        }
        touchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    val sourcePosition = p1.adapterPosition
                    val targetPosition = p2.adapterPosition
                    Collections.swap(list,sourcePosition,targetPosition)
                    adapter?.notifyItemMoved(sourcePosition,targetPosition)
                    return true
                }

                override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        touchHelper?.attachToRecyclerView(rv_item)
    }

    fun updateItem(item : ToDoItem){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard,null)
        val itemName =view.findViewById<EditText>(R.id.ev_todo)
        itemName.setText(item.name)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int->
            if(itemName.text.isNotEmpty()){
                val item = ToDoItem()
                item.name = item.name
                item.toDoId = item.toDoId
                item.isCompleted = item.isCompleted
                dbHandler.addToDoItem(item)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel"){
                _: DialogInterface, _: Int ->
        }
        dialog.show()
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList(){
        rv_item.adapter = ItemActivity.ItemAdapter(this, dbHandler.getToDoItems(toDoId))
    }

    class ItemAdapter(val activity: ItemActivity, val list: MutableList<ToDoItem>):
        RecyclerView.Adapter<ItemAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity).inflate(
                R.layout.rv_child_item,parent,false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemName.text =list[position].name
            holder.itemName.isChecked = list[position].isCompleted
            holder.itemName.setOnClickListener {
                list[position].isCompleted = !list[position].isCompleted
                activity.dbHandler.updateToDoItem(list[position])
            }
            holder.delete.setOnClickListener{
                var dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete item ?")
                dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int->
                    activity.dbHandler.deleteToDoItem(list[position].id)
                    activity.refreshList()
                }
                dialog.setNegativeButton("Cancel"){ _: DialogInterface, _: Int->}
                dialog.show()
            }
            holder.edit.setOnClickListener {
                activity.updateItem(list[position])
            }
            holder.move.setOnTouchListener { v, event ->
                if(event.actionMasked== MotionEvent.ACTION_DOWN){
                    activity.touchHelper?.startDrag(holder)
                }
                false
            }
        }

        class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
            val itemName : CheckBox = v.findViewById(R.id.cb_item)
            val edit :ImageView  = v.findViewById(R.id.iv_edit)
            val delete :ImageView  = v.findViewById(R.id.iv_delete)
            val move :ImageView  = v.findViewById(R.id.iv_move)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }

}
