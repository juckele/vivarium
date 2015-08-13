package com.johnuckele.vivarium.core;

import java.util.LinkedList;

public class CensusRecord
{
    private LinkedList<IntTuple> _censusRecords;

    private int _lastPopulationValue;

    public CensusRecord(int initialPopulationValue)
    {
        _censusRecords = new LinkedList<IntTuple>();
        _censusRecords.add(new IntTuple(0, initialPopulationValue));
    }

    public void updateRecords(int currentTickValue, int currentPopulationValue)
    {
        // Only add a new entry if things have changed to keep the data sparse
        if (currentPopulationValue != _lastPopulationValue)
        {
            _censusRecords.add(new IntTuple(currentTickValue, currentPopulationValue));
            _lastPopulationValue = currentPopulationValue;
        }
    }

    public LinkedList<IntTuple> getRecords()
    {
        return (_censusRecords);
    }
}