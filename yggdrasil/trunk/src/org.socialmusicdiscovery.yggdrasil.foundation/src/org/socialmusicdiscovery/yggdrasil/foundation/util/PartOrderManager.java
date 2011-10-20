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

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.socialmusicdiscovery.server.business.model.core.Part;

/**
 * Observes an observable list of {@link Part} instances, updating the
 * {@link Part#setNumber(Integer)} property when the list order is changed. This
 * approach will probably be very slow if applied to large lists that change
 * order frequently, but we expect reasonable number of parts in works
 * (typically 1-10, certainly less than 100).
 * 
 * @author Peer Törngren
 * 
 */
public class PartOrderManager implements IListChangeListener {

	@Override
	public void handleListChange(ListChangeEvent event) {
		int i = 1;
		for (Object o: event.getObservableList()) {
			Part e = (Part) o;
			e.setNumber(Integer.valueOf(i++));
		}
	}

	public static void manage(IObservableList list) {
		assert allElementsAreParts(list);
		list.addListChangeListener(new PartOrderManager());
	}

	/*
	 * Assert that all elements in supplied list are {@link Part}s, 
	 * or we will get strange error later on. Should only be called 
	 * from within an assert statement, we don't want this to be called 
	 * in normal runtime context.  
	 */
	private static boolean allElementsAreParts(IObservableList list) {
		for (Object object : list) {
			assert object instanceof Part : "Not a Part: "+object;
		}
		return true;
	}

}
