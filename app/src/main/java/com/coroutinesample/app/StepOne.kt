package com.coroutinesample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.step_one.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class StepOne : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step_one)

        btnTest.setOnClickListener {
            //IO : for network request or local database
            //Main : for do task on main thread
            //Default : for any heavy computation work
            CoroutineScope(IO).launch {
                fakeApiRequest2()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        val result1 = getResult1FromApi()
        setTextOnMainThread(result1)

        //this method await for the above job ended and this will execute after it
        val result2 = getResult2FromApi()
        setTextOnMainThread(result2)
    }

    private suspend fun setTextOnMainThread(text: String) {
        //if we use CoroutineScope(Main).launch,
        //we can delete suspend keyword and it execute directly and launch here
        withContext(Main) {
            txtResult.text = txtResult.text.toString().plus("\n${text}")
        }
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        return "RESULT #1"
    }

    private suspend fun getResult2FromApi(): String {
        logThread("getResult2FromApi")
        delay(1000)
        return "RESULT #2"
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }


    //in this case we use *withTimeoutOrNull* for set timeout
    //instead of *launch* it just get time out and return null
    private val JOB_TIME_OUT = 1900L
    private suspend fun fakeApiRequest2() {
        withContext(IO) {
            val job = withTimeoutOrNull(JOB_TIME_OUT) {
                val result1 = getResult1FromApi()
                setTextOnMainThread(result1)

                //this method await for the above job ended and this will execute after it
                val result2 = getResult2FromApi()
                setTextOnMainThread(result2)
            }

            if (job == null) {
                setTextOnMainThread("job took longer than $JOB_TIME_OUT")
            }
        }
    }
}