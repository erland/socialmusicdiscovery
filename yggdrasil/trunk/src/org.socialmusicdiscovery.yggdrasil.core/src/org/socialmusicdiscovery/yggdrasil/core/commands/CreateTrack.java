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

package org.socialmusicdiscovery.yggdrasil.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.yggdrasil.core.editors.track.TrackDialog;
import org.socialmusicdiscovery.yggdrasil.foundation.Activator;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableRelease;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableTrack;
import org.socialmusicdiscovery.yggdrasil.foundation.util.CommandUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.NotYetImplemented;

/**
 * Creates a new instance.
 * @author Peer Törngren
 */
public class CreateTrack extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ObservableRelease r = (ObservableRelease) CommandUtil.resolveEditorInput(event); 
		return newInstance(r);
	}

	private ObservableTrack newInstance(ObservableRelease r) {
		if (NotYetImplemented.confirm("Create new Track")) {
			ObservableTrack c = new TrackDialog().createChild(r);
			if (c!=null) {
				Activator.getDefault().getDataSource().persist(new Shell(), c);
			}
			return c;
		}
		return null;
	}
}
