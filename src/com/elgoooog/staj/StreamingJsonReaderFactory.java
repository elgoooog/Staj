package com.elgoooog.staj;

import java.io.InputStream;
import java.io.Reader;

public class StreamingJsonReaderFactory {
	public StreamingJsonReader createStreamingJsonReader(final InputStream is) {
		return createStreamingJsonReader(new TokenReader(is), 1);
	}

	public StreamingJsonReader createStreamingJsonReader(final Reader reader) {
		return createStreamingJsonReader(new TokenReader(reader), 1);
	}

	public StreamingJsonReader createStreamingJsonReader(
			final TokenReader tokenReader) {
		return createStreamingJsonReader(tokenReader, 1);
	}

	public StreamingJsonReader createStreamingJsonReader(final InputStream is,
			final int eventsToBuffer) {
		return createStreamingJsonReader(new TokenReader(is), eventsToBuffer);
	}

	public StreamingJsonReader createStreamingJsonReader(final Reader reader,
			final int eventsToBuffer) {
		return createStreamingJsonReader(new TokenReader(reader),
				eventsToBuffer);
	}

	public StreamingJsonReader createStreamingJsonReader(
			final TokenReader tokenReader, final int eventsToBuffer) {
		return new StreamingJsonReader(tokenReader, eventsToBuffer);
	}
}
