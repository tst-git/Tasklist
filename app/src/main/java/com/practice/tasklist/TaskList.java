package com.practice.tasklist;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class TaskList {

    String listName;
    ArrayList<Task> tasks;
    Context context;

    String filename = "tasks.pri";

    public TaskList(Context context, String name) {
        this.listName = name;
        this.tasks = new ArrayList<>();
        this.context = context;
    }

    public void append(Task task) {
        task.priority = this.tasks.size()+1;
        this.tasks.add(task);
    }

    public void delete(int index) {
        this.tasks.remove(index);
        // Decrease priority number of all tasks coming after removed one.
        for (int i = index; i < this.tasks.size(); i++) {
            Task t = this.tasks.get(i);
            this.tasks.set(i, new Task(t.name,t.priority-1,t.shortDescription,t.taskDetails,t.readiness));
        }
    }

    // Go through the task list from the end towards the start. Return index of the last item having readiness 100%.
    // An archived task will be stored here (all finished tasks at the end in order of finishing time).
    public int startOfArchive() {
        //
        int i = this.tasks.size()-1;
        while ((0 < i) && (this.item(i).readiness==Readiness.FINISHED)) {
            i--;
        }
        return i+1;
    }

    public void writeToFile() {

        try {
            FileOutputStream fileOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            for (int i = 0; i < this.tasks.size(); i++) {
                out.writeObject(this.tasks.get(i));
            }
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }


    public void readFromFile() {

        try {
            FileInputStream fileIn = context.openFileInput(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            try {
                while (true) {
                    Task t = (Task) in.readObject();
                    this.tasks.add(t);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                in.close();
                fileIn.close();
            }

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    // Adjust priorities of other tasks after one task has changed priority.
    public void arrangeTasks(int originalPriority, int newPriority) {
        // Set new priority for changed task.
        Task c = this.tasks.get(originalPriority-1);
        this.tasks.set(originalPriority-1, new Task(c.name,newPriority,c.shortDescription,c.taskDetails,c.readiness));
        // New priority is a lower number (i.e. higher priority).
        if (newPriority < originalPriority) {
            for (int i = newPriority; i < originalPriority; i++) {
                Task t = this.tasks.get(i-1);
                this.tasks.set(i-1, new Task(t.name,t.priority + 1,t.shortDescription,t.taskDetails,t.readiness));
            }
        // New priority is a greater number (i.e. lower priority).
        } else {
            for (int i = originalPriority+1; i <= newPriority; i++) {
                Task t = this.tasks.get(i-1);
                this.tasks.set(i-1, new Task(t.name, t.priority - 1, t.shortDescription, t.taskDetails, t.readiness));
            }
        }
        // Sort task list using priority as key.
        Collections.sort(this.tasks);
        writeToFile();
    }


    public void setListName(String listName) {
        this.listName = listName;
    }

    public Task item(int position) {
        return this.tasks.get(position);
    }

    public int size() {
        return this.tasks.size();
    }


}
