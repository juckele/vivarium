package com.johnuckele.vivarium.visualization;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.johnuckele.vivarium.core.Species;
import com.johnuckele.vivarium.core.World;
import com.johnuckele.vivarium.core.WorldObject;
import com.johnuckele.vivarium.core.WorldVariables;

public class WorldViewer extends JFrame
{
	public static final int			OVERVIEW_ONLY	= 1;
	public static final int			RAT_LIST_ONLY	= 2;
	private static final long			serialVersionUID	= 8857256647972270073L;

	private World						_w;
	private ArrayList<String>			_overviewRenders	= new ArrayList<String>(50000);
	private ArrayList<String>			_ratListRenders		= new ArrayList<String>(50000);
	private int							_renderIndex		= 0;

	private JTextArea					_overviewRender;
	private JTextArea					_ratListRender;
	private WorldViewerFrameController	_frameController;

	public WorldViewer(World w)
	{
		_w = w;
		_overviewRenders.add(_w.toString(RenderCode.WORLD_MAP) + "\n" + _w.toString(RenderCode.BRAIN_WEIGHTS));
		_ratListRenders.add(_w.toString(RenderCode.FULL_CREATURE_LIST));

		_overviewRender = new JTextArea(_overviewRenders.get(_renderIndex));
		_ratListRender = new JTextArea(_ratListRenders.get(_renderIndex));
		_frameController = new WorldViewerFrameController(this, 0);

		this.add(_overviewRender);
		this.add(_ratListRender);
		this.add(_frameController);

		_overviewRender.setBounds(0, 35, 900, 965);
		_overviewRender.setTabSize(5);
		_ratListRender.setBounds(900, 35, 800, 965);

		this.setSize(1700, 1000);
		this.setVisible(true);
	}

	public void runTicks(int ticks)
	{
		for(int tick = 1; tick <= ticks; tick++)
		{
			_w.tick();
		}
	}

	public void runAndRenderTicks(int ticks, int renderEvery)
	{
//		long startTime = System.currentTimeMillis();
		for(int tick = 1; tick <= ticks; tick++)
		{
			_w.tick();
			if(tick % renderEvery == 0)
			{
				_overviewRenders.add(_w.toString(RenderCode.WORLD_MAP) + "\n" + _w.toString(RenderCode.BRAIN_WEIGHTS));
				_ratListRenders.add(_w.toString(RenderCode.LIVE_CREATURE_LIST));
				System.out.println("Population "+_w.getCount(WorldObject.CREATURE));
			}
		}
	}

	public void setRenderIndex(int index)
	{
		index = Math.max(this.minIndex(), index);
		index = Math.min(this.maxIndex(), index);
		_renderIndex = index;
		_overviewRender.setText(_overviewRenders.get(_renderIndex));
		_ratListRender.setText(_ratListRenders.get(_renderIndex));
		_frameController.setRenderIndex(index);
	}

	public int minIndex()
	{
		return(0);
	}

	public int maxIndex()
	{
		return(_overviewRenders.size() - 1);
	}

	public static void main(String[] args)
	{
		int worldDimensions = 25;
		System.out.println("Creating world... " + worldDimensions + " x " + worldDimensions);
		WorldVariables wv = new WorldVariables(1);
		Species species = wv.getSpecies(0);
		species.setMutationRateExponent(-9);
		species.setCreatureMemoryUnitCount(1);
		species.setCreatureSoundChannelCount(1);
		World w = new World(worldDimensions, wv);
		System.out.println("Created world... " + worldDimensions + " x " + worldDimensions);

		WorldViewer wh = new WorldViewer(w);
		wh.runAndRenderTicks(2000000, 20000);
//		wh.runAndRenderTicks(20000, 1);

		System.out.println("Finished simulations");
	}
}
