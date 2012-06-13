package com.hendyirawan.odatasandbox.threadinmemjaxrs;

import java.util.Arrays;

import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;

import org.core4j.Func;
import org.jboss.solder.servlet.event.Initialized;
import org.odata4j.producer.inmemory.InMemoryProducer;
import org.odata4j.producer.resources.DefaultODataApplication;
import org.odata4j.producer.resources.DefaultODataProducerProvider;
import org.odata4j.producer.resources.RootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ceefour
 */
public class ThreadInmemJaxrs {

	private transient Logger log = LoggerFactory.getLogger(ThreadInmemJaxrs.class);
	private InMemoryProducer producer;
	
	public void start(@Observes @Initialized ServletContext context) {
		log.info("Starting ThreadInmemJaxrs");
		// Create producer
		producer = new InMemoryProducer("ThreadInmemJaxrs", null, 100, null, null);
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
	}
	
	public void startJerseyServer() {
//		JerseyServer server = new JerseyServer("http://localhost:8887/ThreadInmemJaxrs.svc/", DefaultODataApplication.class, RootApplication.class)
//			.addJerseyRequestFilter(LoggingFilter.class);
//		server.start();
	}
}
