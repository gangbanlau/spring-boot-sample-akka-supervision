package org.example.akka.actor;

import java.util.concurrent.TimeUnit;

import org.example.akka.actor.Task.Result;
import org.example.akka.di.SpringExtension;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import static akka.pattern.Patterns.ask;
import static akka.japi.Util.classTag;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTests {
	private static final Logger logger = LoggerFactory.getLogger(TaskServiceTests.class);
	
	@Autowired
	ActorSystem actorSystem;
	
    @Autowired
    private SpringExtension springExtension;
    	
	@Test
	public void test() throws Exception {
		ActorRef serviceActor =
				actorSystem.actorOf(springExtension.props("taskServiceActor", null));
		
	    FiniteDuration duration = Duration.create(2, TimeUnit.SECONDS);
	    Task task = new Task(1L);
	    try {
	    	Result result = Await.result(ask(serviceActor, task, new Timeout(duration)).mapTo(classTag(Result.class)), duration);
	    	logger.info("Got result: " + result);
	    }
	    catch (Exception e) {
	    	logger.warn("", e);
	    }
	    
	    Await.ready(actorSystem.terminate(), Duration.Inf());		
	}
	
	/*
    FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
    Future<Object> awaitable = Patterns.ask(workerActor, new WorkerActor.Response(), Timeout.durationToTimeout(duration));

    logger.info("Response: " + Await.result(awaitable, duration));
    */	
}
