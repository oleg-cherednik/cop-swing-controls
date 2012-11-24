package com.alee.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * User: mgarin Date: 15.04.11 Time: 15:18
 */

public class CursorHighlight
{
    public static void main ( String[] args )
    {
        final JDialog f = new JDialog ();

        f.getContentPane ().setLayout ( new BorderLayout ( 0, 0 ) );
        f.getContentPane ().add ( new JComponent()
        {
            {
                setOpaque ( false );
            }

            private Color bg = new Color ( 192, 192, 192, 64 );

            public void paint ( Graphics g )
            {
                super.paint ( g );

                Graphics2D g2d = ( Graphics2D ) g;
                g2d.setRenderingHint ( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );

                g2d.setPaint ( bg );
                GeneralPath gp = new GeneralPath ( GeneralPath.WIND_EVEN_ODD );
                gp.append ( new Ellipse2D.Double ( 0, 0, 100, 100 ), false );
                gp.append ( new Ellipse2D.Double ( 25, 25, 50, 50 ), false );
                g2d.fill ( gp );
            }
        } );

        new Timer ( 1000 / 96, new ActionListener()
        {
            public void actionPerformed ( ActionEvent e )
            {
                Point p = MouseInfo.getPointerInfo ().getLocation ();
                Point newLocation = new Point ( p.x - 50, p.y - 50 );
                if ( !f.getLocation ().equals ( newLocation ) )
                {
                    f.setLocation ( newLocation );
                }
            }
        } ).start ();

        //        if ( f.getRootPane () != null )
        //        {
        //            for ( MouseListener ml : f.getRootPane ().getMouseListeners () )
        //            {
        //                f.removeMouseListener ( ml );
        //            }
        //            for ( MouseMotionListener ml : f.getRootPane ().getMouseMotionListeners () )
        //            {
        //                f.removeMouseMotionListener ( ml );
        //            }
        //        }
        //
        //        if ( f.getLayeredPane () != null )
        //        {
        //            for ( MouseListener ml : f.getLayeredPane ().getMouseListeners () )
        //            {
        //                f.removeMouseListener ( ml );
        //            }
        //            for ( MouseMotionListener ml : f.getLayeredPane ().getMouseMotionListeners () )
        //            {
        //                f.removeMouseMotionListener ( ml );
        //            }
        //        }
        //
        //        if ( f.getGlassPane () != null )
        //        {
        //            for ( MouseListener ml : f.getGlassPane ().getMouseListeners () )
        //            {
        //                f.removeMouseListener ( ml );
        //            }
        //            for ( MouseMotionListener ml : f.getGlassPane ().getMouseMotionListeners () )
        //            {
        //                f.removeMouseMotionListener ( ml );
        //            }
        //        }
        //
        //        if ( f.getContentPane () != null )
        //        {
        //            for ( MouseListener ml : f.getContentPane ().getMouseListeners () )
        //            {
        //                f.removeMouseListener ( ml );
        //            }
        //            for ( MouseMotionListener ml : f.getContentPane ().getMouseMotionListeners () )
        //            {
        //                f.removeMouseMotionListener ( ml );
        //            }
        //        }
        //
        //        if ( f != null )
        //        {
        //            for ( MouseListener ml : f.getMouseListeners () )
        //            {
        //                f.removeMouseListener ( ml );
        //            }
        //            for ( MouseMotionListener ml : f.getMouseMotionListeners () )
        //            {
        //                f.removeMouseMotionListener ( ml );
        //            }
        //        }

        f.setUndecorated ( true );
        f.setAlwaysOnTop ( true );
        AWTUtilitiesWrapper.setWindowOpaque ( f, false );
        f.setDefaultCloseOperation ( JDialog.DISPOSE_ON_CLOSE );
        f.setLocation ( 0, 0 );
        f.setSize ( 100, 100 );
        f.setVisible ( true );
    }
}
