package com.se.demorestdb.resource;

import com.se.demorestdb.model.DanhMuc;
import com.se.demorestdb.service.DanhMucService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/danhmuc")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DanhMucResource {
    @Inject
    private DanhMucService service;

    @GET
    public Response findAll() {
        return Response.ok(service.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        DanhMuc result = service.findById(id);
        return result == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(result).build();
    }

    @POST
    public Response insert(DanhMuc danhMuc) {
        return Response.status(Response.Status.CREATED)
                .entity(service.insert(danhMuc)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, DanhMuc danhMuc) {
        DanhMuc result = service.update(id, danhMuc);
        return result == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        return service.delete(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
