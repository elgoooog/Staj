package com.elgoooog.staj;

import java.io.IOException;
import java.util.LinkedList;

import com.elgoooog.staj.StreamingJsonReader.JsonEvent;
import com.elgoooog.staj.TokenReader.Token;
import com.elgoooog.staj.TokenReader.TokenType;

public class StreamingJsonParser {
	private static final Integer START_ARRAY = 1;
	private static final Integer START_OBJECT = 2;
	private static final Integer KEY = 3;
	private static final Integer MID_ARRAY = 4;
	private static final Integer MID_OBJECT = 5;

	private final TokenReader tokenReader;
	private final LinkedList<Integer> states;

	public StreamingJsonParser(final TokenReader tokenReader) {
		this.tokenReader = tokenReader;
		states = new LinkedList<>();
	}

	public StreamingJsonReader.JsonEvent readNext() {
		final Token token;
		try {
			token = tokenReader.readNext();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		if (states.size() > 0) {
			final Integer state = states.peek();

			if (state == START_ARRAY) {
				states.pop();
				if (token == Token.CLOSE_BRACKET) {
					return JsonEvent.END_ARRAY;
				}
				states.push(MID_ARRAY);
				return getValue(token);
			} else if (state == START_OBJECT) {
				states.pop();
				if (token == Token.CLOSE_BRACE) {
					return JsonEvent.END_OBJECT;
				}
				states.push(MID_OBJECT);
				return getKey(token);
			} else if (state == KEY) {
				if (token == Token.COLON) {
					states.pop();
					try {
						return getValue(tokenReader.readNext());
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					throw new ParseException(
							"Illegal state:  Expected colon, not found");
				}
			} else if (state == MID_ARRAY) {
				if (token == Token.COMMA) {
					try {
						return getValue(tokenReader.readNext());
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				} else if (token == Token.CLOSE_BRACKET) {
					states.pop();
					return JsonEvent.END_ARRAY;
				} else {
					throw new ParseException(
							"Illegal state:  Expected comma or Close Bracket, not found");
				}
			} else if (state == MID_OBJECT) {
				if (token == Token.COMMA) {
					try {
						return getKey(tokenReader.readNext());
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				} else if (token == Token.CLOSE_BRACE) {
					states.pop();
					return JsonEvent.END_OBJECT;
				} else {
					throw new ParseException(
							"Illegal state:  Expected comma, not found");
				}
			} else {
				throw new ParseException("I'm kinda lost here...");
			}
		} else {
			// need either an Open Brace or an Open Bracket
			if (token == Token.OPEN_BRACE) {
				states.push(START_OBJECT);
				return JsonEvent.START_OBJECT;
			} else if (token == Token.OPEN_BRACKET) {
				states.push(START_ARRAY);
				return JsonEvent.START_ARRAY;
			} else if (token == Token.END) {
				return JsonEvent.END;
			} else {
				throw new ParseException(
						"Illegal state:  Expected Open Bracket/Brace, not found.");
			}
		}
	}

	protected JsonEvent getValue(final Token token) {
		if (token == Token.OPEN_BRACE) {
			states.push(START_OBJECT);
			return JsonEvent.START_OBJECT;
		} else if (token == Token.OPEN_BRACKET) {
			states.push(START_ARRAY);
			return JsonEvent.START_ARRAY;
		} else if (token == Token.NULL) {
			return JsonEvent.NULL;
		} else if (token == Token.FALSE) {
			return JsonEvent.FALSE;
		} else if (token == Token.TRUE) {
			return JsonEvent.TRUE;
		} else if (token.type == TokenType.STRING) {
			return JsonEvent.s(token.value);
		} else if (token.type == TokenType.NUMBER) {
			return JsonEvent.n(token.value);
		} else {
			throw new ParseException(
					"Illegal state:  Expected Value, not found.");
		}
	}

	protected JsonEvent getKey(final Token token) {
		if (TokenType.STRING == token.type) {
			states.push(KEY);
			return JsonEvent.k(token.value);
		} else {
			throw new ParseException(
					"Illegal state:  Expected String, not found.");
		}
	}
}
