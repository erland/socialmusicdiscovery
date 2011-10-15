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

package org.socialmusicdiscovery.yggdrasil.foundation.util;

import java.text.MessageFormat;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.yggdrasil.foundation.content.AbstractObservableEntity;

/**
 * Observes an observable list of {@link AbstractObservableEntity} instances, 
 * updating the {@link AbstractObservableEntity#setSortAs(String)} property 
 * when the list order is changed. This approach will probably be very slow 
 * if applied to large lists that change order frequently; primary objective 
 * is to handle small lists such as {@link Work#getParts()}.   
 * 
 * @author Peer Törngren
 *
 */
public class ListOrderManager implements IListChangeListener {

	private static final String SORT_AS_PATTERN = "{0,number,000}. {1}";

	@Override
	public void handleListChange(ListChangeEvent event) {
		int i = 1;
		for (Object o: event.getObservableList()) {
			AbstractObservableEntity e = (AbstractObservableEntity) o;
			String sortAs = MessageFormat.format(SORT_AS_PATTERN, Integer.valueOf(i++), e.getName());
			e.setSortAs(sortAs);
		}
	}

	public static void manage(IObservableList list) {
		list.addListChangeListener(new ListOrderManager());
	}

}
