package com.practice.tasklist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskEditActivity extends AppCompatActivity implements ArchivePopup.ArchivePopupListener {


    public int taskPriority;
    public int taskReadiness;
    int originalPriority;
    int originalReadiness;
    ArchivePopup archive;
    String editMode;
    int index;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        Intent in = getIntent();
        index = in.getIntExtra("com.practice.ITEM_INDEX", -1);

        if (index > -1) {
            // Get current data from taskDetailView.
            editMode = in.getStringExtra("com.practice.EDIT-MODE");
            taskPriority = in.getIntExtra("com.practice.PRIORITY", -1);
            int lastTaskPriority = in.getIntExtra("com.practice.MIN-PRIORITY", -1);
            String taskName = in.getStringExtra("com.practice.NAME");
            String taskDescription = in.getStringExtra("com.practice.DESCRIPTION");
            String taskDetails = in.getStringExtra("com.practice.DETAILS");
            taskReadiness = in.getIntExtra("com.practice.READINESS", -1);

            // Text elements in activity_task_edit.
            TextView priority = (TextView) findViewById(R.id.priorityEditText);
            EditText name = (EditText) findViewById(R.id.nameEditText);
            EditText description = (EditText) findViewById(R.id.descriptionEditText);
            EditText details = (EditText) findViewById(R.id.commentsEditText);
            // Button elements for changing priority.
            ImageButton up = (ImageButton) findViewById(R.id.priorityUpImageButton);
            ImageButton down = (ImageButton) findViewById(R.id.priorityDownImageButton);
            // Button elements for exiting with changes saved or cancelling without saving changes..
            ImageButton exit = (ImageButton) findViewById(R.id.exitImageButton);
            ImageButton cancel = (ImageButton) findViewById(R.id.cancelImageButton);
            // Button elements for changing readiness.
            Button minus = (Button) findViewById(R.id.decreaseButton);
            Button plus = (Button) findViewById(R.id.increaseButton);
            // Placeholder for readiness images.
            ImageView readiness = findViewById(R.id.readinessImageEditView);

            // Save original values before editing.
            originalPriority = taskPriority;
            originalReadiness = taskReadiness;

            // Set current data for editable fields
            priority.setText(Integer.toString(taskPriority));
            name.setText(taskName);
            description.setText(taskDescription);
            details.setText(taskDetails);
            readiness.setImageBitmap(readinessGraph(taskReadiness));

            // Increase priority by decreasing priority value (if not 1 already).
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (1 < taskPriority) {
                        taskPriority = taskPriority - 1;
                        priority.setText(Integer.toString(taskPriority));
                    }
                }
            });
            // Decrease priority by increasing priority value (if not maximum already).
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (taskPriority < lastTaskPriority) {
                        taskPriority = taskPriority + 1;
                        priority.setText(Integer.toString(taskPriority));
                    }
                }
            });
            // Decrease readiness value (if not CREATED already).
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Readiness.CREATED < taskReadiness) {
                        taskReadiness = taskReadiness - 1;
                        readiness.setImageBitmap(readinessGraph(taskReadiness));
                    }
                }
            });
            // Increase readiness value (if not FINISHED already).
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (taskReadiness < Readiness.FINISHED) {
                        taskReadiness = taskReadiness + 1;
                        readiness.setImageBitmap(readinessGraph(taskReadiness));
                    }
                }
            });
            // Cancel button pressed: discard changes in data and return to List view.
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent();
                    intent.putExtra("com.practice.EDIT-MODE", editMode);
                    intent.putExtra("com.practice.RETURN-MODE", Constants.RETURN_MODE_CANCEL);

                    finish();
                }
            });
            // Exit button pressed: set new values to TaskList.
            // If priority of this task has been changed, changes must be done to other tasks, too.
            // At the end the list view is shown (two finish() -commands).
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((originalReadiness < taskReadiness) && (taskReadiness==Readiness.FINISHED)) {
                        intent = new Intent();
                        intent.putExtra("com.practice.EDIT-MODE", editMode);
                        intent.putExtra("com.practice.NAME", String.valueOf(name.getText()));
                        intent.putExtra("com.practice.DESCRIPTION", String.valueOf(description.getText()));
                        intent.putExtra("com.practice.DETAILS", String.valueOf(details.getText()));
                        intent.putExtra("com.practice.ORIGINAL_PRIORITY", originalPriority);

                        openArchiveDialog();

                    } else {
                        intent = new Intent();
                        intent.putExtra("com.practice.EDIT-MODE", editMode);
                        intent.putExtra("com.practice.RETURN-MODE", Constants.RETURN_MODE_EXIT);
                        intent.putExtra("com.practice.NEW_PRIORITY", taskPriority);
                        intent.putExtra("com.practice.ORIGINAL_PRIORITY", originalPriority);
                        intent.putExtra("com.practice.ORIGINAL_READINESS", originalReadiness);
                        intent.putExtra("com.practice.READINESS", taskReadiness);
                        intent.putExtra("com.practice.NAME", String.valueOf(name.getText()));
                        intent.putExtra("com.practice.DESCRIPTION", String.valueOf(description.getText()));
                        intent.putExtra("com.practice.DETAILS", String.valueOf(details.getText()));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }
    }

    public void openArchiveDialog() {
        archive = new ArchivePopup();
        archive.show(getSupportFragmentManager(), "");
    }

    public void closeArchiveDialog() {
        archive.dismiss();
    }

    @Override
    public void getAction(String action) {
        switch(action)
        {
            case "archive":
                intent.putExtra("com.practice.ITEM_INDEX", index);
                intent.putExtra("com.practice.RETURN-MODE", Constants.RETURN_MODE_ARCHIVE);
                setResult(RESULT_OK, intent);
                closeArchiveDialog();
                finish();
                break;
            case "delete":
                intent = new Intent();
                intent.putExtra("com.practice.RETURN-MODE", Constants.RETURN_MODE_DELETE);
                intent.putExtra("com.practice.ITEM_INDEX", index);
                setResult(RESULT_OK, intent);
                closeArchiveDialog();
                finish();
                break;
            default:    // "cancel"
                finish();
        }
    }

    private int getImg(int phase) {

        switch (phase) {
            case 0:
                return R.drawable.created;
            case 1:
                return R.drawable.started;
            case 2:
                return R.drawable.halfway;
            case 3:
                return R.drawable.progressing;
            case 4:
                return R.drawable.finished;
            default:
                return -1;
        }
    }

    private Bitmap readinessGraph(int phase) {

        int imgNum = getImg(phase);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgNum);
        return bitmap;
    }
}
