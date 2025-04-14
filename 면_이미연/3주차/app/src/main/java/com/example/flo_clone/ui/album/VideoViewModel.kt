package com.example.flo_clone.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is video Fragment"
    }
    val text: LiveData<String> = _text
}