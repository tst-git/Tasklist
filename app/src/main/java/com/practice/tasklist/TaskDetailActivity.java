package com.practice.tasklist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity implements DeletePopup.DeletePopupListener {

    private int index;
    DeletePopup delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent in = getIntent();
        index = in.getIntExtra("com.practice.ITEM_INDEX", -1);

        if (index > -1) {

            int taskPriority = in.getIntExtra("com.practice.PRIORITY", -1);
            int lastTaskPriority = in.getIntExtra("com.practice.MIN-PRIORITY", -1);
            String taskName = in.getStringExtra("com.practice.NAME");
            String taskDescription = in.getStringExtra("com.practice.DESCRIPTION");
            String taskDetails = in.getStringExtra("com.practice.DETAILS");
            int taskReadiness = in.getIntExtra("com.practice.READINESS",-1);

            TextView priority = (TextView) findViewById(R.id.priorityEditText);
            ImageView readiness = (ImageView) findViewById(R.id.readinessImageEditView);
            TextView name = (TextView) findViewById(R.id.nameEditText);
            TextView description = (TextView) findViewById(R.id.descriptionTexViewt);
            TextView details = (TextView) findViewById(R.id.commentsTextView);
            ImageButton edit = (ImageButton) findViewById(R.id.exitImageButton);
            ImageButton delete = (ImageButton) findViewById(R.id.deleteImageButton);

            priority.setText(Integer.toString(taskPriority));
            name.setText(taskName);
            description.setText(taskDescription);
            details.setText(taskDetails);
            readiness.setImageBitmap(readinessGraph(taskReadiness));

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editDetailActivity = new Intent(getApplicationContext(), TaskEditActivity.class);

                    editDetailActivity.putExtra("com.practice.EDIT-MODE", "edit-existing");
                    editDetailActivity.putExtra("com.practice.ITEM_INDEX", index);
                    editDetailActivity.putExtra("com.practice.PRIORITY", taskPriority);
                    editDetailActivity.putExtra("com.practice.MIN-PRIORITY", lastTaskPriority);
                    editDetailActivity.putExtra("com.practice.NAME", taskName);
                    editDetailActivity.putExtra("com.practice.DESCRIPTION", taskDescription);
                    editDetailActivity.putExtra("com.practice.READINESS", taskReadiness);
                    editDetailActivity.putExtra("com.practice.DETAILS", taskDetails);

                    startActivityForResult(editDetailActivity, Constants.EDIT_ACTIVITY_REQUEST_CODE);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDeleteDialog();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.EDIT_ACTIVITY_REQUEST_CODE) {
            // Pass information with data from edit activity to main activity.
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    private int getImg(int phase) {

        switch (phase) {
            case 0: return R.drawable.created;
            case 1: return R.drawable.started;
            case 2: return R.drawable.halfway;
            case 3: return R.drawable.progressing;
            case 4: return R.drawable.finished;
            default: return -1;
        }
    }

    private Bitmap readinessGraph(int phase) {

        int imgNum = getImg(phase);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),imgNum);
        return bitmap;
    }

    public void openDeleteDialog() {
        delete = new DeletePopup();
        delete.show(getSupportFragmentManager(), "");
    }

    public void closeDeleteDialog() {
        delete.dismiss();
    }

    @Override
    public void getAction(String action) {
        if (action.equals("delete")) {
            Intent intent = new Intent();
            intent.putExtra("com.practice.RETURN-MODE", Constants.RETURN_MODE_DELETE);
            intent.putExtra("com.practice.ITEM_INDEX", index);
            setResult(RESULT_OK, intent);
            closeDeleteDialog();
            finish();
        } else {
            finish();
        }
    }
}