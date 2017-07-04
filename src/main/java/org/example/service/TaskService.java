package org.example.service;

import java.util.concurrent.ThreadLocalRandom;

import org.example.akka.actor.RecoverableException;
import org.example.akka.actor.Task;
import org.example.akka.actor.Task.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
	private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
	
	public Result doTask(Task t) {
		logger.info("doing task: " + t.getId());
		flakiness();
		return new Result(200, "OK");
	}
	
	private void flakiness() throws RecoverableException {
		double d = ThreadLocalRandom.current().nextDouble();
		if (d < 0.2)
			throw new RuntimeException("unknown failed");
		else if (d < 0.5)
			throw new RecoverableException();
	}	
}
