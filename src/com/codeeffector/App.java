package com.codeeffector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.*;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.util.SampleLoader;
import com.jsyn.util.WaveRecorder;

import javax.swing.filechooser.FileNameExtensionFilter;

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
    private JTextArea noSelectedSampleTextArea;
    private File soundFile;

    private App() {
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(soundFile == null){
                        getFile();
                        return;
                    }
//                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);


                    Synthesizer synth = JSyn.createSynthesizer();
                    LineOut lineOut = new LineOut();
                    synth.add(lineOut);
                    synth.start();


                    FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                    float[] dsamples = new float[samples.getNumFrames()*2];
                    for (int i = 0; i < dsamples.length; i++)
                        dsamples[i] = (float) samples.readDouble(i);
                    double fr = samples.getFrameRate();
                    samples = new FloatSample(dsamples);
                    samples.setFrameRate(fr);
                    VariableRateMonoReader player = new VariableRateMonoReader();
                    synth.add(player);
                    player.rate.set(samples.getFrameRate()*2);
                    player.dataQueue.queue(samples);

                    File outputWave = new File("output.wav");
                    WaveRecorder recorder = new WaveRecorder(synth, outputWave);
                    player.output.connect(0, recorder.getInput(), 0);
                    recorder.start();

                    player.output.connect(0, lineOut.input, 0);
//                    player.output.connect(0, lineOut.input, 1);
                    synth.startUnit(lineOut);
                    do {
                        synth.sleepFor(1.0);
                    } while (player.dataQueue.hasMore());

                    recorder.stop();
                    recorder.close();
                    synth.stop();
                } catch (IOException | InterruptedException ea) {
                    ea.printStackTrace();
                }
            }
        });

        loadSampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
        });
        playEffectedSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    float[] buff = getBuffer();
                    FloatSample samples = new FloatSample(buff);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }
    public void getFile() throws IOException {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MPEG3 songs", "mp3","wav");
        fc.setFileFilter(filter);

        int returnVal = fc.showOpenDialog(loadSampleButton);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            noSelectedSampleTextArea.setText("Imported sample: " + file.getName());
            soundFile = new File(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
            //This is where a real application would open the file.
        }

    }
    public float[] getBuffer() throws IOException {
        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

        float[] dsamples = new float[samples.getNumFrames()];
        for (int i = 0; i < dsamples.length; i++)
            dsamples[i] = (float) samples.readDouble(i);

        return dsamples;
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



