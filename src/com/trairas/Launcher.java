package com.trairas;

import javax.swing.*;

/**
 * Created by nig on 05/04/17.
 */
public class Launcher extends JFrame{

    public Launcher(JPanel painel){

        this.setTitle("Peer-To-Peer");
        this.add(painel);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }




}
