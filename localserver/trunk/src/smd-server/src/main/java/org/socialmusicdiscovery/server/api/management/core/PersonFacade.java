package org.socialmusicdiscovery.server.api.management.core;

import com.google.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.repository.core.PersonRepository;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/persons")
public class PersonFacade {
    @Inject
    private PersonRepository personRepository;

    @Inject
    private EntityManager em;

    public PersonFacade() {
        InjectHelper.injectMembers(this);}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public Collection<Person> getPersons(@QueryParam("name") String name) {
        if(name != null) {
            return personRepository.findByName(name);
        }else {
            Collection<Person> result = personRepository.findAll();
            return result;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public Person getPerson(@QueryParam("id") String id) {
        return personRepository.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Person createPerson(Person person) {
        em.getTransaction().begin();
        personRepository.create(person);
        em.getTransaction().commit();
        return person;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Person updatePerson(Person person) {
        em.getTransaction().begin();
        person = personRepository.merge(person);
        em.getTransaction().commit();
        return person;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public JSONObject deletePerson(@QueryParam("id") String id) throws JSONException {
        if(id!=null) {
            em.getTransaction().begin();
            Person person = personRepository.findById(id);
            if(person!=null) {
                personRepository.remove(person);
                em.getTransaction().commit();
                return new JSONObject().put("success",true);
            }else {
                em.getTransaction().commit();
            }
        }
        return new JSONObject().put("success",false);
    }
}
