package com.google.gwt.dist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.stream.StreamSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.dist.compiler.Precompile;
import com.google.gwt.dist.compiler.communicator.Communicator;
import com.google.gwt.dist.compiler.communicator.impl.CommunicatorImpl;
import com.google.gwt.dist.compiler.impl.SessionManagerImpl;
import com.google.gwt.dist.linker.Link;
import com.google.gwt.dist.linker.LinkOptionsImpl;
import com.google.gwt.dist.util.ZipCompressor;
import com.google.gwt.dist.util.ZipDecompressor;

public class Application {

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private AgentsSettings settings;
	private static String AGENTS_SETTINGS_FILE_LOCATION = "config/config.xml";

	private static final Logger logger = Logger.getLogger(Application.class
			.getName());

	public static void main(String[] args) {
		ApplicationContext appContext = new FileSystemXmlApplicationContext(
				new File("config/applicationContext.xml").toString());
		Application app = (Application) appContext.getBean("application");
		app.loadSettings();
		app.start();
	}

	public Application() {

	}

	public void start() {

		List<Node> nodes = this.settings.getNodes();
		List<SessionManager> sessionManagers = new ArrayList<SessionManager>();
		// TODO: use springframework for this.
		Communicator communicator = new CommunicatorImpl();
		ZipCompressor compressor = new ZipCompressor();
		ZipDecompressor decompressor = new ZipDecompressor();
		for (Node n : nodes) {
			sessionManagers.add(new SessionManagerImpl(communicator, n,
					compressor, decompressor));
		}

		TreeLogger logger = new PrintWriterTreeLogger();
		Precompile precompile = new Precompile();
		precompile.run(logger);

		boolean precompileFinished = false;
		while (!precompileFinished) {
			for (SessionManager sm : sessionManagers) {
				precompileFinished = sm.start();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}

		LinkOptionsImpl linkOptions = new LinkOptionsImpl();
		Link link = new Link(linkOptions);
		link.run(logger);
	}

	public Marshaller getMarshaller() {
		return this.marshaller;
	}

	public AgentsSettings getSettings() {
		return this.settings;
	}

	public Unmarshaller getUnmarshaller() {
		return this.unmarshaller;
	}

	/**
	 * Loads settings from Application.AGENTS_SETTINGS_FILE_LOCATION TODO:
	 * Candidate for refactoring - File might be supplied as parameter.
	 */
	protected AgentsSettings loadSettings() {
		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(AGENTS_SETTINGS_FILE_LOCATION));
			this.settings = (AgentsSettings) this.unmarshaller
					.unmarshal(new StreamSource(is));
		} catch (XmlMappingException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage());
				}
			}
		}
		return settings;
	}

	public void setMarshaller(Marshaller m) {
		this.marshaller = m;
	}

	public void setUnmarshaller(Unmarshaller um) {
		this.unmarshaller = um;
	}
}