package mobiledev.unb.ca.labexam

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TODO
        //  Get the intent that started this activity along with the extras added to it
        val intent = getIntent()
        val eventYear = intent.getStringExtra(Constants.INTENT_KEY_YEAR)
        val eventNumber = intent.getStringExtra(Constants.INTENT_KEY_NUMBER)
        val eventHost = intent.getStringExtra(Constants.INTENT_KEY_HOST_CITY)
        val eventDates = intent.getStringExtra(Constants.INTENT_KEY_DATES)
        val wikipediaLink = intent.getStringExtra(Constants.INTENT_KEY_WIKIPEDIA_LINK)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            // TODO
            //  Set the title of the action bar to be the event number
            setDisplayHomeAsUpEnabled(true)
            title = eventNumber
        }

        // TODO
        //  Set the details for the host city text view
        val hostTextView: TextView = findViewById(R.id.host_city_text_view)
        hostTextView.text = eventHost

        // TODO
        //  Set the details for the year text view
        val yearTextView: TextView = findViewById(R.id.year_text_view)
        yearTextView.text = eventYear

        // TODO
        //  Set the details for the dates text view
        val datesTextView: TextView = findViewById(R.id.dates_text_view)
        datesTextView.text = eventDates

        // TODO
        //  Set an onClickListener such that when this button is clicked, an implicit intent is started
        //  to open the wikipedia URL in a web browser. Be sure to check that there is
        //  an application installed that can handle this intent before starting it.
        //  If the intent can't be started, show a toast indicating this.
        //  Hints:
        //   https://developer.android.com/reference/android/content/Intent.html#resolveActivity(android.content.pm.PackageManager)
        //   https://developer.android.com/guide/components/intents-common.html#Browser
        //   https://developer.android.com/reference/android/net/Uri.html#parse(java.lang.String)
        val openPageButton: Button = findViewById(R.id.wiki_button)

        openPageButton.setOnClickListener {
            val webPage: Uri = Uri.parse(wikipediaLink)
            val browserIntent = Intent(Intent.ACTION_VIEW, webPage)
            if (browserIntent.resolveActivity(packageManager) != null) {
                startActivity(browserIntent)
            } else {
                Toast.makeText(this, "Couldn't open browser...", Toast.LENGTH_SHORT).show()
            }
        }
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