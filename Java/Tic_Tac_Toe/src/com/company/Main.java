package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class Main extends JFrame implements ActionListener {
    JButton reset,new_game;
    JButton btn[][];
    Font f;
    JTextField p1p,p2p;
    int tura = 1,p1=0,p2=0;
    JPanel panel_btn;
    public Main() throws FontFormatException {
        super("TTT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(450, 100);
        setSize(500, 500);
        setResizable(false);
        f = new Font("New Times Roman", Font.BOLD, 100);
        btn = new JButton[3][3];
        panel_btn= new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < btn.length; i++) {
            for (int j = 0; j < btn[i].length; j++) {
                btn[i][j] = new JButton();
                btn[i][j].setFont(f);
                btn[i][j].addActionListener(this);
                btn[i][j].setFocusable(false);
                panel_btn.add(btn[i][j]);
            }
        }
        add(panel_btn);
        reset = new JButton("Reset");
        reset.addActionListener(this);
        reset.setFocusable(false);
        JPanel panel = new JPanel();
        panel.add(reset);
        p1p = new JTextField("Player 1: "+ p1);
        new_game = new JButton("New game");
        new_game.addActionListener(this);
        new_game.setFocusable(false);
        p2p = new JTextField("Player 2: "+ p2);
        p1p.setFocusable(false);
        p2p.setFocusable(false);
        p1p.setEditable(false);
        p2p.setEditable(false);
        p1p.setBackground(Color.BLUE);
        p2p.setBackground(Color.RED);
        p1p.setForeground(Color.GREEN);
        p2p.setForeground(Color.GREEN);
        panel.add(new_game);
        panel.add(p1p);
        panel.add(p2p);
        add(panel,"South");
        setVisible(true);
    }

    public static void main(String[] args) throws FontFormatException {
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Object zrodlo = ev.getSource();

            for (int i = 0; i < btn.length; i++) {
                for (int j = 0; j < btn[i].length; j++) {
                    if (tura % 2 != 0) {

                        if (zrodlo == btn[i][j] && btn[i][j].getText().equals("")) {

                            btn[i][j].setText("X");
                            btn[i][j].setForeground(Color.BLUE);
                            tura++;
                        }
                    }
                    if(tura % 2 == 0)
                    {
                        if (zrodlo == btn[i][j] && btn[i][j].getText().equals("")) {

                            btn[i][j].setText("O");
                            btn[i][j].setForeground(Color.RED);
                            tura++;
                        }
                    }
                }
            }
        if(checkForWin())
        {
            if(tura%2==0)
            {
                JOptionPane.showMessageDialog(this,"X Win");
                p1++;
                p1p.setText("Player 1: " + p1);
                reset();
            }
            else if(tura%2 !=0)
            {
                JOptionPane.showMessageDialog(this,"O Win");
                p2++;
                p2p.setText("Player 2: "+ p2);
                reset();
            }
        }
            if (zrodlo==reset)
            {
                reset();
            }

            if (zrodlo==new_game)
            {
                reset();
                p1=0;
                p1p.setText("Player 1: " + p1);
                p2=0;
                p2p.setText("Player 2: " + p2);
            }
        }
        private void reset()
        {
            for (int i = 0; i < btn.length; i++) {
                for (int j = 0; j < btn[i].length; j++) {
                    btn[i][j].setText("");
                    tura=1;
                }
            }
        }
    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = btn[i][j].getText();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }
    }




