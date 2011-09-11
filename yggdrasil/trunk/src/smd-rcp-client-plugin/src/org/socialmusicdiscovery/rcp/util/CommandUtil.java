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

package org.socialmusicdiscovery.rcp.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISources;

/**
 * Some helpers for evaluating context and running commands.
 * 
 * @author Peer Törngren
 *
 */
public final class CommandUtil {

	private static final String[] EVALUATION_CONTEXT_KEYS = {
		ISources.ACTIVE_ACTION_SETS_NAME,
		ISources.ACTIVE_CONTEXT_NAME,
		ISources.ACTIVE_CURRENT_SELECTION_NAME,
		ISources.ACTIVE_EDITOR_ID_NAME,
		ISources.ACTIVE_EDITOR_INPUT_NAME,
		ISources.ACTIVE_EDITOR_NAME,
		ISources.ACTIVE_FOCUS_CONTROL_ID_NAME,
		ISources.ACTIVE_FOCUS_CONTROL_NAME,
		ISources.ACTIVE_MENU_EDITOR_INPUT_NAME,
		ISources.ACTIVE_MENU_NAME,
		ISources.ACTIVE_MENU_SELECTION_NAME,
		ISources.ACTIVE_PART_ID_NAME,
		ISources.ACTIVE_PART_NAME,
		ISources.ACTIVE_SHELL_NAME,
		ISources.ACTIVE_SITE_NAME,
		ISources.ACTIVE_WORKBENCH_WINDOW_ACTIVE_PERSPECTIVE_NAME,
		ISources.ACTIVE_WORKBENCH_WINDOW_IS_COOLBAR_VISIBLE_NAME,
		ISources.ACTIVE_WORKBENCH_WINDOW_IS_PERSPECTIVEBAR_VISIBLE_NAME,
		ISources.ACTIVE_WORKBENCH_WINDOW_NAME,
		ISources.ACTIVE_WORKBENCH_WINDOW_SHELL_NAME,
		ISources.SHOW_IN_INPUT,
		ISources.SHOW_IN_SELECTION,
	};

	private CommandUtil() {}

	public static <T> T getDefaultVariable(ExecutionEvent event) {
		EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
		T result = (T) ctx.getDefaultVariable();
		return result;
	}

	public static IEditorInput resolveEditorInput(ExecutionEvent event) {
		EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
//		String s = dump(ctx);
		return (IEditorInput) ctx.getVariable(ISources.ACTIVE_EDITOR_INPUT_NAME);
	}

	public static String dump(EvaluationContext ctx) {
		StringBuilder sb = new StringBuilder();
		for (String s : EVALUATION_CONTEXT_KEYS) {
			sb.append(s);
			sb.append('=');
			sb.append(ctx.getVariable(s));
			sb.append('\n');
		}
		String all = sb.toString();
		return all;
	}

}
