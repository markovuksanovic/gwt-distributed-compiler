package com.google.gwt.dist.compiler.agent.events;

/**
 * All the objects that want to be norified of compile perms action being
 * completed should implement this interface.
 */
public interface CompilePermsListener {
	void onCompilePermsFinished();
}