package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GUI
{
    BorderLayout borderLayout;
    JFrame frame;
    JPanel main_panel, btn_panel, l_panel;
    ButtonGroup btn_grp;
    JRadioButton a,b,c;
    JLabel l_category,l_sentence,l_answ;

    public GUI(String sentence, String category, String h1, String h2, String h3, String correct_answ)
    {
        borderLayout = new BorderLayout(20,20);
        frame = new JFrame("Wiki Guess!");
        frame.setLayout(borderLayout);
        main_panel = new JPanel(new GridLayout(2,1,20,20));
        frame.add(main_panel,borderLayout.CENTER);
        l_panel = new JPanel(new GridLayout(2,1,20,20));
        btn_panel = new JPanel(new GridLayout(3,1,20,20));

        ActionListener listener = event ->
        {
            Object src = event.getSource();

            if((src == a))
            {
                if(h1 == correct_answ)
                {
                    a.setBackground(Color.GREEN);
                }
                else
                {
                    a.setBackground(Color.RED);
                }

            }
            else if((src == b))
            {
                if(h2 == correct_answ)
                {
                    b.setBackground(Color.GREEN);
                }
                else
                {
                    b.setBackground(Color.RED);
                }

            }
            else if((src == c))
            {
                if(h3 == correct_answ)
                {
                    c.setBackground(Color.GREEN);
                }
                else
                {
                    c.setBackground(Color.RED);
                }

            }

            a.repaint(); b.repaint(); c.repaint();
        };

        l_sentence = new JLabel(sentence);
        l_category = new JLabel(category);
        l_panel.add(l_category);
        l_panel.add(l_sentence);

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        main_panel.add(l_panel);
        main_panel.add(btn_panel);


        btn_grp = new ButtonGroup();
        a = new JRadioButton("a. " + h1);
        btn_grp.add(a);

        b = new JRadioButton("b. " + h2);
        btn_grp.add(b);

        c = new JRadioButton("c. " + h3);
        btn_grp.add(c);


        btn_panel.add(a);
        a.addActionListener(listener);
        btn_panel.add(b);
        b.addActionListener(listener);
        btn_panel.add(c);
        c.addActionListener(listener);


        frame.pack();
        frame.setVisible(true);
    }


}
