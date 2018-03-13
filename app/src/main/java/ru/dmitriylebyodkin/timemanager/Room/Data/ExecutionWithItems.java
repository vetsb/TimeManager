package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by dmitr on 10.03.2018.
 */

public class ExecutionWithItems {
    @Embedded
    private Execution execution;
    @Relation(parentColumn = "id", entity = ExItem.class, entityColumn = "executionId")
    private List<ExItem> items;

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public List<ExItem> getItems() {
        return items;
    }

    public void setItems(List<ExItem> items) {
        this.items = items;
    }
}
