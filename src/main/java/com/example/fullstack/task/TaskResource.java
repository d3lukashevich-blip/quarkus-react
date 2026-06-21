package com.example.fullstack.task;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/api/v1/tasks")
public class TaskResource {

    private final TaskService taskService;

    @Inject
    public TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }

    @GET
    public Uni<List<Task>> get() {
        return taskService.listForUser();
    }

    @GET
    @Path("{id}")
    public Uni<Task> getById(@PathParam("id") long id) {
        return taskService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    public Uni<Task> create(Task task) {
        return taskService.create(task);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Task> update(@PathParam("id") long id, Task task) {
        task.id = id;
        return taskService.update(task);
    }

    @PUT
    @Path("/{id}/complete")
    public Uni<Boolean> setComplete(@PathParam("id") long id, boolean complete) {
        return taskService.setComplete(id, complete);
    }
}
