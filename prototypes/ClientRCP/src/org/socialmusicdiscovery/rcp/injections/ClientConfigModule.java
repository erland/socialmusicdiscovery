package org.socialmusicdiscovery.rcp.injections;

import java.util.HashMap;
import java.util.Map;

import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.content.ObservableMedium;
import org.socialmusicdiscovery.rcp.content.ObservablePerson;
import org.socialmusicdiscovery.rcp.content.ObservableRecording;
import org.socialmusicdiscovery.rcp.content.ObservableRecordingSession;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.content.ObservableWork;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.RecordingSession;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.server.support.json.AbstractJSONProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sun.jersey.api.client.config.ClientConfig;

/**
 * Configure how to map from server API classes to concrete client classes.
 * @author Peer Törngren
 *
 */
public class ClientConfigModule extends AbstractModule {
	private static ClientConfig clientConfig;

	public static class JSONProvider extends AbstractJSONProvider {
		public JSONProvider() {
			super(true);
		}

		protected Map<Class, Class> getConversionMap() {
			Map<Class, Class> converters = new HashMap<Class, Class>();

			// converters.put(GlobalIdentity.class, Observable???.class);
			// converters.put(SMDIdentityReference.class, Observable???.class);
			
			converters.put(Artist.class, ObservableArtist.class);
			// converters.put(Classification.class, ObservableClassification.class);
			converters.put(Contributor.class, ObservableContributor.class);
			// converters.put(Credit.class, ObservableCredit.class);
			// converters.put(Label.class, ObservableLabel.class);
			converters.put(Medium.class, ObservableMedium.class);
			 converters.put(Person.class, ObservablePerson.class);
			// converters.put(PlayableElement.class, ObservablePlayableElement.class);
			converters.put(Recording.class, ObservableRecording.class);
			converters.put(RecordingSession.class, ObservableRecordingSession.class);
			// converters.put(Relation.class, ObservableSMDIdentityReference.class);
			converters.put(Release.class, ObservableRelease.class);
			// converters.put(Series.class, ObservableSeries.class);
			converters.put(Track.class, ObservableTrack.class);
			converters.put(Work.class, ObservableWork.class);

			return converters;
		}
	}

	@Override
	protected void configure() {
	}

	@Provides
	public ClientConfig provideClientConfig() {
		if (clientConfig == null) {
			clientConfig = DataSource.newClientConfig();
		}
		return clientConfig;
	}
}