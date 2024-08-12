package com.scheduler.core.table.sync.controller;

import com.scheduler.core.table.sync.dto.TableSyncDTO;
import com.scheduler.core.table.sync.enums.TableType;
import com.scheduler.features.task.model.Task;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Transactional
@ApplicationScoped
public class TableSyncController {
    @Inject
    ManagedExecutor executor;

    public <T extends PanacheEntity> List<TableSyncDTO> syncAll(List<TableSyncDTO> listTableSyncDTO) {
        final var listSyncronizedTables = new ArrayList<TableSyncDTO>();

        for (TableSyncDTO tableSyncDTO : listTableSyncDTO) {

            final var tableType = TableType.getByName(tableSyncDTO.name());
            final List<T> entities = (List<T>) syncTable(tableType, (List<Map<String, Object>>) tableSyncDTO.rows());
            listSyncronizedTables.add(new TableSyncDTO(tableSyncDTO.name(), entities));
        }

        return listSyncronizedTables;
    }

    private <T extends PanacheEntity> List<?> syncTable(TableType tableType, List<Map<String, Object>> rowsJson) {

        final List<T> listJsonEntities = new CopyOnWriteArrayList<>();

        for (Map<String, Object> jsonMap : rowsJson) {
            if (tableType.equals(TableType.TASK)) {
                listJsonEntities.add((T) Task.fromJson(jsonMap));
            }
        }

        final LinkedBlockingQueue<Runnable> runnables = new LinkedBlockingQueue<>();

        final List<T> listDatabaseEntities = (CopyOnWriteArrayList<T>) tableType.tableSyncStrategy.getTable();

        final List<T> updatedList = new CopyOnWriteArrayList<>();

        if (!listJsonEntities.isEmpty()) {
            for (T jsonEntity : listJsonEntities) {

                if (jsonEntity.isPersistent()) {
                    listJsonEntities.remove(jsonEntity);
                    continue;
                }

                for (T databaseEntity : listDatabaseEntities) {
                    if (databaseEntity.id.equals(jsonEntity.id)) {

                        updatedList.add(jsonEntity);
                        listJsonEntities.remove(jsonEntity);
                        listDatabaseEntities.remove(databaseEntity);
                    }
                }
            }
            for (var entity : listJsonEntities) {
                runnables.add(() -> {

                    try {
                        System.out.println("inserting");
                        entity.id = null;
                        System.out.println(entity);
                        tableType.tableSyncStrategy.insertRow(entity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            for (var entity : updatedList) {

                System.out.println("updating");
                System.out.println(entity);
                runnables.add(() -> tableType.tableSyncStrategy.updateRow(entity));
            }

            for (var entity : listDatabaseEntities) {
                System.out.println("deleting");
                System.out.println(entity);
                runnables.add(() -> tableType.tableSyncStrategy.deleteRow(entity.id));
            }

            for (Runnable runnable : runnables) {
                executor.submit(runnable);
            }
        }

        return tableType.tableSyncStrategy.getTable();
    }
}
