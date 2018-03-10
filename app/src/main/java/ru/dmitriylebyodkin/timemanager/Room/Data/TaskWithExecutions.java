package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by dmitr on 09.03.2018.
 */

public class TaskWithExecutions {
    @Embedded
    private Task task;
    @Relation(parentColumn = "id", entity = Execution.class, entityColumn = "taskId")
    private List<Execution> executions;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }
}
