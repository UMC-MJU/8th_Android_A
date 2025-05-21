package com.example.flo_clone.ui.look

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LookViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is look Fragment"
    }
    val text: LiveData<String> = _text
}