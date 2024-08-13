package com.scheduler.core.table.sync.controller;

import com.scheduler.core.table.sync.dto.TableSyncDTO;
import com.scheduler.core.table.sync.enums.TableType;
import com.scheduler.features.task.model.Task;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Transactional
@ApplicationScoped
@SuppressWarnings("unchecked")
public class TableSyncController {

    public List<TableSyncDTO> syncAll(List<TableSyncDTO> listTableSyncDTO) {
        final var listSyncronizedTables = new ArrayList<TableSyncDTO>();

        for (TableSyncDTO tableSyncDTO : listTableSyncDTO) {

            final var tableType = TableType.getByName(tableSyncDTO.name());
            final List<?> entities = syncTable(tableType, (List<Map<String, Object>>) tableSyncDTO.rows(), tableSyncDTO.userId());
            listSyncronizedTables.add(new TableSyncDTO(tableSyncDTO.name(), tableSyncDTO.userId(), entities));
        }

        return listSyncronizedTables;
    }

    private <T extends PanacheEntity> List<?> syncTable(TableType tableType, List<Map<String, Object>> rowsJson, long userId) {

        final List<T> listJsonEntities = convertGenericJson(tableType, rowsJson);
        final List<T> listDatabaseEntities = tableType.tableSyncStrategy.getTableOrm(userId);
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
            synchronizeTables(tableType, listJsonEntities, updatedList, listDatabaseEntities);
        }

        return tableType.tableSyncStrategy.getTableDto(userId);
    }

    private <T extends PanacheEntity> List<T> convertGenericJson(TableType tableType, List<Map<String, Object>> rowsJson) {
        final List<T> listJsonEntities = new CopyOnWriteArrayList<>();
        for (Map<String, Object> jsonMap : rowsJson) {
            if (tableType.equals(TableType.TASK)) {
                listJsonEntities.add((T) Task.fromJson(jsonMap));
            }
        }
        return listJsonEntities;
    }

    private <T extends PanacheEntity> void synchronizeTables(TableType tableType, List<T> listJsonEntities, List<T> updatedList, List<T> listDatabaseEntities) {

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            final var runnables = new ConcurrentLinkedQueue<Runnable>();

            for (var entity : listJsonEntities) {
                runnables.add(() -> tableType.tableSyncStrategy.insertRow(entity));
            }

            for (var entity : updatedList) {
                runnables.add(() -> tableType.tableSyncStrategy.updateRow(entity));
            }

            for (var entity : listDatabaseEntities) {
                runnables.add(() -> tableType.tableSyncStrategy.deleteRow(entity.id));
            }

            runnables.forEach((runnable -> executor.execute(() -> QuarkusTransaction.requiringNew().run(runnable))));
        }
    }
}

