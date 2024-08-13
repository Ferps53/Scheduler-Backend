package com.scheduler.core.table.sync.controller.strategies;

import com.scheduler.core.auth.repository.UserRepository;
import com.scheduler.features.task.dto.TaskDTO;
import com.scheduler.features.task.model.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.concurrent.CopyOnWriteArrayList;


@Transactional()
@ApplicationScoped
public class TaskSyncStrategy implements TableSyncStrategy<Task, TaskDTO>, PanacheRepository<Task> {

    final UserRepository userRepository = new UserRepository();

    @Override
    public void deleteRow(long... id) {

        System.out.println("Deleting id: " + id[0]);
        deleteById(id[0]);
    }

    @Override
    public void insertRow(Task entity) {

        entity.id = null;
        entity.user = userRepository.findById(entity.user.id);
        persist(entity);
        System.out.println("Inserted: " + entity.id);
    }

    @Override
    public void updateRow(Task entity) {

        Parameters params = Parameters.with("title", entity.title)
                .and("desc", entity.description)
                .and("concludedAt", entity.concludedAt)
                .and("isConcluded", entity.isConcluded)
                .and("id", entity.id)
                .and("userId", entity.user.id)
                .and("expiresIn", entity.expiresIn);

        entity.user = userRepository.findById(entity.user.id);
        System.out.println("Updating entity: " + entity.id);
        update("set title = :title, description = :desc, concludedAt = :concludedAt, isConcluded = :isConcluded, expiresIn = :expiresIn where id = :id and user.id = :userId", params);
    }

    @Override
    public CopyOnWriteArrayList<TaskDTO> getTableDto(long... userId) {

        return new CopyOnWriteArrayList<>(find("user.id = ?1", userId[0]).project(TaskDTO.class).list());
    }

    @Override
    public CopyOnWriteArrayList<Task> getTableOrm(long... userId) {

        return new CopyOnWriteArrayList<>(find("user.id = ?1", userId[0]).list());
    }
}
