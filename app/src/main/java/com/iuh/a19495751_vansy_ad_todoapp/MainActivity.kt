package com.iuh.a19495751_vansy_ad_todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() , UpdateAndDelete {
    lateinit var  database : DatabaseReference
    var todoList : MutableList<ToDoModel>? = null
    lateinit var adapter : ToDoAdapter
    private var listViewItem : ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        listViewItem = findViewById<ListView>(R.id.item_listView)

        database = FirebaseDatabase.getInstance().reference

        fab.setOnClickListener{view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setTitle("Add New Task")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Save"){ dialog, i ->
                val todoItemData = ToDoModel.createList()
                todoItemData.itemDataText = textEditText.text.toString()
                todoItemData.done = false

                val newItemData = database.child("todo").push()
                todoItemData.UID = newItemData.key

                newItemData.setValue(todoItemData)
                dialog.dismiss()
                Toast.makeText(this, "item saved" , Toast.LENGTH_LONG).show()
            }
            alertDialog.show()
        }


        todoList = mutableListOf<ToDoModel>()
        adapter = ToDoAdapter(this, todoList!!)
        listViewItem!!.adapter = adapter
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext , "No item Added" , Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                todoList!!.clear()
                addItemToList(snapshot)
            }
        })

    }

    private fun addItemToList(snapshot: DataSnapshot) {
        val items = snapshot.children.iterator()
        if(items.hasNext()){
            val todoIndexdvalue = items.next()
            val itemsIterator = todoIndexdvalue.children.iterator()
            while(itemsIterator.hasNext()){
                val currentItem = itemsIterator.next()
                val todoItemData = ToDoModel.createList()
                val map = currentItem.getValue() as HashMap<String , Any>
                todoItemData.UID = currentItem.key
                todoItemData.done = map.get("done") as Boolean?
                todoItemData.itemDataText = map.get("itemDataText") as String?
                todoList!!.add(todoItemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemUID: String, isDone: Boolean) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemUID: String) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}