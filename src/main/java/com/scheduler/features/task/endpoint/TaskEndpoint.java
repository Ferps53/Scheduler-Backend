package com.scheduler.features.task.endpoint;

import com.scheduler.features.task.controller.TaskController;
import com.scheduler.features.task.dto.NewTaskDTO;
import com.scheduler.features.task.dto.TaskDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import java.util.List;

@RequestScoped
@Path("task")
public class TaskEndpoint {

    @Inject
    @Claim(standard = Claims.sub)
    String userId;

    @Inject
    TaskController taskController;

    @GET
    public Response getTasks() {
        final List<TaskDTO> taskList = taskController.getTasksNotInTrashBin(Long.parseLong(userId));
        return Response.ok(taskList).build();
    }

    @GET
    @Path("{id}")
    public Response getTaskById(@PathParam("id") Long taskId) {
        final TaskDTO task = taskController.getTaskById(taskId, Long.parseLong(userId));
        return Response.ok(task).build();
    }

    @PATCH
    @Path("/{id}")
    public Response patchTask(@PathParam("id") Long taskId, NewTaskDTO newTaskDTO) {
        final TaskDTO taskDTO = taskController.patchTask(taskId, Long.parseLong(userId), newTaskDTO);
        return Response.ok(taskDTO).build();
    }

    @POST
    public Response createTask(NewTaskDTO newTaskDTO) {
        final TaskDTO newTask = taskController.createTask(newTaskDTO, Long.parseLong(userId));
        return Response.ok(newTask).build();
    }
}
