package org.socialmusicdiscovery.rcp.error;

public class NotYetImplementedException extends RuntimeException {

	private static final long serialVersionUID = 2436515802934587805L;

	public NotYetImplementedException() {
		super("Not Yet Implemented");
	}
	public NotYetImplementedException(String arg0) {
		super("Not Yet Implemented: "+arg0);
	}

}
