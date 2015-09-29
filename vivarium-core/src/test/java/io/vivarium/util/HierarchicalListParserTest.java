package io.vivarium.util;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.johnuckele.vtest.Tester;

public class HierarchicalListParserTest
{
    @Test
    public void parseEmptyString()
    {
        List<Object> list = HierarchicalListParser.parseList("");
        Tester.isNull("Empty string is null", list);
    }

    @Test
    public void generateEmptyString()
    {
        List<Object> list = null;
        String listString = HierarchicalListParser.generateString(list);
        Tester.equal("Null object generates empty string", "", listString);
    }

    @Test
    public void parseEmptyArray()
    {
        List<Object> list = HierarchicalListParser.parseList("[]");
        Tester.equal("Empy list has zero elements", list.size(), 0);
    }

    @Test
    public void generateEmptyList()
    {
        List<Object> list = new LinkedList<Object>();
        String listString = HierarchicalListParser.generateString(list);
        Tester.equal("Null object generates empty string", "[]", listString);
    }

    @Test
    public void parseSingleItem()
    {
        List<Object> list = HierarchicalListParser.parseList("[one]");
        Tester.equal("Single item list has one element", list.size(), 1);
        Tester.equal("First value is parsed", (String) list.get(0), "one");
    }

    @Test
    public void generateSingleItemList()
    {
        List<Object> list = new LinkedList<Object>();
        list.add("one");
        String listString = HierarchicalListParser.generateString(list);
        Tester.equal("Null object generates empty string", "[one]", listString);
    }

    @Test
    public void parseMultipleItems()
    {
        List<Object> list = HierarchicalListParser.parseList("[one, 4,\"Hot Dog\"]");
        Tester.equal("Multi item list has three elements", list.size(), 3);
        Tester.equal("First value is parsed", (String) list.get(0), "one");
        Tester.equal("Subsequent value is parsed with preceeding whitespace", (String) list.get(1), "4");
        Tester.equal("Subsequent value is parsed without preceeding whitespace", (String) list.get(2), "\"Hot Dog\"");
    }

    @Test
    public void generateMultipleItemList()
    {
        List<Object> list = new LinkedList<Object>();
        list.add("one");
        list.add("4");
        list.add("\"Hot Dog\"");
        String listString = HierarchicalListParser.generateString(list);
        Tester.equal("Null object generates empty string", "[one,4,\"Hot Dog\"]", listString);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parseDeepEmptyList()
    {
        List<Object> list = HierarchicalListParser.parseList("[[[]]]");
        Tester.equal("Hierarchical list has one element", list.size(), 1);
        Tester.equal("... which also has one element", ((List<Object>) list.get(0)).size(), 1);
        Tester.equal("... which is empty", ((List<Object>) ((List<Object>) list.get(0)).get(0)).size(), 0);
    }

    @Test
    public void generateDeepEmptyList()
    {
        List<Object> innerList = new LinkedList<Object>();
        List<Object> list = new LinkedList<Object>();
        list.add(innerList);
        List<Object> outerList = new LinkedList<Object>();
        outerList.add(list);
        String listString = HierarchicalListParser.generateString(outerList);
        Tester.equal("Null object generates empty string", "[[[]]]", listString);
    }

    @Test
    public void generateCompoundList()
    {
        List<Object> innerList = new LinkedList<Object>();
        innerList.add("5");
        innerList.add(new Double(5.5));
        innerList.add("5.75");
        List<Object> list = new LinkedList<Object>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add(innerList);
        list.add("6");
        String listString = HierarchicalListParser.generateString(list);
        Tester.equal("Null object generates empty string", "[1,2,3,4,[5,5.5,5.75],6]", listString);
    }
}
