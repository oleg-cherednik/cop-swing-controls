package com.alee.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * User: mgarin Date: 14.04.11 Time: 20:49
 */

public class ClickEffect
{
    private static final int MAX_RADIUS = 50;
    private static final int MAX_SPEED = 3;
    private static Point p = null;
    private static int radius;
    private static int button = -1;
    private static Timer clickEffect = null;

    public static void main ( String[] args )
    {
        try
        {
            // ������������� �������� ����� �����������
            UIManager.setLookAndFeel ( UIManager.getSystemLookAndFeelClassName () );
        }
        catch ( Throwable e )
        {
            //
        }

        final JFrame f = new JFrame ();

        f.getRootPane ().setOpaque ( true );
        f.getRootPane ().setBackground ( Color.WHITE );

        f.getContentPane ().setLayout ( new BorderLayout ( 5, 5 ) );
        f.getContentPane ().setBackground ( Color.WHITE );

        JPanel panel = new JPanel ();
        panel.setBorder ( BorderFactory.createEmptyBorder ( 5, 5, 5, 5 ) );
        panel.setLayout ( new BorderLayout ( 5, 5 ) );
        f.getContentPane ().add ( panel, BorderLayout.CENTER );

        panel.add ( new JButton ( "�������� ������" ), BorderLayout.NORTH );
        panel.add ( new JButton ( "Test" ), BorderLayout.EAST );
        panel.add ( new JButton ( "Test" ), BorderLayout.WEST );
        panel.add ( new JScrollPane ( new JTextArea( "���������� �������� � ����� ����� ����" )
        {
            {
                setFont ( getFont ().deriveFont ( 14f ) );
            }
        } ), BorderLayout.CENTER );
        panel.add ( new JButton ( "�������� ������" ), BorderLayout.SOUTH );


        final JComponent gp = new JComponent()
        {
            {
                clickEffect = new Timer ( 1000 / 48, new ActionListener()
                {
                    public void actionPerformed ( ActionEvent e )
                    {
                        radius += Math.max ( 1, MAX_SPEED - MAX_SPEED * radius / MAX_RADIUS );
                        repaint ( p.x - radius / 2 - 2, p.y - radius / 2 - 2, radius + 4,
                                radius + 4 );
                        if ( radius >= MAX_RADIUS )
                        {
                            clickEffect.stop ();
                        }
                    }
                } );
            }

            public boolean contains ( int x, int y )
            {
                return false;
            }

            private Stroke stroke = new BasicStroke ( 2.5f );

            public void paint ( Graphics g )
            {
                super.paint ( g );

                if ( clickEffect.isRunning () )
                {
                    Graphics2D g2d = ( Graphics2D ) g;
                    g2d.setRenderingHint ( RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON );
                    g2d.setComposite ( AlphaComposite.getInstance ( AlphaComposite.SRC_OVER,
                            1 - ( float ) radius / MAX_RADIUS ) );
                    g2d.setStroke ( stroke );
                    g2d.setPaint ( button == MouseEvent.BUTTON1 ? Color.GRAY :
                            button == MouseEvent.BUTTON2 ? Color.MAGENTA :
                                    button == MouseEvent.BUTTON3 ? Color.RED : Color.WHITE );
                    g2d.drawOval ( p.x - radius / 2 - 1, p.y - radius / 2 - 1, radius, radius );
                }
            }
        };
        gp.setOpaque ( false );
        f.setGlassPane ( gp );

        Toolkit.getDefaultToolkit ().addAWTEventListener ( new AWTEventListener()
        {
            public void eventDispatched ( AWTEvent event )
            {
                if ( event instanceof MouseEvent && event.getID () == MouseEvent.MOUSE_PRESSED )
                {
                    MouseEvent e = ( MouseEvent ) event;
                    if ( clickEffect != null && f != null && f.isVisible () )
                    {
                        Point m = MouseInfo.getPointerInfo ().getLocation ();
                        Point rp = f.getRootPane ().getLocationOnScreen ();
                        p = new Point ( m.x - rp.x, m.y - rp.y );
                        radius = 0;
                        button = e.getButton ();
                        gp.repaint ();
                        clickEffect.start ();
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK );

        f.setDefaultCloseOperation (
                args.length > 0 ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE );
        f.setSize ( 500, 300 );
        f.setLocationRelativeTo ( null );
        f.setVisible ( true );

        gp.setVisible ( true );
    }
}
