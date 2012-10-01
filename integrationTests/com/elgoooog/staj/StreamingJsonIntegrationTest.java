package com.elgoooog.staj;

import org.junit.Test;

import com.elgoooog.staj.StreamingJsonReader.JsonEvent;

public class StreamingJsonIntegrationTest {
	@Test
	public void testIt1() {
		final StreamingJsonReaderFactory factory = new StreamingJsonReaderFactory();
		final StreamingJsonReader reader = factory.createStreamingJsonReader(
				getClass().getResourceAsStream("test1.json"), 1);

		JsonEvent jsonEvent = reader.next();
		while (jsonEvent != JsonEvent.END) {
			System.out.println(jsonEvent);
			jsonEvent = reader.next();
		}
	}

	@Test
	public void testIt2() {
		final StreamingJsonReaderFactory factory = new StreamingJsonReaderFactory();
		final StreamingJsonReader reader = factory.createStreamingJsonReader(
				getClass().getResourceAsStream("test2.json"), 5);

		JsonEvent jsonEvent = reader.next();
		while (jsonEvent != JsonEvent.END) {
			System.out.println(jsonEvent);
			jsonEvent = reader.next();
		}
	}

	@Test
	public void testIt3() {
		final StreamingJsonReaderFactory factory = new StreamingJsonReaderFactory();
		final StreamingJsonReader reader = factory.createStreamingJsonReader(
				getClass().getResourceAsStream("test3.json"), 5);

		JsonEvent jsonEvent = reader.next();
		while (jsonEvent != JsonEvent.END) {
			System.out.println(jsonEvent);
			jsonEvent = reader.next();
		}
	}
}
