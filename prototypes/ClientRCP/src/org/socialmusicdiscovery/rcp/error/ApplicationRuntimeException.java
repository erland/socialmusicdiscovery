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
 * @author Peer Törngren
 * 
 */
public class ApplicationRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 31961810319470519L;

	public ApplicationRuntimeException() {
	}

	public ApplicationRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ApplicationRuntimeException(String arg0) {
		super(arg0);
	}

	public ApplicationRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
