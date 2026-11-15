package mobiledev.unb.ca.threadinglab

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TODO 1
        //  Get the intent that started this activity, and get the extras from it
        //  corresponding to the title and description of the course
        val intent = getIntent()
        val courseId = intent.getStringExtra("courseId")
        val courseName = intent.getStringExtra("courseName")
        val courseDesc = intent.getStringExtra("courseDescription")

        // Lookup the action bar as defined in the layout file
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)  // This gives us the return arrow button in the action bar
            // TODO 2
            //  Set the title of the action bar to be the course ID
            title = courseId
        }

        // TODO 3
        //  Set the name TextView to be the course name
        val nameText: TextView = findViewById(R.id.nameTextView)
        nameText.text = courseName
        
        // TODO 4
        //  Set the description TextView to be the course description
        val descText: TextView = findViewById(R.id.descriptionTextview)
        descText.text = courseDesc
        
        // TODO 5
        //  Make the TextView scrollable
        //  HINT: Set the movementMethod attribute for description TextView
        descText.movementMethod = ScrollingMovementMethod()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Destroy the activity and go back to the parent activity
        // This is specified by using android:parentActivityName=".MainActivity" in the
        // AndroidManifest.xml file
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}