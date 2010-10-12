package org.socialmusicdiscovery.server.api.management.core;

import com.google.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.repository.core.ArtistRepository;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/artists")
public class ArtistFacade {
    @Inject
    private ArtistRepository artistRepository;

    @Inject
    private EntityManager em;

    public ArtistFacade() {
        InjectHelper.injectMembers(this);}
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public Collection<Artist> getArtists(@QueryParam("name") String name) {
        if(name != null) {
            return artistRepository.findByName(name);
        }else {
            return artistRepository.findAll();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public Artist getArtist(@QueryParam("id") String id) {
        return artistRepository.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Artist createArtist(Artist artist) {
        em.getTransaction().begin();
        artistRepository.create(artist);
        em.getTransaction().commit();
        return artist;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Artist updateArtist(Artist artist) {
        em.getTransaction().begin();
        artist = artistRepository.merge(artist);
        em.getTransaction().commit();
        return artist;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public JSONObject deleteArtist(@QueryParam("id") String id) throws JSONException {
        if(id!=null) {
            em.getTransaction().begin();
            Artist artist = artistRepository.findById(id);
            if(artist != null) {
                artistRepository.remove(artist);
                em.getTransaction().commit();
                return new JSONObject().put("success",true);
            }else {
                em.getTransaction().commit();
            }
        }
        return new JSONObject().put("success",false);
    }
}
