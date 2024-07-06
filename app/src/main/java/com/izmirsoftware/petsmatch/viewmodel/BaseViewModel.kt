package com.izmirsoftware.petsmatch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izmirsoftware.petsmatch.util.Resource

open class BaseViewModel : ViewModel() {
    protected val <T> LiveData<T>.mutable: MutableLiveData<T>
        get() = this as MutableLiveData<T>

    val liveDataStatus: LiveData<Resource<Boolean>> = MutableLiveData()
}