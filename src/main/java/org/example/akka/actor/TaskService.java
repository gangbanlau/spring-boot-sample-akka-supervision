package org.example.akka.actor;

import java.util.HashMap;
import java.util.Map;

import org.example.akka.di.SpringExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Status;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;

@Component("taskServiceActor")
@Scope("prototype")
public class TaskService extends AbstractLoggingActor {
	private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
	
    @Autowired
    private SpringExtension springExtension;
    
	Map<ActorRef, ActorRef> pendingWorkers = new HashMap<>();
	 
	private SupervisorStrategy strategy = new OneForOneStrategy(false,
			DeciderBuilder.match(RecoverableException.class, e -> {
				//log().warning("Evaluation of a recoverable failed, restarting.");
				logger.info("Evaluation of a recoverable failed, restarting.");
				return SupervisorStrategy.restart();
				/*
			}).match(ArithmeticException.class, e -> {
				log().error("Evaluation failed because of: {}", e.getMessage());
				notifyConsumerFailure(sender(), e);
				return SupervisorStrategy.stop();
				*/
			}).match(Throwable.class, e -> {
				//log().error("Unexpected failure: {}", e.getMessage());
				logger.warn("Unexpected failure: {}", e.getMessage());
				notifyConsumerFailure(sender(), e);
				return SupervisorStrategy.stop();
			}//).matchAny(e -> SupervisorStrategy.escalate()
			).build());

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	private void notifyConsumerFailure(ActorRef worker, Throwable failure) {
		// Status.Failure is a message type provided by the Akka library. The
		// reason why it is used is because it is recognized by the "ask"
		// pattern and the Future returned by ask will fail with the provided exception.
		ActorRef pending = pendingWorkers.get(worker);
		if (pending != null) {
			pending.tell(new Status.Failure(failure), self());
			pendingWorkers.remove(worker);
		}
	}

	private void notifyConsumerSuccess(ActorRef worker, Task.Result result) {
		ActorRef pending = pendingWorkers.get(worker);
		if (pending != null) {
			pending.tell(result, self());
			pendingWorkers.remove(worker);
		}
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Task.class, task -> {
			// We delegate the dangerous task to a worker,
			// passing the
			// task as a constructor argument to the actor.
			ActorRef worker = getContext().actorOf(springExtension.props("taskHandlerActor", task));			
			pendingWorkers.put(worker, sender());
		}).match(Task.Result.class, r -> notifyConsumerSuccess(sender(), r)).build();
	}

}
