/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.yggdrasil.datasource.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.socialmusicdiscovery.rcp.content.AbstractContributableEntity;
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.content.ObservableLabel;
import org.socialmusicdiscovery.rcp.content.ObservableMedium;
import org.socialmusicdiscovery.rcp.content.ObservablePerson;
import org.socialmusicdiscovery.rcp.content.ObservablePlayableElement;
import org.socialmusicdiscovery.rcp.content.ObservableRecording;
import org.socialmusicdiscovery.rcp.content.ObservableRecordingSession;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.content.ObservableSMDIdentityReference;
import org.socialmusicdiscovery.rcp.content.ObservableTrack;
import org.socialmusicdiscovery.rcp.content.ObservableWork;
import org.socialmusicdiscovery.rcp.util.GenericWritableList;
import org.socialmusicdiscovery.rcp.util.GenericWritableSet;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.model.core.Medium;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
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
 * <p>
 * Configure how to map from server API classes to concrete client classes.
 * </p>
 * <p>
 * <b>Note on collection conversion:</b><br>
 * We need to do a seemingly strange mapping where we convert a concrete class
 * to itself. This is logic buried deeply inside Gson so there is no obvious or
 * easy way around it.
 * 
 * The logic is that Gson:
 * <ol>
 * <li>Tries to find an exact mapping among the converters we provide.</li>
 * <li>If it can't find an exact mapping it tries to find a mapping from the
 * standard type converters for {@link Collection}, {@link List}, {@link Set}
 * etc.</li>
 * </ol>
 * 
 * 
 * We need to make it match in point 1 because else it will create a
 * {@link LinkedList} instead of {@link GenericWritableList}, and the only way
 * to do that is to register a converter for the exact match, which would be
 * {@link GenericWritableList}. Converters have to be specified for a specific
 * class/interface, you can't register generic converters that have logic inside
 * them that decide what to convert and what to not convert. So it looks a bit
 * strange to have a mapping from {@link GenericWritableList} to
 * {@link GenericWritableList} but it's needed to make Gson do what we want.
 * </p>
 * 
 * 
 * @author Peer Törngren
 * 
 */
public class ClientConfigModule extends AbstractModule {
	private static ClientConfig clientConfig;

	public static class JSONProvider extends AbstractJSONProvider {
		public JSONProvider() {
			super(true);
		}

		@SuppressWarnings("rawtypes")
		protected Map<Class, Class> getConversionMap() {
			Map<Class, Class> converters = new HashMap<Class, Class>();

			converters.put(Artist.class, ObservableArtist.class);
			// converters.put(Classification.class, ObservableClassification.class);
			converters.put(Contributor.class, ObservableContributor.class);
			// converters.put(Credit.class, ObservableCredit.class);
			converters.put(Label.class, ObservableLabel.class);
			converters.put(Medium.class, ObservableMedium.class);
			converters.put(Person.class, ObservablePerson.class);
			converters.put(PlayableElement.class, ObservablePlayableElement.class);
			converters.put(Recording.class, ObservableRecording.class);
			converters.put(RecordingSession.class, ObservableRecordingSession.class);
			// converters.put(Relation.class, ObservableSMDIdentityReference.class);
			converters.put(Release.class, ObservableRelease.class);
			// converters.put(Series.class, ObservableSeries.class);
			converters.put(Track.class, ObservableTrack.class);
			converters.put(Work.class, ObservableWork.class);
			converters.put(SMDIdentityReference.class, ObservableSMDIdentityReference.class);
			
			// handle type conversion of abstract classes
			converters.put(AbstractContributableEntity.class, AbstractContributableEntity.class);
			
			// handle type conversion of collection attributes
			converters.put(IObservableList.class, WritableList.class);
			converters.put(IObservableSet.class, WritableSet.class);
			converters.put(GenericWritableList.class, GenericWritableList.class);
			converters.put(GenericWritableSet.class, GenericWritableSet.class);

			return converters;
		}

		@SuppressWarnings("rawtypes")
		protected Map<String, Class> getObjectTypeConversionMap() {
		    Map<String, Class> converters = new HashMap<String,Class>();
		
		    converters.put(Release.class.getSimpleName(), ObservableRelease.class);
		    converters.put(Work.class.getSimpleName(), ObservableWork.class);
		    converters.put(Recording.class.getSimpleName(), ObservableRecording.class);
		    converters.put(RecordingSession.class.getSimpleName(), ObservableRecordingSession.class);
		    return converters;
		}
	}
	
    @Override
	protected void configure() {
	}

	@Provides
	public ClientConfig provideClientConfig() {
		if (clientConfig == null) {
			clientConfig = RESTDataSource.newClientConfig();
		}
		return clientConfig;
	}
}