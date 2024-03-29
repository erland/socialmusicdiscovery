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

package org.socialmusicdiscovery.yggdrasil.foundation.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource.Root;
import org.socialmusicdiscovery.yggdrasil.foundation.util.SMDUtil;
import org.socialmusicdiscovery.yggdrasil.foundation.util.WorkbenchUtil;

/**
 * <p>
 * Disconnect the client from the {@link DataSource}.
 * </p>
 * <p>
 * <b>NOTE:</b> If client is set to "auto-connect", it will effectively do
 * precisely that after this command, since the navigator will refresh its
 * content when the {@link Root}s are cleared. Hence, in "auto-connect mode",
 * this command is really a "reconnect" command; it will disconnect from the
 * current {@link DataSource}, but the data binding will most likely force a new
 * connection immediately.
 * </p>
 * 
 * @author Peer Törngren
 * 
 */
public class DisconnectDataSource extends AbstractHandler {
	public static final String COMMAND_ID = DisconnectDataSource.class.getName();	

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (WorkbenchUtil.closeAllEditors()) {
			SMDUtil.getDataSource().disconnect();
			fireHandlerChanged(new HandlerEvent(this, true, isEnabled()));
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return SMDUtil.getDataSource().isConnected();
	}
}
