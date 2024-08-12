package com.scheduler.core.table.sync.enums;

import com.scheduler.core.table.sync.controller.strategies.TableSyncStrategy;
import com.scheduler.core.table.sync.controller.strategies.TaskSyncStrategy;
import com.scheduler.features.task.model.Task;

public enum TableType {

    TASK(
            Task.class,
            new TaskSyncStrategy(),
            1
    );

    public final Class<?> tableClass;

    public final TableSyncStrategy tableSyncStrategy;

    public final int priority;

    TableType(Class<?> tableClass, TableSyncStrategy tableSyncStrategy, int priority) {
        this.tableClass = tableClass;
        this.tableSyncStrategy = tableSyncStrategy;
        this.priority = priority;
    }

    public static TableType getByName(String name) {

        return switch (name) {

            case "task" -> TableType.TASK;

            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}
