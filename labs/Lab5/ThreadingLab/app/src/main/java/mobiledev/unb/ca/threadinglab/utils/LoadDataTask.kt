package mobiledev.unb.ca.threadinglab.utils

import android.content.Context
import android.content.Intent
import android.telecom.Call
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mobiledev.unb.ca.threadinglab.CourseListAdapter
import mobiledev.unb.ca.threadinglab.DetailActivity
import mobiledev.unb.ca.threadinglab.models.Course
import kotlin.time.Duration.Companion.milliseconds

class LoadDataTask(private val parentActivity: AppCompatActivity) {
    private val appContext: Context = parentActivity.applicationContext
    private lateinit var recyclerView: RecyclerView
    private lateinit var circularProgressIndicator: CircularProgressIndicator

    fun setRecyclerView(recyclerView: RecyclerView): LoadDataTask {
        this.recyclerView = recyclerView
        return this
    }

    fun setCircularProgressIndicator(circularProgressIndicator: CircularProgressIndicator): LoadDataTask {
        this.circularProgressIndicator = circularProgressIndicator
        return this
    }

    fun execute() {
        // Launch a coroutine thread on an IO (background task) thread to load JSON data
        // without freezing the UI
        MainScope().launch(Dispatchers.IO) {
            // Show the circular progress indicator
            circularProgressIndicator.visibility = ProgressBar.VISIBLE

            // TODO 1
            //  Load the data from the JSON assets file and return the list of courses
            val jsonUtils = JsonUtils(parentActivity)

            // switch back to the main UI thread to update the loading bar then
            // update the display with courses when they're done loading
            launch(Dispatchers.Main) {
                // Simulate a long-running operation
                for (i in 0..DOWNLOAD_TIME) {
                    sleep()
                    // TODO 2
                    //  Update the progress attribute of the progress bar
                    circularProgressIndicator.progress = i * 10
                }

                // TODO 3
                //  Using the updateDisplay method update the UI with the results
                updateDisplay(jsonUtils.courses)
            }
        }
    }

    private suspend fun sleep() {
        //delay(500.milliseconds)
        delay(100.milliseconds)
    }

    private fun updateDisplay(courseList: ArrayList<Course>) {
        // Update the RecyclerView
        updateRecyclerView(courseList)

        // Hide the circular progress indicator
        circularProgressIndicator.visibility = ProgressBar.INVISIBLE

        // TODO 6
        //  Create a Toast indicating that the file has been loaded
        //    HINT: Read this for help with Toast:
        //    http://developer.android.com/guide/topics/ui/notifiers/toasts.html
        Toast.makeText(appContext, "File has been loaded!", Toast.LENGTH_SHORT).show()
    }

    private fun updateRecyclerView(courseList: ArrayList<Course>) {
        // Set up the adapter for use in the RecyclerView
        // Basically means courseAdapter = new CourseListAdapter({ course -> ... })
        // So we are saying the onClick function is we take the course, make an intent to
        // start the details activity and fill it with the details of the course we
        // clicked on then start the activity
        val courseAdapter = CourseListAdapter { course ->
            // TODO 4
            //  Set the onClickListener for the TextView in the ViewHolder (holder) to create
            //  an explicit intent to launch DetailActivity
            //  from the parentActivity object passed into the class constructor
            //  HINT: You will need to put three extra pieces of information in this intent:
            //      The Course ID
            //      The Course Name
            //      The Course Description
            val intent = Intent(appContext, DetailActivity::class.java)
            intent.putExtra("courseID", course.id)
            intent.putExtra("courseName", course.name)
            intent.putExtra("courseDescription", course.description)
            parentActivity.startActivity(intent)
        }

        // Set our recyclerView to use the courseAdapter we just created
        recyclerView.apply {
            adapter = courseAdapter
        }

        // TODO 5: Push the course list into courseAdapter using submitList()
        // Updates the internal list in the courseAdapter with all our courses
        courseAdapter.submitList(courseList)
    }

    companion object {
        private const val TAG = "LoadDataTask"
        private const val DOWNLOAD_TIME = 10 // Download time simulation
    }
}