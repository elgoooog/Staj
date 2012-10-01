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
		public static final JsonEvent START = new JsonEvent("start", null);
		public static final JsonEvent END = new JsonEvent("end", null);
		public static final JsonEvent START_OBJECT = new JsonEvent("startObj",
				null);
		public static final JsonEvent START_ARRAY = new JsonEvent("startArr",
				null);
		public static final JsonEvent END_OBJECT = new JsonEvent("endObj", null);
		public static final JsonEvent END_ARRAY = new JsonEvent("endArr", null);
		public static final JsonEvent NULL = new JsonEvent("null", null);
		public static final JsonEvent TRUE = new JsonEvent("true", null);
		public static final JsonEvent FALSE = new JsonEvent("false", null);

		protected final String value;
		protected final JsonEventType type;

		private JsonEvent(final String value, final JsonEventType type) {
			this.value = value;
			this.type = type;
		}

		@Override
		public String toString() {
			return type == null ? value : type + ":" + value;
		}

		protected static JsonEvent s(final String s) {
			return new JsonEvent(s, JsonEventType.STRING);
		}

		protected static JsonEvent n(final String n) {
			return new JsonEvent(n, JsonEventType.NUMBER);
		}

		protected static JsonEvent k(final String k) {
			return new JsonEvent(k, JsonEventType.KEY);
		}
	}

	public static class JsonEventType {
		protected static final JsonEventType STRING = new JsonEventType(
				"string");
		protected static final JsonEventType NUMBER = new JsonEventType(
				"number");
		protected static final JsonEventType KEY = new JsonEventType("key");

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
