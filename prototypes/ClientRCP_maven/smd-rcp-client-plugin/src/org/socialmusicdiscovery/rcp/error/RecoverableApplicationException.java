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
 * @author Peer Törngren
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
