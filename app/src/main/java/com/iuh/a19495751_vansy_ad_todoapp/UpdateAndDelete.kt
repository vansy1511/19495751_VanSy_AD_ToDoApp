package com.iuh.a19495751_vansy_ad_todoapp

interface UpdateAndDelete{
    fun modifyItem(itemUID : String , isDone : Boolean)
    fun onItemDelete(itemUID : String)
}