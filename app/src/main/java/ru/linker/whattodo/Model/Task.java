package ru.linker.whattodo.Model;

import java.io.Serializable;

/**
 * Created by root on 2/4/17.
 * Licensed under Attribution-NonCommercial 3.0 Unported
 */

public class Task implements Serializable {

    private String taskDescription;
    private Integer priority;

    public Task(String taskDescription, Integer priority) {

        if (priority > 100 || priority < 0)
            throw new IndexOutOfBoundsException();

        this.taskDescription = taskDescription;
        this.priority = priority;
    }

    public String getTaskDescription() {

        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {

        this.taskDescription = taskDescription;
    }

    public Integer getPriority() {
        System.out.println("Undated task, priority : " + priority);
        return priority;
    }

    public void setPriority(Integer priority) {

        if (priority > 100 || priority < 0)
            throw new IndexOutOfBoundsException();

        this.priority = priority;
    }

    public Integer getNonDatedPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Task && (((Task) obj).getPriority().equals(priority)) && (((Task) obj).getTaskDescription().equals(taskDescription));

    }
}
