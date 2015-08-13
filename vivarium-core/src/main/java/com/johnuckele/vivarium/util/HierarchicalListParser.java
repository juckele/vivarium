package com.johnuckele.vivarium.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HierarchicalListParser
{

    public static String generateString(List<Object> list)
    {
        if (list == null)
        {
            return "";
        }
        else
        {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            innerGenerate(list, builder);
            builder.append(']');
            return builder.toString();
        }
    }

    private static void innerGenerate(List<Object> list, StringBuilder builder)
    {
        Iterator<Object> it = list.iterator();
        while (it.hasNext())
        {
            Object o = it.next();
            if (o instanceof List)
            {
                @SuppressWarnings("unchecked")
                List<Object> innerList = (List<Object>) o;
                builder.append('[');
                innerGenerate(innerList, builder);
                builder.append(']');
            }
            else
            {
                builder.append(o);
            }

            if (it.hasNext())
            {
                builder.append(',');
            }
        }
    }

    public static List<Object> parseList(String listString)
    {
        listString = listString.trim();
        if (listString.length() == 0)
        {
            return null;
        }
        else if (listString.charAt(0) == '[' && listString.charAt(listString.length() - 1) == ']')
        {
            // Replace 'delimiter' tokens with extra characters to use as actual delimiters
            listString = listString.replace("[", "[~");
            listString = listString.replace(",", "~,~");
            listString = listString.replace("]", "~]");
            listString = listString.replace("[~~]", "[~]"); // Remove this particular double delimiter
            // Remove the first and last trash delimiters to avoid getting empty strings at the start and end
            // listString = listString.substring(1, listString.length() - 1);
            // Split the strings into tokens, including entries for the token delimiters above
            List<String> tokenList = new LinkedList<String>(Arrays.asList(listString.split("~")));
            // Discard the first token, which is always a '[' so that when the recursive function sees the last ']', it
            // can return correctly
            tokenList.remove(0);
            // Parse the token list into the object list
            ArrayList<Object> objectList = new ArrayList<Object>();
            innerParse(objectList, tokenList);
            return objectList;
        }
        else
        {
            throw new IllegalArgumentException(
                    "String " + listString + " is not in the expected format, lists should be enclosed in brackets");
        }
    }

    private static void innerParse(List<Object> list, List<String> tokenList)
    {
        boolean emptyString = true;
        while (tokenList.size() > 0)
        {
            String token = tokenList.remove(0);
            if (token.equals("["))
            {
                ArrayList<Object> innerList = new ArrayList<Object>();
                list.add(innerList);
                innerParse(innerList, tokenList);
                emptyString = false;
            }
            else if (token.equals(","))
            {
                if (emptyString)
                {
                    list.add("");
                }
                emptyString = true;
                continue;
            }
            else if (token.equals("]"))
            {
                emptyString = false;
                return;
            }
            else
            {
                emptyString = false;
                list.add(token.trim());
            }
        }
        throw new IllegalStateException("Mismatched braces");
    }
}
