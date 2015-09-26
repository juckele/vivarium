package com.johnuckele.vivarium.scripts;

public abstract class Script
{
    public Script(String[] args)
    {
        if (argumentCountIsValid(args.length))
        {
            run(args);
        }
        else
        {
            printUsageAndExit();
        }
    }

    protected abstract boolean argumentCountIsValid(int argCount);

    protected abstract String getUsage();

    protected abstract void run(String[] args);

    protected void printUsageAndExit()
    {
        System.out.println(getUsage());
    }
}
