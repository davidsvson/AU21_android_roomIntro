package com.example.au21roomintro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() , CoroutineScope {

    private lateinit var job : Job
    private lateinit var db : AppDatabase
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        db = Room.databaseBuilder(applicationContext,
                            AppDatabase::class.java,
                            "shopping-items")
            .fallbackToDestructiveMigration()
            .build()

        val item1 = Item(0, "banan", false, "frukt" )
        val item2 = Item(0, "mjölk", false, "kyl" )
        val item3 = Item(0, "ost", false, "kyl" )
/*
        saveItem(item1)
        saveItem(item2)
        saveItem(item3)
*/
        val list = loadAllItems()
        //val list = loadByCategory("frukt")


        launch {
            val itemsList = list.await()
            //delete(itemsList[0])
            for( item in itemsList) {
                Log.d("!!!", "onCreate: $item")
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    fun saveItem(item : Item) {
        launch(Dispatchers.IO) {
            db.itemDao().insert(item)
        }
    }

    fun loadAllItems() : Deferred<List<Item>> =
        async(Dispatchers.IO) {
            db.itemDao().getAll()
        }

    fun loadByCategory(category: String) =
        async(Dispatchers.IO) {
            db.itemDao().findByCategory(category)
        }

    fun delete(item : Item) =
        launch(Dispatchers.IO) {
            db.itemDao().delete(item)
        }

}