package com.example.roomapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Subscriber::class], version = 1)
abstract class SubsciberDatabase : RoomDatabase() {
    abstract val subscriberDAO: SubscriberDAO

    companion object {
        @Volatile
        private var INSTANCE: SubsciberDatabase? = null
        fun getInstance(context: Context): SubsciberDatabase {
            synchronized( this)
            {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SubsciberDatabase::class.java,
                        "subscriber_data_table"
                    ).build()
                }
                return instance
            }
        }
    }
}