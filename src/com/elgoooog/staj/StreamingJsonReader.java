package com.elgoooog.staj;

import java.util.LinkedList;

public class StreamingJsonReader {
	private final LinkedList<JsonEvent> jsonEvents;
	private final TokenReader tokenReader;
	private final int bufferSize;
	private volatile boolean done;

	public StreamingJsonReader(final TokenReader reader,
			final int eventsToBuffer) {
		jsonEvents = new LinkedList<>();
		tokenReader = reader;
		bufferSize = eventsToBuffer > 1 ? eventsToBuffer : 1;
		done = false;
		jsonEvents.add(JsonEvent.START);
		new ReaderThread().start();
	}

	public JsonEvent next() {
		synchronized (this) {
			while (true) {
				if (jsonEvents.size() > 0) {
					final JsonEvent event = jsonEvents.removeFirst();
					notifyAll();
					return event;
				} else if (done) {
					throw new IllegalStateException(
							"JsonEvent END has already been sent.  There is nothing left.");
				} else {
					doWait();
				}
			}
		}
	}

	protected class ReaderThread extends Thread {
		private final StreamingJsonParser parser = new StreamingJsonParser(
				tokenReader);

		@Override
		public void run() {
			while (!done) {
				synchronized (StreamingJsonReader.this) {
					if (jsonEvents.size() < bufferSize) {
						try {
							final JsonEvent event = parser.readNext();
							if (event == JsonEvent.END) {
								done = true;
							}

							jsonEvents.add(event);
						} finally {
							StreamingJsonReader.this.notifyAll();
						}
					} else {
						doWait();
					}
				}
			}
		}
	}

	protected void doWait() {
		while (true) {
			try {
				wait();
				break;
			} catch (final InterruptedException e) {
				// keep going
			}
		}
	}

	public static class JsonEvent {
		public static final JsonEvent START = new JsonEvent("start",
				JsonEventType.START);
		public static final JsonEvent END = new JsonEvent("end",
				JsonEventType.END);
		public static final JsonEvent START_OBJECT = new JsonEvent("startObj",
				JsonEventType.START_OBJECT);
		public static final JsonEvent START_ARRAY = new JsonEvent("startArr",
				JsonEventType.START_ARRAY);
		public static final JsonEvent END_OBJECT = new JsonEvent("endObj",
				JsonEventType.END_OBJECT);
		public static final JsonEvent END_ARRAY = new JsonEvent("endArr",
				JsonEventType.END_ARRAY);
		public static final JsonEvent NULL = new JsonEvent("null",
				JsonEventType.NULL);
		public static final JsonEvent TRUE = new JsonEvent("true",
				JsonEventType.TRUE);
		public static final JsonEvent FALSE = new JsonEvent("false",
				JsonEventType.FALSE);

		protected final String value;
		protected final JsonEventType type;

		private JsonEvent(final String value, final JsonEventType type) {
			this.value = value;
			this.type = type;
		}

		@Override
		public String toString() {
			return type.toString().equals(value) ? value : type + ":" + value;
		}

		protected static JsonEvent string(final String s) {
			return new JsonEvent(s, JsonEventType.STRING);
		}

		protected static JsonEvent number(final String n) {
			return new JsonEvent(n, JsonEventType.NUMBER);
		}

		protected static JsonEvent key(final String k) {
			return new JsonEvent(k, JsonEventType.KEY);
		}
	}

	public static class JsonEventType {
		protected static final JsonEventType STRING = new JsonEventType(
				"string");
		protected static final JsonEventType NUMBER = new JsonEventType(
				"number");
		protected static final JsonEventType KEY = new JsonEventType("key");
		protected static final JsonEventType START = new JsonEventType("start");
		protected static final JsonEventType END = new JsonEventType("end");
		protected static final JsonEventType START_OBJECT = new JsonEventType(
				"startObj");
		protected static final JsonEventType START_ARRAY = new JsonEventType(
				"startArr");
		protected static final JsonEventType END_OBJECT = new JsonEventType(
				"endObj");
		protected static final JsonEventType END_ARRAY = new JsonEventType(
				"endArr");
		protected static final JsonEventType NULL = new JsonEventType("null");
		protected static final JsonEventType TRUE = new JsonEventType("true");
		protected static final JsonEventType FALSE = new JsonEventType("false");

		protected final String name;

		private JsonEventType(final String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
