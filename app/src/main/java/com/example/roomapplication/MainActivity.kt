package com.example.roomapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomapplication.databinding.ActivityMainBinding
import com.example.roomapplication.db.SubsciberDatabase
import com.example.roomapplication.db.Subscriber
import com.example.roomapplication.db.SubscriberRepository

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewmodel: SubscriberViewmodel
    private lateinit var adapter: MyRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = SubsciberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewmodel = ViewModelProvider(this, factory).get(SubscriberViewmodel::class.java)
        binding.myViewModel = subscriberViewmodel
        binding.lifecycleOwner = this
        initRecyclerView()
        subscriberViewmodel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initRecyclerView() {
        binding.subscriberRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter =
            MyRecyclerViewAdapter({ selectedItem: Subscriber -> listItemClicked(selectedItem)})
        binding.subscriberRecyclerview.adapter = adapter
        displaySubscribersList()
    }

    private fun displaySubscribersList() {
        subscriberViewmodel.subscribers.observe(this, Observer {
            Log.i("MyTag", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(subscriber: Subscriber) {
        // Toast.makeText(this,"Selected name is ${subscriber.name}",Toast.LENGTH_LONG).show()
        subscriberViewmodel.initUpdateAndDelete(subscriber)
    }
}
