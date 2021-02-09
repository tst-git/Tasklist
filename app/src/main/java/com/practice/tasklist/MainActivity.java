package com.practice.tasklist;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ListView taskListView;

    public TaskList taskList1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        taskListView = (ListView) findViewById(R.id.taskListView);
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.AddFloatingActionButton);

        taskList1 = new TaskList(this, "My tasks");
        taskList1.readFromFile();

        ItemAdapter itemAdapter = new ItemAdapter(this, taskList1);
        taskListView.setAdapter(itemAdapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDetailActivity = new Intent(getApplicationContext(), TaskDetailActivity.class);
                showDetailActivity.putExtra("com.practice.ITEM_INDEX", position);
                showDetailActivity.putExtra("com.practice.PRIORITY", taskList1.item(position).priority);
                showDetailActivity.putExtra("com.practice.MIN-PRIORITY", taskList1.size());
                showDetailActivity.putExtra("com.practice.READINESS", taskList1.item(position).readiness);
                showDetailActivity.putExtra("com.practice.NAME", taskList1.item(position).name);
                showDetailActivity.putExtra("com.practice.DESCRIPTION", taskList1.item(position).shortDescription);
                showDetailActivity.putExtra("com.practice.DETAILS", taskList1.item(position).taskDetails);

                startActivityForResult(showDetailActivity, Constants.SHOW_ACTIVITY_REQUEST_CODE);

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editDetailActivity = new Intent(getApplicationContext(), TaskEditActivity.class);

                editDetailActivity.putExtra("com.practice.EDIT-MODE", "create-new");
                editDetailActivity.putExtra("com.practice.ITEM_INDEX", taskList1.size());
                editDetailActivity.putExtra("com.practice.PRIORITY", taskList1.size()+1);
                editDetailActivity.putExtra("com.practice.MIN-PRIORITY", taskList1.size());
                editDetailActivity.putExtra("com.practice.NAME", "");
                editDetailActivity.putExtra("com.practice.DESCRIPTION", "");
                editDetailActivity.putExtra("com.practice.READINESS", 0);
                editDetailActivity.putExtra("com.practice.DETAILS", "");

                startActivityForResult(editDetailActivity, Constants.EDIT_ACTIVITY_REQUEST_CODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SHOW_ACTIVITY_REQUEST_CODE || requestCode == Constants.EDIT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Pointer to adapter in case list have to be updated.
                ItemAdapter itemAdapter = (ItemAdapter) taskListView.getAdapter();
                int returnMode = data.getIntExtra("com.practice.RETURN-MODE",-1);
                // Data will be checked and updated only if save/exit was selected.
                switch (returnMode) {
                    case Constants.RETURN_MODE_EXIT:
                        // Get data.
                        String editMode = data.getStringExtra("com.practice.EDIT-MODE");
                        int new_priority = data.getIntExtra("com.practice.NEW_PRIORITY", -1);
                        int old_priority = data.getIntExtra("com.practice.ORIGINAL_PRIORITY", -1);
                        int readiness = data.getIntExtra("com.practice.READINESS", -1);
                        int old_readiness = data.getIntExtra("com.practice.ORIGINAL_READINESS", -1);
                        String name = data.getStringExtra("com.practice.NAME");
                        String description = data.getStringExtra("com.practice.DESCRIPTION");
                        String details = data.getStringExtra("com.practice.DETAILS");
                        if (editMode.equals("create-new")) {
                            // Add new task at the end of list. Priority is set to -1 since append will set it to size+1.
                            taskList1.append(new Task(name, -1, description, details, readiness));
                            taskList1.writeToFile();
                        } else {
                            // Update data of edited task (no check if changes are done - do it anyway).
                            taskList1.tasks.set(old_priority - 1, new Task(name, new_priority, description, details, readiness));
                            taskList1.writeToFile();
                        }
                        // If priority of this task has changed, it (probably) needs changes to other priorities, too.
                        if (old_priority != new_priority) {
                            taskList1.arrangeTasks(old_priority, new_priority);
                        }
                        itemAdapter.notifyDataSetChanged();
                        break;
                    case Constants.RETURN_MODE_DELETE:
                        int index = data.getIntExtra("com.practice.ITEM_INDEX",-1);
                        taskList1.delete(index);
                        taskList1.writeToFile();
                        itemAdapter.notifyDataSetChanged();
                        break;
                    case Constants.RETURN_MODE_ARCHIVE:
                        old_priority = data.getIntExtra("com.practice.ORIGINAL_PRIORITY", -1);
                        name = data.getStringExtra("com.practice.NAME");
                        description = data.getStringExtra("com.practice.DESCRIPTION");
                        details = data.getStringExtra("com.practice.DETAILS");
                        // Update data of edited task (no check if changes are done - do it anyway).
                        new_priority = taskList1.startOfArchive();
                        taskList1.tasks.set(old_priority - 1, new Task(name, new_priority, description, details, Readiness.FINISHED));
                        taskList1.writeToFile();
                        // If priority of this task has changed, it (probably) needs changes to other priorities, too.
                        if (old_priority != new_priority) {
                            taskList1.arrangeTasks(old_priority, new_priority);
                        }
                        itemAdapter.notifyDataSetChanged();

                        break;
                }
            }
        }
    }
}
