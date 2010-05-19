package com.google.gwt.dist.compiler.agent.processor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.CompilePerms;
import com.google.gwt.dev.CompilePerms.CompilePermsOptions;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.dist.ProcessingState;
import com.google.gwt.dist.compiler.agent.events.CompilePermsListener;
import com.google.gwt.dist.compiler.agent.impl.CompilePermsOptionsImpl;
import com.google.gwt.dist.util.Util;

/**
 * CompilePermsService executes actual CompilePerms operation. This is a long
 * running operation and is intended to be run in background. It uses
 * publish/subscribe model to notify other objects about a job being completed.
 */
public class CompilePermsService implements Runnable {

	private CompilePermsListener listener;
	private List<String> moduleNames;
	private File tempStorage;

	public CompilePermsService(File tempStorage) {
		this.tempStorage = tempStorage;
	}

	public void compilePermsFinished() {
		listener.onDataProcessorStateChanged(ProcessingState.COMPLETED);
	}
	
	public CompilePermsListener getCompilePermsListener() {
		return this.listener;
	}

	@Override
	public void run() {
		try {
			File uncompressedSrc = new File(tempStorage + File.separator
					+ "src" + File.separator);

			Util.addUrl(uncompressedSrc.toURI().toURL());

			TreeLogger logger = new PrintWriterTreeLogger();
			((PrintWriterTreeLogger) logger).setMaxDetail(TreeLogger.INFO);

			// Compile Perms using the input data stored in tempStorage.
			File workDir = new File("uncompressed" + File.separator + "work");

			final CompilePermsOptions options = new CompilePermsOptionsImpl();

			options.setModuleNames(moduleNames);
			options.setWorkDir(workDir);
			int perms[] = { 0, 1, 2, 3, 4, 5 };
			options.setPermsToCompile(perms);
			
			compilePermsStarted();
			new CompilePerms(options).run(logger);
			compilePermsFinished();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnableToCompleteException e) {
			e.printStackTrace();
		}

	}

	public void compilePermsStarted() {
		listener.onDataProcessorStateChanged(ProcessingState.INPROGRESS);
	}
	
	public void setModuleNames(List<String> moduleNames) {
		this.moduleNames = moduleNames;
	}
	
	public void setCompilePermsListener(CompilePermsListener listener) {
		this.listener = listener;
	}

}
