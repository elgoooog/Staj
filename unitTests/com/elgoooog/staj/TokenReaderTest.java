package com.elgoooog.staj;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

import com.elgoooog.staj.TokenReader.Token;
import com.elgoooog.staj.TokenReader.TokenType;

public class TokenReaderTest {
	private TokenReader reader;

	@Test
	public void testParseString() throws Exception {
		reader = new TokenReader(new StringReader("Cat\""));

		final Token token = reader.parseString();
		assertEquals("Cat", token.value);
		assertEquals(TokenType.STRING, token.type);
	}

	@Test
	public void testParseNumber() throws Exception {
		reader = new TokenReader(new StringReader(".14"));

		final Token token = reader.parseNumber('3');
		assertEquals("3.14", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_negative() throws Exception {
		reader = new TokenReader(new StringReader("3.14"));

		final Token token = reader.parseNumber('-');
		assertEquals("-3.14", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNull() throws Exception {
		reader = new TokenReader(new StringReader("ull"));

		reader.parseNull();
	}

	@Test(expected = JsonParseException.class)
	public void testParseNull_notNull() throws Exception {
		reader = new TokenReader(new StringReader("ul!"));

		reader.parseNull();
	}

	@Test
	public void testParseFalse() throws Exception {
		reader = new TokenReader(new StringReader("alse"));

		reader.parseFalse();
	}

	@Test(expected = JsonParseException.class)
	public void testParseFalse_notFalse() throws Exception {
		reader = new TokenReader(new StringReader("a!se"));

		reader.parseFalse();
	}

	@Test
	public void testParseTrue() throws Exception {
		reader = new TokenReader(new StringReader("rue"));

		reader.parseTrue();
	}

	@Test(expected = JsonParseException.class)
	public void testParseTrue_notTrue() throws Exception {
		reader = new TokenReader(new StringReader("ru3"));

		reader.parseTrue();
	}

	@Test
	public void testParseExactWord() throws Exception {
		reader = new TokenReader(new StringReader("hello world"));

		reader.parseExactWord("hello world");
	}

	@Test(expected = JsonParseException.class)
	public void testParseExactWord_fail() throws Exception {
		reader = new TokenReader(new StringReader("hello world"));

		reader.parseExactWord("tally ho!");
	}

	@Test
	public void testNoWhitespace() throws Exception {
		reader = new TokenReader(getClass().getResourceAsStream(
				"noWhitespaceTest.json"));

		TokenReader.Token token = reader.readNext();
		assertEquals(TokenReader.Token.OPEN_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("something", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("somethingElse", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("another", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.OPEN_BRACKET, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("hi", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.TRUE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.NULL, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.OPEN_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("hi", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.NUMBER, token.type);
		assertEquals("123", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.CLOSE_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.CLOSE_BRACKET, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("pint", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.FALSE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.CLOSE_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.END, token);
	}

	@Test
	public void testWhitespace() throws Exception {
		reader = new TokenReader(getClass().getResourceAsStream(
				"whitespaceTest.json"));

		TokenReader.Token token = reader.readNext();
		assertEquals(TokenReader.Token.OPEN_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("something", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("somethingElse", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("another", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.OPEN_BRACKET, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("hi", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.TRUE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.NULL, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.OPEN_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("hi", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.NUMBER, token.type);
		assertEquals("123", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.CLOSE_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.CLOSE_BRACKET, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COMMA, token);

		token = reader.readNext();
		assertEquals(TokenReader.TokenType.STRING, token.type);
		assertEquals("pint", token.value);

		token = reader.readNext();
		assertEquals(TokenReader.Token.COLON, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.FALSE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.CLOSE_BRACE, token);

		token = reader.readNext();
		assertEquals(TokenReader.Token.END, token);
	}
}
