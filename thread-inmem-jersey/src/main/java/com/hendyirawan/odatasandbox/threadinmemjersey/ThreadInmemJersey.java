package com.hendyirawan.odatasandbox.threadinmemjersey;

import java.util.Arrays;

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
public class ThreadInmemJersey {

	private transient Logger log = LoggerFactory.getLogger(ThreadInmemJersey.class);
	private InMemoryProducer producer;
	
	public void start(@Observes ContainerInitialized e) {
		log.info("Starting ThreadInmemJersey");
		// Create producer
		producer = new InMemoryProducer("ThreadInmemJersey", null, 100, null, null);
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
		JerseyServer server = new JerseyServer("http://localhost:8887/ThreadInmemJersey.svc/", DefaultODataApplication.class, RootApplication.class)
			.addJerseyRequestFilter(LoggingFilter.class);
		server.start();
	}
}
