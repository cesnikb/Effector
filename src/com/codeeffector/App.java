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

import com.jsyn.unitgen.InterpolatingDelay;

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
                    if (soundFile == null) {
                        getFile();
                        return;
                    }

                    Synthesizer synth = JSyn.createSynthesizer();
                    LineOut lineOut = new LineOut();
                    synth.add(lineOut);
                    synth.start();


                    FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                    float[] dsamples = new float[samples.getNumFrames() * 2];
                    for (int i = 0; i < dsamples.length; i++)
                        dsamples[i] = (float) samples.readDouble(i);
                    double fr = samples.getFrameRate();
                    samples = new FloatSample(dsamples);
                    samples.setFrameRate(fr);
                    VariableRateMonoReader player = new VariableRateMonoReader();
                    synth.add(player);
                    player.rate.set(samples.getFrameRate() * 2);
                    player.dataQueue.queue(samples);

                    //File outputWave = new File("output.wav");
                    //WaveRecorder recorder = new WaveRecorder(synth, outputWave);
                    //player.output.connect(0, recorder.getInput(), 0);
                    //recorder.start();

                    player.output.connect(0, lineOut.input, 0);
//                    player.output.connect(0, lineOut.input, 1);
                    synth.startUnit(lineOut);
                    do {
                        synth.sleepFor(1.0);
                    } while (player.dataQueue.hasMore());

                    //recorder.stop();
                    //recorder.close();
                    synth.stop();
                } catch (IOException | InterruptedException ea) {
                    ea.printStackTrace();
                }
            }
        });

        flangerButton.addActionListener(new ActionListener() {
            /*
             * We take an array, copy it and shift it. Then we apply some changes to the
             * copied signal and join in with the original one
             * to get the flanger effect.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        int delay = 1200;

                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                        float[] dsamples = new float[samples.getNumFrames() * 2];
                        for (int i = 0; i < dsamples.length; i++)
                            dsamples[i] = (float) samples.readDouble(i);

                        System.out.println(samples.getFrameRate());

                        float[] delayed = new float[dsamples.length + delay];
                        float[] originals = new float[delayed.length];

                        for (int i = 0; i < dsamples.length; i++) {
                            delayed[i + delay] = dsamples[i];
                            originals[i] = dsamples[i];
                        }
                        for (int i = 0; i < delay; i++) {
                            delayed[i] = 0;
                            originals[i + delay] = 0;
                        }

                        double angle = 0.0;
                        double modulo_i = 0.0;
                        double sample_rate = samples.getFrameRate();
                        double oscilator = 0.0;

                        for (int i = 0; i < delayed.length; i++) {
                            modulo_i = i % sample_rate * 10;
                            if (i % sample_rate == 0) {
                                angle = 0;
                            }
                            angle += (float) (2 * Math.PI) * 2 * (float) (modulo_i / sample_rate); // sin(2*pi* f  *(t/Fs))
                            oscilator = (float) Math.sin(angle);
                            delayed[i] = delayed[i] * (float) oscilator;
                        }

                        float[] mixed = new float[originals.length];
                        for (int i = 0; i < originals.length; i++) {
                            mixed[i] = originals[i] + delayed[i];
                        }

                        samples = new FloatSample(mixed);

                        double fr = samples.getFrameRate();
                        samples.setFrameRate(fr);

                        play(samples);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
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
        delayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        int numberOfEchoes = 2;
                        int delayms = 1000; //in miliseconds
                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

                        double frame_rate = samples.getFrameRate();
                        int buffer_length = samples.getNumFrames() * 2;
                        int delayfr = (int) (frame_rate * delayms / 1000);//Delay calculated to number of frames

                        float echoAmplitude = 1f;

                        float[] dsamples = new float[buffer_length];
                        float[] delayedsample = new float[buffer_length + (delayfr * numberOfEchoes)];
                        int echoIndex = 0;
                        float echoValue = 0f;

                        for (int i = 0; i < buffer_length; i++) {
                            dsamples[i] = (float) samples.readDouble(i);
                            delayedsample[i] = dsamples[i];
                        }
                        for (int i = 1; i <= numberOfEchoes; i++) {
                            echoAmplitude = echoAmplitude * 0.6f;
                            for (int j = 0; j < buffer_length; j++) {
                                echoIndex = j + (delayfr * i);
                                echoValue = (dsamples[j] * echoAmplitude);
                                delayedsample[echoIndex] = echoValue + delayedsample[echoIndex];
                            }
                        }
                        samples = new FloatSample(delayedsample);
                        samples.setFrameRate(frame_rate);
                        play(samples);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    private void play(FloatSample samples) {
        try {
            Synthesizer synth = JSyn.createSynthesizer();
            LineOut lineOut = new LineOut();
            synth.add(lineOut);
            synth.start();

            VariableRateMonoReader player = new VariableRateMonoReader();
            synth.add(player);
            player.rate.set(samples.getFrameRate() * 2);
            player.dataQueue.queue(samples);
            File outputWave = new File("output.wav");
            WaveRecorder recorder = new WaveRecorder(synth, outputWave);
            player.output.connect(0, recorder.getInput(), 0);
            recorder.start();

            player.output.connect(0, lineOut.input, 0);
            synth.startUnit(lineOut);
            do {
                synth.sleepFor(1.0);
            } while (player.dataQueue.hasMore());

            recorder.stop();
            recorder.close();
            synth.stop();
            synth.stop();
        } catch (IOException | InterruptedException ea) {
            ea.printStackTrace();
        }
    }


    public void getFile() throws IOException {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MPEG3 songs", "mp3", "wav", "mp4");
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
            throws Exception {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}



