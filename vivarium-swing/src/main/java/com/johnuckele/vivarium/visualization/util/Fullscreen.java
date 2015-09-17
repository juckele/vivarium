package com.johnuckele.vivarium.visualization.util;

import java.awt.Window;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.XClientMessageEvent;
import com.sun.jna.platform.unix.X11.XEvent;

public class Fullscreen
{
    private static final int _NET_WM_STATE_REMOVE = 0;
    private static final int _NET_WM_STATE_ADD = 1;

    public static void setFullScreenWindow(Window window, boolean fullScreen)
    public static void setFullScreenWindow(JFrame window, boolean fullScreen)
    {
        String osName = System.getProperty("os.name");
        if (osName.equals("Linux"))
        {
            setFullScreenWindowLinux(window, fullScreen);
        }
        else
        {
            setFullScreenWindowLightweight(window, fullScreen);
        }
    }

    private static void setFullScreenWindowLightweight(JFrame window, boolean fullScreen)
    {
        window.setUndecorated(true);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(window);
    }

    private static void setFullScreenWindowLinux(Window window, boolean fullScreen)
    {
        X11 x11Instance = X11.INSTANCE;
        Display display = null;
        try
        {
            display = x11Instance.XOpenDisplay(null);
            long windowID = Native.getWindowID(window);

            // Event set up
            XEvent xEvent = new XEvent();
            xEvent.setType(XClientMessageEvent.class);

            xEvent.xclient.type = X11.ClientMessage;
            xEvent.xclient.message_type = x11Instance.XInternAtom(display, "_NET_WM_STATE", false);
            xEvent.xclient.format = 32;
            xEvent.xclient.window = new com.sun.jna.platform.unix.X11.Window(windowID);
            xEvent.xclient.send_event = 1;
            xEvent.xclient.serial = new NativeLong(0L);

            xEvent.xclient.data.setType(NativeLong[].class);
            xEvent.xclient.data.l[0] = new NativeLong(fullScreen ? _NET_WM_STATE_ADD : _NET_WM_STATE_REMOVE);
            xEvent.xclient.data.l[1] = x11Instance.XInternAtom(display, "_NET_WM_STATE_FULLSCREEN", false);
            xEvent.xclient.data.l[2] = new NativeLong(0L);
            xEvent.xclient.data.l[3] = new NativeLong(0L);
            xEvent.xclient.data.l[4] = new NativeLong(0L);

            // Make it so
            NativeLong mask = new NativeLong(X11.SubstructureRedirectMask | X11.SubstructureNotifyMask);
            x11Instance.XSendEvent(display, x11Instance.XDefaultRootWindow(display), 0, mask, xEvent);
            x11Instance.XFlush(display);
        }
        finally
        {
            if (display != null)
            {
                x11Instance.XCloseDisplay(display);
            }
        }
    }
}