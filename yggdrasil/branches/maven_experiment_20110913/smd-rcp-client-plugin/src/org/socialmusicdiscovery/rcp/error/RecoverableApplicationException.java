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
 * <p>
 * A generic abstraction of expected application exceptions. Even in a bug-free
 * application, these exceptions may happen. Extending {@link RuntimeException}
 * to avoid polluting the code with checked exceptions, but we need to let
 * handlers distinguish between generic exceptions caused by programming errors,
 * and exceptions that we catch and re-throw. In the RCP application, a
 * proprietary {@link WorkbenchErrorHandler} can manage our specific errors.
 * </p>
 * 
 * <p>
 * When this exception is thrown, it is possible that the user can fix the
 * problem and retry the operation. For this reason, the constructor accepts a
 * user-friendly hint on how to handle the error.
 * </p>
 * 
 * @author Peer T�rngren
 * 
 */
public class RecoverableApplicationException extends RuntimeException {

	private static final long serialVersionUID = -3965445820779294579L;
	private String hint;

	public RecoverableApplicationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RecoverableApplicationException(String arg0) {
		super(arg0);
	}

	public RecoverableApplicationException(String msg, String hint, Throwable e) {
		this(msg, e);
		this.hint = hint;
	}

	public String getHint() {
		return hint;
	}
}
