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

package org.socialmusicdiscovery.rcp.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.socialmusicdiscovery.rcp.content.AbstractDependentEntity;
import org.socialmusicdiscovery.rcp.content.Deletable;
import org.socialmusicdiscovery.rcp.util.CommandUtil;
import org.socialmusicdiscovery.rcp.util.NotYetImplemented;
import org.socialmusicdiscovery.rcp.util.WorkbenchUtil;

/**
 * Deletes {@link Deletable} instances and all dependents (if any).
 * 
 * @author Peer Törngren
 *
 */
public class Delete extends AbstractHandler implements IHandler {

	@Override
	public Boolean execute(ExecutionEvent event) throws ExecutionException {
		List<? extends Deletable> victims = CommandUtil.getDefaultVariable(event);
		List<? extends AbstractDependentEntity> dependents = resolveDependents(victims);
		
		boolean isConfirmed = MessageDialog.openConfirm(null, "Delete", "Delete "+victims.size()+" element(s) and " +dependents.size() + "dependents? This action can NOT be undone!");
		isConfirmed &= NotYetImplemented.confirm("Delete");
		if (isConfirmed) {
			if (WorkbenchUtil.closeEditors(victims)) {
				for (AbstractDependentEntity d : dependents) {
					d.delete();
				}
				for (Deletable v : victims) {
					v.delete();
				}
			}
		}
		return Boolean.valueOf(isConfirmed);
	}
	
	private <T extends AbstractDependentEntity> List<T> resolveDependents(Collection<? extends Deletable> primaryVictims) {
		List<T> result = new ArrayList<T>();
		for (Deletable deletable : primaryVictims) {
			Collection<T> dependentsToDelete = deletable.getDeletableDependents();
			result.addAll(dependentsToDelete);
		}
		return result;
	}

}
