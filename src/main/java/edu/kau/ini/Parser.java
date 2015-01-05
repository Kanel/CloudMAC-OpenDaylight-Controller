package edu.kau.ini;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Parser
{	
	public static Map<String, Map<String, String>> parse(String path)
	{
		Lexer lexer = new Lexer();
		
		if (lexer.init(path))
		{
			Map<String, Map<String, String>> result;
			
			lexer.next();
			
			result = sections(lexer);
			
			lexer.close();
			
			return result;
		}
		else
		{
			lexer.close();
			
			return null;
		}
	}
	
	private static Map<String, Map<String, String>> sections(Lexer lexer)
	{
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		Map<String, String> current;

		current = new HashMap<String, String>();

		map.put("default", current);
		
		while (lexer.getToken().tag == Tags.SQUARE_BRACKET_LEFT || 
			   lexer.getToken().tag == Tags.STRING || 
			   lexer.getToken().tag == Tags.HASH_TAG || 
			   lexer.getToken().tag == Tags.END_OF_LINE)
		{
			if (lexer.getToken().tag == Tags.SQUARE_BRACKET_LEFT)
			{
				lexer.next();
				
				if (lexer.getToken().tag == Tags.STRING)
				{
					current = new HashMap<String, String>();

					map.put(lexer.getToken().value, current);
					
					lexer.next();
				}
				else
				{					
					return null;
				}
				
				if (!lexer.match(Tags.SQUARE_BRACKET_RIGHT))
				{
					return null;
				}
				
				if (!lexer.match(Tags.END_OF_LINE))
				{
					return null;
				}
			}
			else if (lexer.getToken().tag == Tags.STRING)
			{
				KeyValuePair pair = keyValuePair(lexer);
				
				if (pair != null)
				{
					current.put(pair.getKey(), pair.getValue());
				}
				else
				{
					return null;
				}
			}
			else if (lexer.getToken().tag == Tags.END_OF_LINE)
			{
				lexer.next();
			}
			else
			{
				if (comment(lexer) == null)
				{
					return null;
				}
			}
		}
		
		return map;
	}
	
	private static KeyValuePair keyValuePair(Lexer lexer)
	{
		KeyValuePair pair = new KeyValuePair();
		
		if (lexer.getToken().getTag() == Tags.STRING)
		{
			pair.setKey(lexer.getToken().getValue());
			
			lexer.next();
		}
		else
		{
			return null;
		}
		
		if (!lexer.match(Tags.EQUALS))
		{
			return null;
		}
		
		if (lexer.getToken().getTag() == Tags.STRING)
		{
			pair.setValue(lexer.getToken().getValue());
			
			lexer.next();
		}
		else
		{
			return null;
		}
		
		if (!(lexer.match(Tags.END_OF_LINE) || lexer.match(Tags.END_OF_LINE)))
		{
			return null;
		}
		
		return pair;
	}
	
	private static String comment(Lexer lexer)
	{
		String value;
		
		if (!lexer.match(Tags.HASH_TAG))
		{
			return null;
		}
		if (lexer.getToken().getTag() == Tags.STRING)
		{
			value = lexer.getToken().getValue();
			
			lexer.next();
		}
		else
		{
			return null;
		}
		
		if (!(lexer.match(Tags.END_OF_LINE) || lexer.match(Tags.END_OF_LINE)))
		{
			return null;
		}
		
		return value;
	}
}
