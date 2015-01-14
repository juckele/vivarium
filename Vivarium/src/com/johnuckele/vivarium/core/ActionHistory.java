package com.johnuckele.vivarium.core;

import java.io.Serializable;

public class ActionHistory implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	int[][] _actionCount;
	
	public ActionHistory()
	{
		_actionCount = new int[2][UckeleoidAction.getDistinctActionCount()];
	}

	public void recordAction(UckeleoidAction action, boolean didSucceed)
	{
		int actionInt = UckeleoidAction.convertActionToInteger(action);
		_actionCount[didSucceed ? 1 : 0][actionInt]++;
	}

	public int getActionCount(UckeleoidAction action, boolean didSucceed)
	{
		int actionInt = UckeleoidAction.convertActionToInteger(action);
		return(_actionCount[didSucceed ? 1 : 0][actionInt]);
	}

	public void addActionHistory(ActionHistory history)
	{
		for(int i = 0; i < 2; i++)
		{
			for(int j = 0; j < UckeleoidAction.getDistinctActionCount(); j++)
			{
				this._actionCount[i][j] += history._actionCount[i][j];
			}
		}
	}
}