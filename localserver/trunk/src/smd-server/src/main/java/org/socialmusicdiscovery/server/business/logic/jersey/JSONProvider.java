package org.socialmusicdiscovery.server.business.logic.jersey;

import com.sun.jersey.spi.resource.Singleton;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.subjective.*;
import org.socialmusicdiscovery.server.support.json.AbstractJSONProvider;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Provider
@Produces(MediaType.APPLICATION_JSON)
public final class JSONProvider extends AbstractJSONProvider {
    public JSONProvider() {
        super(true);
    }

    @Override
    protected Map<Class, Class> getConversionMap() {
        Map<Class, Class> converters = new HashMap<Class,Class>();

        converters.put(Label.class, LabelEntity.class);
        converters.put(Release.class, ReleaseEntity.class);
        converters.put(Contributor.class, ContributorEntity.class);
        converters.put(Artist.class, ArtistEntity.class);
        converters.put(Person.class, PersonEntity.class);
        converters.put(Medium.class, MediumEntity.class);
        converters.put(Track.class, TrackEntity.class);
        converters.put(PlayableElement.class, PlayableElementEntity.class);
        converters.put(RecordingSession.class, RecordingSessionEntity.class);
        converters.put(Recording.class, RecordingEntity.class);
        converters.put(Work.class, WorkEntity.class);
        converters.put(SMDIdentityReference.class, SMDIdentityReferenceEntity.class);
        converters.put(Classification.class, ClassificationEntity.class);
        converters.put(GlobalIdentity.class, GlobalIdentityEntity.class);
        converters.put(Relation.class, SMDIdentityReferenceEntity.class);
        converters.put(Credit.class, CreditEntity.class);
        converters.put(Series.class, SeriesEntity.class);

        return converters;
    }
}