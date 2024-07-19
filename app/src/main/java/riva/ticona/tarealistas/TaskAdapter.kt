package riva.ticona.tarealistas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onStatusChanged: (Task, Boolean) -> Unit,
    private val onDeleteClicked: (Task) -> Unit,
    private val onEditClicked: (Task, String) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, onStatusChanged, onDeleteClicked, onEditClicked)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class TaskViewHolder(
        itemView: View,
        private val onStatusChanged: (Task, Boolean) -> Unit,
        private val onDeleteClicked: (Task) -> Unit,
        private val onEditClicked: (Task, String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cbIsDone: CheckBox = itemView.findViewById(R.id.cbIsDone)
        private val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        private val etTaskName: EditText = itemView.findViewById(R.id.etTaskName)
        private val btnEditTask: ImageButton = itemView.findViewById(R.id.btnEditTask)
        private val btnSaveTask: ImageButton = itemView.findViewById(R.id.btnSaveTask)
        private val btnDeleteTask: ImageButton = itemView.findViewById(R.id.btnDeleteTask)

        fun bind(task: Task) {
            tvTaskName.text = task.name
            cbIsDone.isChecked = task.isDone
            etTaskName.setText(task.name)

            cbIsDone.setOnCheckedChangeListener { _, isChecked ->
                onStatusChanged(task, isChecked)
            }

            btnDeleteTask.setOnClickListener {
                onDeleteClicked(task)
            }

            btnEditTask.setOnClickListener {
                if (etTaskName.visibility == View.GONE) {
                    // Cambia a modo edición
                    etTaskName.visibility = View.VISIBLE
                    tvTaskName.visibility = View.GONE
                    btnEditTask.visibility = View.GONE
                    btnSaveTask.visibility = View.VISIBLE
                    etTaskName.requestFocus()
                }
            }

            btnSaveTask.setOnClickListener {
                val newName = etTaskName.text.toString()
                if (newName.isNotBlank()) {
                    onEditClicked(task, newName)
                }
                // Regresa a modo visualización
                etTaskName.visibility = View.GONE
                tvTaskName.visibility = View.VISIBLE
                btnEditTask.visibility = View.VISIBLE
                btnSaveTask.visibility = View.GONE
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
