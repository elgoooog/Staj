package com.elgoooog.staj;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;

public class TokenReader implements Closeable {
	private final PushbackReader reader;

	public TokenReader(final Reader reader) {
		this.reader = new PushbackReader(reader);
	}

	public TokenReader(final InputStream is) {
		this(new InputStreamReader(is));
	}

	public Token readNext() throws IOException {
		char c;

		do {
			c = (char) reader.read();
		} while (c != -1 && Character.isWhitespace(c));

		switch (c) {
		case (char) -1:
			return Token.END;
		case '{':
			return Token.OPEN_BRACE;
		case '}':
			return Token.CLOSE_BRACE;
		case '[':
			return Token.OPEN_BRACKET;
		case ']':
			return Token.CLOSE_BRACKET;
		case ':':
			return Token.COLON;
		case ',':
			return Token.COMMA;
		case '"':
			return parseString();
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '0':
		case '-':
			return parseNumber(c);
		case 'n':
			return parseNull();
		case 't':
			return parseTrue();
		case 'f':
			return parseFalse();
		default:
			throw new JsonParseException("not legal");
		}
	}

	protected Token parseString() throws IOException {
		final StringBuilder builder = new StringBuilder();

		char c = (char) reader.read();

		while ('"' != c) {
			if (c == '\\') {
				// process escaped characters
			}
			builder.append(c);
			c = (char) reader.read();
		}

		return Token.s(builder.toString());
	}

	protected Token parseNumber(char c) throws IOException {
		final StringBuilder builder = new StringBuilder();

		boolean hasDecimal = false;

		while (Character.isDigit(c) || c == '-' || (!hasDecimal && c == '.')) {
			if (c == '.') {
				hasDecimal = true;
			}

			builder.append(c);
			c = (char) reader.read();
		}

		if (c != -1) {
			reader.unread(c);
		}

		return Token.n(builder.toString());
	}

	protected Token parseNull() throws IOException {
		parseExactWord("ull");

		return Token.NULL;
	}

	protected Token parseFalse() throws IOException {
		parseExactWord("alse");

		return Token.FALSE;
	}

	protected Token parseTrue() throws IOException {
		parseExactWord("rue");

		return Token.TRUE;
	}

	protected void parseExactWord(final CharSequence word) throws IOException {
		for (int i = 0; i < word.length(); ++i) {
			if (word.charAt(i) != reader.read()) {
				throw new JsonParseException("expected word: " + word
						+ " not found");
			}
		}
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	protected static class Token {
		protected static final Token END = new Token("", TokenType.END);
		protected static final Token OPEN_BRACE = new Token("{",
				TokenType.OPEN_BRACE);
		protected static final Token CLOSE_BRACE = new Token("}",
				TokenType.CLOSE_BRACE);
		protected static final Token OPEN_BRACKET = new Token("[",
				TokenType.OPEN_BRACKET);
		protected static final Token CLOSE_BRACKET = new Token("]",
				TokenType.CLOSE_BRACKET);
		protected static final Token NULL = new Token("null", TokenType.NULL);
		protected static final Token FALSE = new Token("false", TokenType.FALSE);
		protected static final Token TRUE = new Token("true", TokenType.TRUE);
		protected static final Token COLON = new Token(":", TokenType.COLON);
		protected static final Token COMMA = new Token(",", TokenType.COMMA);

		protected final String value;
		protected final TokenType type;

		private Token(final String value, final TokenType type) {
			this.value = value;
			this.type = type;
		}

		private static Token s(final String s) {
			return new Token(s, TokenType.STRING);
		}

		private static Token n(final String n) {
			return new Token(n, TokenType.NUMBER);
		}
	}

	protected static class TokenType {
		protected static final TokenType STRING = new TokenType();
		protected static final TokenType NUMBER = new TokenType();
		protected static final TokenType END = new TokenType();
		protected static final TokenType OPEN_BRACE = new TokenType();
		protected static final TokenType CLOSE_BRACE = new TokenType();
		protected static final TokenType OPEN_BRACKET = new TokenType();
		protected static final TokenType CLOSE_BRACKET = new TokenType();
		protected static final TokenType NULL = new TokenType();
		protected static final TokenType FALSE = new TokenType();
		protected static final TokenType TRUE = new TokenType();
		protected static final TokenType COLON = new TokenType();
		protected static final TokenType COMMA = new TokenType();
	}
}
