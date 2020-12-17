package com.coroutinesample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class StepFour : AppCompatActivity() {

    /** what is different between CoroutineScope(Main).launch and runBlocking */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step_four)

        //here we come and show results
        CoroutineScope(Main).launch {
            println("result 1:" + getResult())
            println("result 2:" + getResult())
            println("result 3:" + getResult())
            println("result 4:" + getResult())
            println("result 5:" + getResult())
        }

        //if we use this, it come and block all job
        // and do it self jobs and after the the thread will be free for others
        CoroutineScope(Main).launch {
            delay(2000)
            runBlocking {
                println("Blocking thread ${Thread.currentThread().name}")
                delay(4000)
                println("Done blocking the thread")
            }
        }

    }

    private suspend fun getResult(): Int {
        delay(1000)
        return Random.nextInt(0, 100)
    }
}