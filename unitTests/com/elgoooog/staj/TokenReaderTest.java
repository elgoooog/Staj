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
	public void testParseNumber_whole() throws Exception {
		reader = new TokenReader(new StringReader("3"));

		final Token token = reader.parseNumber();
		assertEquals("3", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_zero() throws Exception {
		reader = new TokenReader(new StringReader("0"));

		final Token token = reader.parseNumber();
		assertEquals("0", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_zeroPointSomething() throws Exception {
		reader = new TokenReader(new StringReader("0.223"));

		final Token token = reader.parseNumber();
		assertEquals("0.223", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_e() throws Exception {
		reader = new TokenReader(new StringReader("0.223e3"));

		final Token token = reader.parseNumber();
		assertEquals("0.223e3", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_ePlus() throws Exception {
		reader = new TokenReader(new StringReader("0.223e+3"));

		final Token token = reader.parseNumber();
		assertEquals("0.223e+3", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_eMinus() throws Exception {
		reader = new TokenReader(new StringReader("0.223e-3"));

		final Token token = reader.parseNumber();
		assertEquals("0.223e-3", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_bigE() throws Exception {
		reader = new TokenReader(new StringReader("0.223E3"));

		final Token token = reader.parseNumber();
		assertEquals("0.223E3", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_decimal() throws Exception {
		reader = new TokenReader(new StringReader("3.14"));

		final Token token = reader.parseNumber();
		assertEquals("3.14", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNumber_negative() throws Exception {
		reader = new TokenReader(new StringReader("-3.14"));

		final Token token = reader.parseNumber();
		assertEquals("-3.14", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test(expected = JsonParseException.class)
	public void testParseNumber_exception_noDigitsAfterDecimal()
			throws Exception {
		reader = new TokenReader(new StringReader("3."));

		reader.parseNumber();
	}

	@Test
	public void testParseNumber_exception_multipleDecimals() throws Exception {
		reader = new TokenReader(new StringReader("3.3.4"));

		final Token token = reader.parseNumber();
		assertEquals("3.3", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test(expected = JsonParseException.class)
	public void testParseNumber_exception_nothingAfterE() throws Exception {
		reader = new TokenReader(new StringReader("3.3e"));

		reader.parseNumber();
	}

	@Test
	public void testParseNumber_exception_numbersAfter0() throws Exception {
		reader = new TokenReader(new StringReader("014"));

		final Token token = reader.parseNumber();
		assertEquals("0", token.value);
		assertEquals(TokenType.NUMBER, token.type);
	}

	@Test
	public void testParseNull() throws Exception {
		reader = new TokenReader(new StringReader("null"));

		final Token token = reader.parseNull();
		assertEquals(Token.NULL, token);
	}

	@Test(expected = JsonParseException.class)
	public void testParseNull_notNull() throws Exception {
		reader = new TokenReader(new StringReader("ul!"));

		reader.parseNull();
	}

	@Test
	public void testParseFalse() throws Exception {
		reader = new TokenReader(new StringReader("false"));

		final Token token = reader.parseFalse();
		assertEquals(Token.FALSE, token);
	}

	@Test(expected = JsonParseException.class)
	public void testParseFalse_notFalse() throws Exception {
		reader = new TokenReader(new StringReader("a!se"));

		reader.parseFalse();
	}

	@Test
	public void testParseTrue() throws Exception {
		reader = new TokenReader(new StringReader("true"));

		final Token token = reader.parseTrue();
		assertEquals(Token.TRUE, token);
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
	public void testParseEscapedCharacter1() throws Exception {
		reader = new TokenReader(new StringReader("t"));

		final char c = reader.parseEscapedCharacter();
		assertEquals('\t', c);
	}

	@Test
	public void testParseEscapedCharacter2() throws Exception {
		reader = new TokenReader(new StringReader("\""));

		final char c = reader.parseEscapedCharacter();
		assertEquals('\"', c);
	}

	@Test
	public void testParseUnicodeCharacter1() throws Exception {
		reader = new TokenReader(new StringReader("0021"));

		final char c = reader.parseUnicodeCharacter();
		assertEquals('!', c);
	}

	@Test
	public void testParseUnicodeCharacter2() throws Exception {
		reader = new TokenReader(new StringReader("00A1"));

		final char c = reader.parseUnicodeCharacter();
		assertEquals('¡', c);
	}

	@Test
	public void testParseUnicodeCharacter2_caseSensitive() throws Exception {
		reader = new TokenReader(new StringReader("00a1"));

		final char c = reader.parseUnicodeCharacter();
		assertEquals('¡', c);
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
