package org.example.service;

import java.util.concurrent.ThreadLocalRandom;

import org.example.akka.actor.RecoverableException;
import org.example.akka.actor.Task;
import org.example.akka.actor.Task.Result;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskService {
	public Result doTask(Task t) {
		log.info("doing task: " + t.getId());
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
