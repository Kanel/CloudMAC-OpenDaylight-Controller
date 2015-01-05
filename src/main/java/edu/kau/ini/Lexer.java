package edu.kau.ini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Lexer
{
	Map<String, Tags> reserved;
	Token current;
	Reader reader;
	int peek_value;
	
	public Lexer()
	{
		reader = null;
		current = null;
		peek_value = -2;
	}
	
	public boolean init(String path)
	{
		try
		{
			File file = new File(path);
			InputStream stream = new FileInputStream(file);
			Reader streamreader = new InputStreamReader(stream, StandardCharsets.UTF_8);
			
			reader = new BufferedReader(streamreader);
			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}		
	}
	
	public void close()
	{
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			
		}
	}
	
	public boolean next()
	{
		if (reader == null)
		{
			return false;
		}
		
		switch (peek())
		{
		case '[':
			current = new Token("[", Tags.SQUARE_BRACKET_LEFT);
			get();
			break;
			
		case ']':
			current = new Token("]", Tags.SQUARE_BRACKET_RIGHT);
			get();
			break;
			
		case '#':
			current = new Token("#", Tags.HASH_TAG);
			get();
			break;
			
		case '=':
			current = new Token("=", Tags.EQUALS);
			get();
			break;
			
		case '\n':
			current = new Token("\n", Tags.END_OF_LINE);
			get();
			break;
			
		case '\r':
			current = new Token("\r", Tags.END_OF_LINE);
			get();
			
			if (peek() == '\n')
			{
				current.setValue("\r\n");
				get();
			}
			
			break;
			
		case -1:
			current = new Token("", Tags.END_OF_INPUT);
			break;

		default:
			String value = new String();
			
			while(isString(peek()))
			{
				value += (char) get();
			}
			
			current = new Token(value.trim(), Tags.STRING);
			
			break;
		}
		
		return true;
	}
	
	public Token getToken()
	{
		return current;
	}
	
	public boolean match(Tags tag)
	{
		if (current.tag == tag)
		{
			next();
			
			return true;
		}
		else
		{
			return false;
		}
			
	}
	
	private boolean isString(int i)
	{
		return i != ']' && i != '[' && i != '#' && i != '#' && i != '\n' && i != '\r' && i != '=' && i != -1;
	}
	
	private int peek()
	{
		if (peek_value == -2)
		{
			peek_value = get();
		}
		
		return peek_value;
	}
	
	private int get()
	{		
		int i;
		
		if (peek_value == -2)
		{		
			try
			{
				i = reader.read();
			}
			catch (Exception e)
			{
				i = -1;
			}
		}
		else
		{
			i = peek_value;
			peek_value = -2;
		}
		
		return i;
	}
}
