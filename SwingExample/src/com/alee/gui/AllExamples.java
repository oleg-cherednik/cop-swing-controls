package com.alee.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: mgarin Date: 18.04.11 Time: 20:10
 */

public class AllExamples
{
    private static final String[] main =  new String[]{"examples"};

    public static void main ( String[] args )
    {
        try
        {
            // Устанавливаем нативный стиль компонентов
            UIManager.setLookAndFeel ( UIManager.getSystemLookAndFeelClassName () );
        }
        catch ( Throwable e )
        {
            //
        }

        final JFrame f = new JFrame ();

        f.getRootPane ().setOpaque ( true );
        f.getRootPane ().setBackground ( Color.WHITE );
        f.getRootPane ().setBorder ( BorderFactory.createEmptyBorder ( 10, 10, 10, 10 ) );

        f.getContentPane ().setBackground ( Color.WHITE );
        f.getContentPane ().setLayout ( new GridLayout ( 2, 4, 10, 10 ) );

        f.getContentPane ().add ( createExamplePanel ( 0, "Пример SliderUI", new Runnable()
        {
            public void run ()
            {
                MySliderUI.main ( main );
            }
        } ), BorderLayout.CENTER );

        f.getContentPane ().add ( createExamplePanel ( 1, "Пример кастом поля", new Runnable()
        {
            public void run ()
            {
                TextFieldChecker.main ( main );
            }
        } ), BorderLayout.CENTER );

        f.getContentPane ().add ( createExamplePanel ( 2, "Пример кастом чекбокса", new Runnable()
        {
            public void run ()
            {
                MyCheckBox.main ( main );
            }
        } ), BorderLayout.CENTER );

        f.getContentPane ().add ( createExamplePanel ( 3, "Пример ButtonUI", new Runnable()
        {
            public void run ()
            {
                MyButtonUI.main ( main );
            }
        } ), BorderLayout.CENTER );

        f.getContentPane ().add ( createExamplePanel ( 4, "Пример с GlassPane", new Runnable()
        {
            public void run ()
            {
                ClickEffect.main ( main );
            }
        } ), BorderLayout.CENTER );

        f.getContentPane ().add ( createExamplePanel ( 5, "Пример кастом компонента", new Runnable()
        {
            public void run ()
            {
                ImageResizeComponent.main ( main );
            }
        } ), BorderLayout.CENTER );

        f.getContentPane ().add ( createExamplePanel ( 6, "Пример DnD", new Runnable()
        {
            public void run ()
            {
                DndExample.main ( main );
            }
        } ), BorderLayout.CENTER );

        f.getContentPane ().add ( createExamplePanel ( 7, "Пример кастом окна", new Runnable()
        {
            public void run ()
            {
                CustomDialog.main (main );
            }
        } ), BorderLayout.CENTER );

        f.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
        f.pack ();
        f.setLocationRelativeTo ( null );
        f.setResizable ( false );
        f.setVisible ( true );
    }

    private static int rollover = -1;

    private static JPanel createExamplePanel ( final int exampleIcon, String exampleName,
                                               final Runnable exampleRun )
    {
        final JPanel panel = new JPanel()
        {
            private Color light = new Color ( 216, 216, 216 );
            private Color dark = new Color ( 166, 166, 166 );

            public void paint ( Graphics g )
            {
                Graphics2D g2d = ( Graphics2D ) g;
                g2d.setRenderingHint ( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );

                g2d.setPaint ( new GradientPaint ( 0, 0, rollover == exampleIcon ? dark : light,
                        getWidth (), getHeight (), Color.WHITE ) );
                g2d.fillRoundRect ( 0, 0, getWidth (), getHeight (), 20, 20 );

                g2d.setPaint ( Color.GRAY );
                g2d.drawRoundRect ( 0, 0, getWidth () - 1, getHeight () - 1, 20, 20 );

                super.paint ( g );
            }
        };
        panel.setOpaque ( false );
        panel.setBorder ( BorderFactory.createEmptyBorder ( 10, 10, 10, 10 ) );
        panel.setLayout ( new BorderLayout ( 5, 5 ) );

        panel.add ( new JLabel ( new ImageIcon (
                AllExamples.class.getResource ( "icons/all/" + exampleIcon + ".png" ) ) ),
                BorderLayout.CENTER );

        panel.add ( new JLabel ( exampleName, JLabel.CENTER ), BorderLayout.SOUTH );

        panel.addMouseListener ( new MouseAdapter()
        {
            public void mouseEntered ( MouseEvent e )
            {
                rollover = exampleIcon;
                panel.repaint ();
            }

            public void mouseExited ( MouseEvent e )
            {
                rollover = -1;
                panel.repaint ();
            }

            public void mousePressed ( MouseEvent e )
            {
                exampleRun.run ();
            }
        } );

        return panel;
    }
}
