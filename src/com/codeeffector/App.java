package com.codeeffector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.*;
public class App {
    private JButton delayButton;
    private JButton wahWahButton;
    private JButton flangerButton;
    private JButton reverbButton;
    private JButton chorusButton;
    private JButton button6;
    private JPanel panelMain;
    private JButton playEffectedSoundButton;
    private JButton loadSampleButton;

    public App() {
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File soundFile = new File("sample.wav");
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volume.setValue(1.0f); // Reduce volume by 10 decibels.
                    clip.start();
                    JOptionPane.showMessageDialog(null, "Click OK to stop music");
                    clip.drain();
                } catch (UnsupportedAudioFileException ea) {
                    ea.printStackTrace();
                } catch (IOException ea) {
                    ea.printStackTrace();
                } catch (LineUnavailableException ea) {
                    ea.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args)
            throws Exception
    {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}



