package com.coroutinesample.app

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.step_two.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class StepTwo : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 //time that we want to do a job
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.step_two)

        handleJob.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
            }
            progressBar.startJobOrCancel(job)
        }
    }

    private fun initJob() {
        handleJob.text = "Start Job"
        updateJobCompleteText("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrEmpty()) {
                    msg = "Unknown cancellation Error"
                }
                showToast(msg)
            }
        }
        progressBar.max = PROGRESS_MAX
        progressBar.progress = PROGRESS_START
    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            resetJob()
        } else {
            handleJob.text = "Cancel Job"
            //notice 1 : if we use withContext(IO + job) we need to add suspend keyword into our fun
            //notice 2 : we must use (IO + job)
            //notice 2 : if we don't use that coroutine will do operation into all of IO
            //notice 2 : so we use that (IO + job) for do operation for specific job
            CoroutineScope(IO + job).launch {
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteText("Job is complete")
            }
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()
    }

    private fun updateJobCompleteText(text: String) {
        CoroutineScope(Main).launch {
            txtJobState.text = text
        }
    }

    private fun showToast(text: String) {
        CoroutineScope(Main).launch {
            Toast.makeText(this@StepTwo, text, Toast.LENGTH_SHORT).show()
        }
    }
}