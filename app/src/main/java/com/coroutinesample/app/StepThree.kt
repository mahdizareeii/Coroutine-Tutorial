package com.coroutinesample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.step_one.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class StepThree : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step_three)

        btnTest.setOnClickListener {
            doSequentialTask()
        }
    }

    private fun doSequentialTask() {
        val parentJob = CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    delay(1000)
                    println("debug: launching job1: ${Thread.currentThread().name}")
                }

                result1.await()

                val result2 = async {
                    delay(1000)
                    println("debug: launching job2: ${Thread.currentThread().name}")
                }

                result2.await()
            }
        }

        parentJob.invokeOnCompletion {
            println("debug : all job ended sequentially")
        }
    }

    private fun doTaskAsync() {
        val parentJob = CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }

                val result2: Deferred<String> = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult2FromApi()
                }
                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")
            }
        }

        parentJob.invokeOnCompletion {
            println("debug : all job ended async")
        }
    }

    private fun doTaskWithoutAsync() {
        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time = measureTimeMillis {
                    setTextOnMainThread("job1 in thread")
                }
                println("debug: complete job1 in $time ms.")
            }
            val job2 = launch {
                val time = measureTimeMillis {
                    setTextOnMainThread("job2 in thread")
                }
                println("debug: complete job2 in $time ms.")
            }
        }

        parentJob.invokeOnCompletion {
            println("debug : all job ended after each other")
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "RESULT #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(1000)
        return "RESULT #2"
    }

    private suspend fun setTextOnMainThread(text: String) {
        //if we use CoroutineScope(Main).launch,
        //we can delete suspend keyword and it execute directly and launch here
        withContext(Main) {
            txtResult.text = txtResult.text.toString().plus("\n${text}")
        }
    }

}