package com.alumnus.zebra.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Coroutines {

    // Higher order fun lambda in kotlin
    fun main(work :suspend (()->Unit)){
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }
    }

    fun IO(work :suspend (()->Unit)){
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }
    }

    fun Default(work :suspend (()->Unit)){
        CoroutineScope(Dispatchers.Default).launch {
            work()
        }
    }
}