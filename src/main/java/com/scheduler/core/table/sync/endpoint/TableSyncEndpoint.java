package com.scheduler.core.table.sync.endpoint;

import com.scheduler.core.table.sync.controller.TableSyncController;
import com.scheduler.core.table.sync.dto.TableSyncDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("table-sync")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TableSyncEndpoint {

    @Inject
    TableSyncController tableSyncController;

    @PUT
    public Response synchronizeTables(List<TableSyncDTO> listTableSyncDTO) {
        final var tableSyncReturnDTOS = tableSyncController.syncAll(listTableSyncDTO);
        return Response.ok(tableSyncReturnDTOS).build();
    }
}
