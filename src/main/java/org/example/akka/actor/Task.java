package org.example.akka.actor;

import lombok.Data;

@Data
public class Task {
	final long id;
	
	@Data
	public static class Result {
		final int resultCode;
		final String resultText;
	}
}
