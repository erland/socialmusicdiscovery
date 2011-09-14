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

package org.socialmusicdiscovery.rcp.error;

import org.eclipse.ui.statushandlers.WorkbenchErrorHandler;

/**
 * A generic abstraction of unexpected application exceptions. In a bug-free and
 * properly configured application, these exceptions should never happen.
 * Extending {@link RuntimeException} to let handlers distinguish between
 * generic exceptions caused by programming errors, and exceptions that we catch
 * and re-throw. In the RCP application, a proprietary
 * {@link WorkbenchErrorHandler} can manage our specific errors.
 * 
 * @author Peer T�rngren
 * 
 */
public class FatalApplicationException extends RuntimeException {
	private static final long serialVersionUID = 31961810319470519L;

	public FatalApplicationException() {
	}

	public FatalApplicationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public FatalApplicationException(String arg0) {
		super(arg0);
	}

	public FatalApplicationException(Throwable arg0) {
		super(arg0);
	}

}
