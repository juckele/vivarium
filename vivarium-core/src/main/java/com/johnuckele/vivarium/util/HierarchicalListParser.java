package com.johnuckele.vivarium.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
        for (Object o : list)
        {
            if (o instanceof List)
            {

            }
            else
            {
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
            StringTokenizer tokenizer = new StringTokenizer(listString, "[,]", true);
            tokenizer.nextToken(); // Pops off the first [ so that the innerParse returns when it hits the last ]
            ArrayList<Object> list = new ArrayList<Object>();
            innerParse(list, tokenizer);
            return list;
        }
        else
        {
            throw new IllegalArgumentException(
                    "String " + listString + " is not in the expected format, lists should be enclosed in brackets");
        }
    }

    private static void innerParse(List<Object> list, StringTokenizer tokenizer)
    {
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (token.equals("["))
            {
                ArrayList<Object> innerList = new ArrayList<Object>();
                list.add(innerList);
                innerParse(innerList, tokenizer);
            }
            else if (token.equals(","))
            {
                continue;
            }
            else if (token.equals("]"))
            {
                return;
            }
            else
            {
                list.add(token.trim());
            }
        }
        throw new IllegalStateException("Mismatched braces");
    }
}
