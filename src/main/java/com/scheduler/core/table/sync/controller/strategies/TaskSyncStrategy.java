package com.scheduler.core.table.sync.controller.strategies;

import com.scheduler.features.task.model.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.concurrent.CopyOnWriteArrayList;

import static jakarta.transaction.Transactional.TxType.NOT_SUPPORTED;

@ApplicationScoped
@Transactional(NOT_SUPPORTED)
public class TaskSyncStrategy implements TableSyncStrategy<Task>, PanacheRepository<Task> {

    @Override
    public void deleteRow(long... id) {

        System.out.println("Deleting id: " + id[0]);
        deleteById(id[0]);
    }

    @Override
    public void insertRow(Task entity) {

        System.out.println("Inserting entity: " + entity.title);
        persist(entity);
    }

    @Override
    public void updateRow(Task entity) {

        System.out.println("Updating entity: " + entity.id);
        persist(entity);
    }

    @Override
    public CopyOnWriteArrayList<Task> getTable() {

        return new CopyOnWriteArrayList<>(listAll());
    }
}
