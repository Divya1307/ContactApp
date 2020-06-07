package com.example.roomapplication

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Delete
import com.example.roomapplication.db.Subscriber
import com.example.roomapplication.db.SubscriberRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SubscriberViewmodel(private val repository: SubscriberRepository) : ViewModel(), Observable {
    val subscribers = repository.subscribers
    private var isUpdateorDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val clearOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearOrDeleteButtonText.value = "Clear"
    }

    fun saveOrUpdate() {
        if (inputName.value == null) {
            statusMessage.value = Event("Please enter Subscriber's name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter Subscriber's email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter correct email address")
        } else {
            if (isUpdateorDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email: String = inputEmail.value!!

                insert((Subscriber(0, name, email)))
                inputName.value = null
                inputEmail.value = null
            }
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateorDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    fun insert(subscriber: Subscriber): Job =
        viewModelScope.launch {
            val newRowId: Long = repository.insert(subscriber)
            if (newRowId > -1) {
                statusMessage.value = Event("Subscriber inserted successfully")
            } else {
                statusMessage.value = Event("Error Occured")
            }
        }

    fun update(subscriber: Subscriber): Job =
        viewModelScope.launch {
            val noOfRows: Int = repository.update(subscriber)
            if (noOfRows > 0) {
                inputName.value = null
                inputEmail.value = null
                isUpdateorDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("Subscriber updated successfully")
            } else {
                statusMessage.value = Event("Error Occured")
            }
        }

    fun delete(subscriber: Subscriber): Job =
        viewModelScope.launch {
            val noOfRowsDeleted: Int = repository.delete(subscriber)
            if (noOfRowsDeleted > 0) {
                inputName.value = null
                inputEmail.value = null
                isUpdateorDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("Subscriber deleted successfully")
            } else {
                statusMessage.value = Event("Error Occured")
            }

        }

    fun clearAll(): Job =
        viewModelScope.launch {
            val noOPfAllRowsDeleted: Int = repository.deleteAll()
            if (noOPfAllRowsDeleted > 0) {
                statusMessage.value = Event("All Subscriber deleted successfully")
            } else {
                statusMessage.value = Event("Error Occured")
            }
        }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email

        isUpdateorDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearOrDeleteButtonText.value = "Delete"
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}