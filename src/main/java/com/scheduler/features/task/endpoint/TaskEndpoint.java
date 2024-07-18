package com.scheduler.features.task.endpoint;

import com.scheduler.features.task.controller.TaskController;
import com.scheduler.features.task.dto.NewTaskDTO;
import com.scheduler.features.task.dto.TaskDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

@RequestScoped
@Path("task")
public class TaskEndpoint {

    @Inject
    @Claim(standard = Claims.sub)
    String userId;

    @Inject
    TaskController taskController;

    @POST
    public Response createTask(NewTaskDTO newTaskDTO) {
        final TaskDTO newTask = taskController.createTask(newTaskDTO, Long.parseLong(userId));
        return Response.ok(newTask).build();
    }
}
