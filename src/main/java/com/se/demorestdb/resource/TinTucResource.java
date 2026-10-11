package com.se.demorestdb.resource;

import com.se.demorestdb.model.TinTuc;
import com.se.demorestdb.service.TinTucService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tintuc")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TinTucResource {
    @Inject
    private TinTucService service;

    @GET
    public Response findAll() {
        return Response.ok(service.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") int id) {
        TinTuc result = service.findById(id);
        return result == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(result).build();
    }

    @POST
    public Response insert(TinTuc tinTuc) {
        TinTuc result = service.insert(tinTuc);
        return result == null
                ? Response.status(Response.Status.BAD_REQUEST)
                    .entity("MADM khong ton tai").type(MediaType.TEXT_PLAIN).build()
                : Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, TinTuc tinTuc) {
        TinTuc result = service.update(id, tinTuc);
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

    // Các API JOIN và thống kê trong queries_tintucdb.pdf
    @GET
    @Path("/queries/danh-muc")
    public Response findAllWithCategory() {
        return Response.ok(service.findAllWithCategory()).build();
    }

    @GET
    @Path("/queries/theo-danh-muc")
    public Response findByCategoryName(@QueryParam("ten") String tenDanhMuc) {
        if (tenDanhMuc == null || tenDanhMuc.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(service.findByCategoryName(tenDanhMuc)).build();
    }

    @GET
    @Path("/queries/thong-ke")
    public Response countByCategory() {
        return Response.ok(service.countByCategory()).build();
    }

    @GET
    @Path("/queries/tu-n-tin")
    public Response categoriesWithAtLeast(@DefaultValue("3") @QueryParam("min") int minimum) {
        return Response.ok(service.categoriesWithAtLeast(minimum)).build();
    }

    @GET
    @Path("/queries/nhieu-tin-nhat")
    public Response categoriesWithMostNews() {
        return Response.ok(service.categoriesWithMostNews()).build();
    }
}
