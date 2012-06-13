package org.soluvas.odataproduct;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.core4j.Func;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.odata4j.jersey.producer.server.JerseyServer;
import org.odata4j.producer.inmemory.InMemoryProducer;
import org.odata4j.producer.resources.DefaultODataApplication;
import org.odata4j.producer.resources.DefaultODataProducerProvider;
import org.odata4j.producer.resources.RootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.container.filter.LoggingFilter;

/**
 * @author ceefour
 */
public class OdataProduct {

	private transient Logger log = LoggerFactory.getLogger(OdataProduct.class);
	private InMemoryProducer producer;
	
	public void start(@Observes ContainerInitialized e) {
		log.info("Starting OdataProduct");
		// Create producer
		producer = new InMemoryProducer("OdataProduct", null, 100, null, null);
		// Register entity set
		producer.register(Thread.class, "Threads", new Func<Iterable<Thread>>() {
			@Override
			public Iterable<Thread> apply() {
				ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
				while (threadGroup.getParent() != null)
					threadGroup = threadGroup.getParent();
				Thread[] threads = new Thread[1000];
				int threadCount = threadGroup.enumerate(threads, true);
				return Arrays.asList(Arrays.copyOf(threads, threadCount));
			}
		}, "Id");
		// Set the default producer instance
		DefaultODataProducerProvider.setInstance(producer);
		// Start Jersey server
		startJerseyServer();
	}
	
	public void startJerseyServer() {
		JerseyServer server = new JerseyServer("http://localhost:8887/OdataProduct.svc/", DefaultODataApplication.class, RootApplication.class)
			.addJerseyRequestFilter(LoggingFilter.class);
		server.start();
	}
}
