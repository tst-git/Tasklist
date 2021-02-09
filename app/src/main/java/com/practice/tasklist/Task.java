package com.practice.tasklist;

public class Task implements Comparable<Task>, java.io.Serializable {

    String name;                // Task name shown in all views with large font.
    String shortDescription;    // Description shown with small font when only one list is visible (not 3 parallel).
    String taskDetails;         // Longer description with possible phases. Shown only in one-task-view.
    int priority;               // Priority number: integer starting from 1. Smaller number means higher priority.
    int readiness;              // Readiness scale: CREATED=0, STARTED=1, HALFWAY=2, PROGRESSING=3, FINISHED=4

    public Task(String name, int priority, String description, String details, int readiness) {
        this.name=name;
        this.shortDescription = description;
        this.taskDetails = details;
        this.priority = priority;
        this.readiness = readiness;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Task theOtherOne) {
        return this.priority - theOtherOne.getPriority();
    }

}
