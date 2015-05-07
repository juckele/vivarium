package com.johnuckele.vivarium.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CSVUtils
{
	public static boolean validate(String fileName)
	{
		// Open the csv file
		Scanner scanner = null;
		try
		{
			scanner = new Scanner(new File(fileName));
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}

		// Make sure the header is well formatted
		String headerLine = scanner.nextLine();
		int headerColumns = headerLine.split(",").length;
		while(scanner.hasNextLine())
		{
			String line = scanner.nextLine();
			int lineColumns = line.split(",").length;
			if(headerColumns != lineColumns)
			{
				System.err.println(
						"Line: \""+line+"\" has "+lineColumns+
						" columns but header: \""+headerLine+
						"\" has "+headerColumns);
				scanner.close();
				return false;
			}
		}

		// If we haven't discovered any problems, this looks correct
		scanner.close();
		return true;
	}

	public static int getRowCount(String fileName)
	{
		// Open the csv file
		Scanner scanner = null;
		try
		{
			scanner = new Scanner(new File(fileName));
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			return -1;
		}

		// Count rows
		scanner.nextLine(); // Header Line
		int rowLines = 0;
		while(scanner.hasNextLine())
		{
			scanner.nextLine();
			rowLines++;
		}

		// Clean up & return
		scanner.close();
		return rowLines;
	}
}
