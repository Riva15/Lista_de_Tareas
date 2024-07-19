package riva.ticona.tarealistas

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: TaskDatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvTask: RecyclerView
    private lateinit var btnAddTask: Button
    private lateinit var etAddTask: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = TaskDatabaseHelper(this)
        taskAdapter = TaskAdapter(
            onStatusChanged = { task, isChecked -> updateTaskStatus(task, isChecked) },
            onDeleteClicked = { task -> deleteTask(task) },
            onEditClicked = { task, newName -> updateTaskName(task, newName) }
        )

        rvTask = findViewById(R.id.rvTask)
        btnAddTask = findViewById(R.id.btnAddTask)
        etAddTask = findViewById(R.id.etAddTask)

        rvTask.layoutManager = LinearLayoutManager(this)
        rvTask.adapter = taskAdapter

        btnAddTask.setOnClickListener {
            val taskName = etAddTask.text.toString()
            if (taskName.isNotEmpty()) {
                addTask(taskName)
                etAddTask.text.clear()
            }
        }

        loadTasks()
    }

    private fun addTask(taskName: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDatabaseHelper.COLUMN_NAME, taskName)
            put(TaskDatabaseHelper.COLUMN_IS_DONE, 0)
        }
        db.insert(TaskDatabaseHelper.TABLE_NAME, null, values)
        loadTasks()
    }

    private fun updateTaskStatus(task: Task, isDone: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDatabaseHelper.COLUMN_IS_DONE, if (isDone) 1 else 0)
        }
        db.update(TaskDatabaseHelper.TABLE_NAME, values, "${TaskDatabaseHelper.COLUMN_ID} = ?", arrayOf(task.id.toString()))
        loadTasks()
    }

    private fun updateTaskName(task: Task, newName: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TaskDatabaseHelper.COLUMN_NAME, newName)
        }
        db.update(TaskDatabaseHelper.TABLE_NAME, values, "${TaskDatabaseHelper.COLUMN_ID} = ?", arrayOf(task.id.toString()))
        loadTasks()
    }

    private fun loadTasks() {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TaskDatabaseHelper.TABLE_NAME,
            arrayOf(TaskDatabaseHelper.COLUMN_ID, TaskDatabaseHelper.COLUMN_NAME, TaskDatabaseHelper.COLUMN_IS_DONE),
            null, null, null, null, null
        )
        val tasks = mutableListOf<Task>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_NAME))
                val isDone = getInt(getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_IS_DONE)) == 1
                tasks.add(Task(id, name, isDone))
            }
        }
        cursor.close()
        taskAdapter.submitList(tasks)
    }

    private fun deleteTask(task: Task) {
        val db = dbHelper.writableDatabase
        db.delete(TaskDatabaseHelper.TABLE_NAME, "${TaskDatabaseHelper.COLUMN_ID} = ?", arrayOf(task.id.toString()))
        loadTasks()
    }
}
