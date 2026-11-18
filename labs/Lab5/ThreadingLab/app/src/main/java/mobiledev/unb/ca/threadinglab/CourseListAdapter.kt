package mobiledev.unb.ca.threadinglab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mobiledev.unb.ca.threadinglab.models.Course

/**
 * Class used to populate each row of the RecyclerView
 * NOTE: This makes use of a Callback Lambda for the listener
 *       which allows you to delegate the click logic back to LoadDataTask
 * @param onRowClicked The onClickListener behavior for each row
 */
class CourseListAdapter(
    // (Course) -> Unit means "a function that takes a course and returns nothing"
    private val onRowClicked: (Course) -> Unit
    // extend ListAdapter so it automatically keeps track of the list of courses
    // <Course, CourseListAdapter.CourseViewHolder> means <type of data, type of ViewHolder>
) : ListAdapter<Course, CourseListAdapter.CourseViewHolder>(CourseDiffCallback) {

    // called when the RecyclerView needs a new ViewHolder for a course
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        // Create a new view from the XML of what the row should look like
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return CourseViewHolder(view, onRowClicked)
    }

    // Called when the course is about to be displayed
    // Position is the index of the course in the list
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = getItem(position) // get the course at index position in the internal list
        holder.bind(course)
    }

    // Class that extends ViewHolder since recyclerView requires ViewHolders
    class CourseViewHolder(itemView: View, private val onRowClicked: (Course) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val courseIdTextView: TextView = itemView.findViewById(R.id.courseId)
        val courseNameTextView: TextView = itemView.findViewById(R.id.courseName)

        // When the course is about to be displayed, set the text and listener
        fun bind(course: Course) {
            // TODO 2
            //  Set the text for TextViews to use the id and name of the course
            courseIdTextView.text = course.id
            courseNameTextView.text = course.name

            // Set the onClickListener to the onRowClicked function using the current course details
            itemView.setOnClickListener { onRowClicked(course) }
        }
    }

    // DiffUtil handles efficient item updates instead of recreating the whole list
    // These are necessary in all implementations of ListAdapter
    object CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}