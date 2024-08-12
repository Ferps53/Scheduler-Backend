package com.scheduler.core.table.sync.controller.strategies;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.concurrent.CopyOnWriteArrayList;

public interface TableSyncStrategy<T extends PanacheEntity> {

    void deleteRow(long... id);

    void insertRow(T entity);

    void updateRow(T entity);

    CopyOnWriteArrayList<T> getTable();
}
