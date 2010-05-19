package com.google.gwt.dist.perms;

import java.io.Serializable;

import com.google.gwt.dev.CompilerOptions;
import com.google.gwt.dev.CompilePerms.CompilePermsOptions;
import com.google.gwt.dist.compiler.impl.CompileTaskOptionsImpl;

/**
 * Concrete class to implement compiler perm options.
 */
public class CompilePermsOptionsImpl extends CompileTaskOptionsImpl implements
		CompilePermsOptions, Serializable {

	private static final long serialVersionUID = -4757085237535925396L;
	private int localWorkers;
	private int[] permsToCompile;

	public CompilePermsOptionsImpl() {
	}

	public CompilePermsOptionsImpl(CompilerOptions other) {
		copyFrom(other);
	}

	public void copyFrom(CompilePermsOptions other) {
		super.copyFrom(other);
		setPermsToCompile(other.getPermsToCompile());
		setLocalWorkers(other.getLocalWorkers());
	}

	public int getLocalWorkers() {
		return localWorkers;
	}

	public int[] getPermsToCompile() {
		return (permsToCompile == null) ? null : permsToCompile.clone();
	}

	public void setLocalWorkers(int localWorkers) {
		this.localWorkers = localWorkers;
	}

	public void setPermsToCompile(int[] permsToCompile) {
		this.permsToCompile = (permsToCompile == null) ? null : permsToCompile
				.clone();
	}
}