package com.johnuckele.vivarium.core;

import java.io.Serializable;

public class ActionProfile implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	int[][] _actionCount;
	
	public ActionProfile()
	{
		_actionCount = new int[2][Action.getDistinctActionCount()];
	}

	public void recordAction(Action action, boolean didSucceed)
	{
		int actionInt = Action.convertActionToInteger(action);
		_actionCount[didSucceed ? 1 : 0][actionInt]++;
	}

	public int getActionCount(Action action, boolean didSucceed)
	{
		int actionInt = Action.convertActionToInteger(action);
		return(_actionCount[didSucceed ? 1 : 0][actionInt]);
	}

	public void addActionHistory(ActionProfile history)
	{
		for(int i = 0; i < 2; i++)
		{
			for(int j = 0; j < Action.getDistinctActionCount(); j++)
			{
				this._actionCount[i][j] += history._actionCount[i][j];
			}
		}
	}
}