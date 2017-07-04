package org.example.akka.actor;

import org.example.akka.actor.Task.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;

@Component("taskHandlerActor")
@Scope("prototype")
public class TaskHandler extends AbstractLoggingActor {

	@Autowired
	org.example.service.TaskService service;
	
	Task task;
	
	public TaskHandler(Task t) {
		this.task = t;
	}
	
	@Override
	public void preStart() {
		getContext().self().tell(this.task, getContext().parent());
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Task.class, task -> {
			Result r = service.doTask(task);
			
			getContext().parent().tell(new Task.Result(r.getResultCode(), r.getResultText()), self());
	        // Don't forget to stop the actor after it has nothing more to do
			getContext().stop(self());			
		}).build();
	}

}
