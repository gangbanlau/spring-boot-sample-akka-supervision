# spring-boot-sample-akka-supervision

demo how can a supervisor handle the failures of its subordinates.

. org.example.service.TaskService
  this is a standard Spring Service, demo some dangerous things

. org.example.akka.actor.TaskHandler
  this is a actor handling task. when it recv a task(DTO), it will call above Spring
  Service and reply back result

. org.example.akka.actor.TaskService
  this is a supervisor actor, it hands tasks to the child actors. when it catch
  RecoverableException, it will restart child actor.

there are test code bring all these together. take a look TaskServiceTests, it
also demo how other Spring Services can call "Akka managed" Spring Services.
