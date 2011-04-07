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

package org.socialmusicdiscovery.rcp.test;

import java.util.HashSet;
import java.util.Set;

import org.socialmusicdiscovery.rcp.event.AbstractObservable;

/**
 * A simple observable object for unit testing.
 * 
 * @author Peer Törngren
 */
public class AnObservable extends AbstractObservable {
	public static final String NAME = "name";
	public static final String CHILD = "child";
	public static final String CHILDREN = "children";
	
	private int nameIndex = 0;
	private String name;
	private AnObservable child;
	private Set<AnObservable> children = new HashSet<AnObservable>();
	private final String id;

	public AnObservable(String id) {
		this.id = id;
	}

	public AnObservable(AnObservable parent, String id) {
		parent.setChild(this);
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
	firePropertyChange(AnObservable.NAME, this.name, this.name = name);}

	public AnObservable getChild() {
		return child;
	}

	public void setChild(AnObservable child) {
		firePropertyChange(AnObservable.CHILD, this.child, this.child = child);}
	

	public Set<AnObservable> getChildren() {
		return children;
	}

	public void setChildren(Set<AnObservable> children) {
	updateSet(AnObservable.CHILDREN, this.children, children);}

	public void changeName() {
		setName("Name#"+nameIndex++);
	}

	public void remove(AnObservable... children) {
		Set<AnObservable > set = new HashSet<AnObservable >(getChildren());
		for (AnObservable c: children) {
			boolean changed = set.remove(c);
			assert changed : "Nothing changed - suspect bad test setup";
		}
		setChildren(set);
	}

	public void add(AnObservable... children) {
		Set<AnObservable > set = new HashSet<AnObservable >(getChildren());
		for (AnObservable c : children) {
			boolean changed  = set.add(c);
			assert changed : "Nothing changed - suspect bad test setup";
		}
		setChildren(set);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+":"+id;
	}
	
	
}