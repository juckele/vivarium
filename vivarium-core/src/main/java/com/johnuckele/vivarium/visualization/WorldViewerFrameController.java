package com.johnuckele.vivarium.visualization;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WorldViewerFrameController extends JPanel implements ActionListener
{
    private WorldViewer _targetWorldViewer;
    private int         _frame;

    private JButton[] _skips = new JButton[10];
    private JLabel    _frameCounter;

    public WorldViewerFrameController(WorldViewer worldViewer, int initialFrame)
    {
        this._targetWorldViewer = worldViewer;

        this._frame = initialFrame;

        _skips[0] = new JButton("<<<<<");
        _skips[1] = new JButton("<<<<");
        _skips[2] = new JButton("<<<");
        _skips[3] = new JButton("<<");
        _skips[4] = new JButton("<");
        _skips[5] = new JButton(">");
        _skips[6] = new JButton(">>");
        _skips[7] = new JButton(">>>");
        _skips[8] = new JButton(">>>>");
        _skips[9] = new JButton(">>>>>");
        _frameCounter = new JLabel("" + _frame);

        for (int i = 0; i < 10; i++)
        {
            this.add(_skips[i]);
            _skips[i].addActionListener(this);
        }
        this.add(_frameCounter);
    }

    public void setRenderIndex(int index)
    {
        _frame = index;
        _frameCounter.setText("" + _frame);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == _skips[0])
        {
            _targetWorldViewer.setRenderIndex(_frame - 10000);
        }
        else if (source == _skips[1])
        {
            _targetWorldViewer.setRenderIndex(_frame - 1000);
        }
        else if (source == _skips[2])
        {
            _targetWorldViewer.setRenderIndex(_frame - 100);
        }
        else if (source == _skips[3])
        {
            _targetWorldViewer.setRenderIndex(_frame - 10);
        }
        else if (source == _skips[4])
        {
            _targetWorldViewer.setRenderIndex(_frame - 1);
        }
        else if (source == _skips[5])
        {
            _targetWorldViewer.setRenderIndex(_frame + 1);
        }
        else if (source == _skips[6])
        {
            _targetWorldViewer.setRenderIndex(_frame + 10);
        }
        else if (source == _skips[7])
        {
            _targetWorldViewer.setRenderIndex(_frame + 100);
        }
        else if (source == _skips[8])
        {
            _targetWorldViewer.setRenderIndex(_frame + 1000);
        }
        else if (source == _skips[9])
        {
            _targetWorldViewer.setRenderIndex(_frame + 10000);
        }
    }
}
