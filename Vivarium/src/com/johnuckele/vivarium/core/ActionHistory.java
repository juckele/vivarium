package com.johnuckele.vivarium.core;

import java.io.Serializable;

public class ActionHistory implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	public static final int	FAIL	= 0;
	public static final int	SUCCEED	= 1;

	int[][] _actionCount;
	
	public ActionHistory()
	{
		_actionCount = new int[2][UckeleoidAction.getDistinctActionCount()];
	}

	public void recordAction(UckeleoidAction action, int didSucceed)
	{
		int actionInt = UckeleoidAction.convertActionToInteger(action);
		_actionCount[didSucceed][actionInt]++;
	}

	public int getActionCount(UckeleoidAction action, int didSucceed)
	{
		int actionInt = UckeleoidAction.convertActionToInteger(action);
		return(_actionCount[didSucceed][actionInt]);
	}
}