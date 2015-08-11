package com.johnuckele.vivarium.core;

public class ActionProfile
{
    int[][] _actionCount;

    public ActionProfile()
    {
        _actionCount = new int[2][Action.values().length];
    }

    public void recordAction(Action action, boolean didSucceed)
    {
        int actionInt = Action.convertActionToInteger(action);
        _actionCount[didSucceed ? 1 : 0][actionInt]++;
    }

    public int getActionCount(Action action, boolean didSucceed)
    {
        int actionInt = Action.convertActionToInteger(action);
        return (_actionCount[didSucceed ? 1 : 0][actionInt]);
    }

    public void addActionHistory(ActionProfile history)
    {
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < Action.values().length; j++)
            {
                this._actionCount[i][j] += history._actionCount[i][j];
            }
        }
    }
}