package com.codeeffector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;

import com.jsyn.JSyn;
import com.jsyn.ports.*;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.*;
import com.jsyn.util.SampleLoader;
import com.jsyn.util.WaveRecorder;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class App {
    private JButton delayButton;
    private JButton distortionButton;
    private JButton flangerButton;
    private JButton reverbButton;
    private JButton chorusButton;
    private JButton button6;
    private JPanel panelMain;
    private JButton playEffectedSoundButton;
    private JButton loadSampleButton;
    private JButton loadDefaultSampleButton;
    private JTextArea noSelectedSampleTextArea;
    public JSlider slider1;
    private JSlider slider2;
    private JSlider slider3;
    public JButton powerDelay;
    public JLabel decayVal;
    public JLabel feedbackVal;
    private JLabel delayVal;
    private JButton button1;
    private File soundFile;
    private VariableRateDataReader samplePlayer;
    private App() {

        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                        return;
                    }
                    FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                    play(samples);
                } catch (IOException ea) {
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
                new Thread(new Runnable() {
                    public void run(){

                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        int delay_ms_max = 60;
                        int delay_ms_min = 20;
                        //int delay = 1200;
                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

                        float[] dsamples = new float[samples.getNumFrames() * samples.getChannelsPerFrame()];
                        for (int i = 0; i < dsamples.length; i++)
                            dsamples[i] = (float) samples.readDouble(i);

                        double frame_rate = samples.getFrameRate();

                        //Delayed sample
                        FloatSample delayedsample = calculate_varying_delay(40, delay_ms_max, delay_ms_min,1, 1f, 1f, samples, samples.getChannelsPerFrame());
                        delayedsample.setFrameRate(frame_rate);
                        delayedsample.setChannelsPerFrame(samples.getChannelsPerFrame());

                        play(delayedsample);



                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                    }
                }).start();
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

        delayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        // Number parameters
                        int delayms = 500; //in miliseconds
                        int numberOfRepetitions = 50;
                        float echoAmplitude = 1f;
                        float echoDecay = 0.8f;

                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                        int numberOfChannels = samples.getChannelsPerFrame();

                        double frame_rate = samples.getFrameRate();

                        //Delayed sample
                        FloatSample delayedsample = calculate_delay(delayms, numberOfRepetitions, echoAmplitude, echoDecay, samples,numberOfChannels);
                        delayedsample.setFrameRate(frame_rate);
                        delayedsample.setChannelsPerFrame(2);

                        play(delayedsample);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });


        distortionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

                        double frame_rate = samples.getFrameRate();
                        int numberOfChannels = samples.getChannelsPerFrame();


                        FloatSample distortedSamples = calculate_distortion(samples,numberOfChannels,0.1f);
                        distortedSamples.setFrameRate(frame_rate);
                        play(distortedSamples);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                decayVal.setText(String.format("%.2f", source.getValue()*0.01));
            }
        });
        slider3.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                feedbackVal.setText(Integer.toString(source.getValue()));
            }
        });
        slider2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                delayVal.setText(Integer.toString(source.getValue()));
            }
        });
        powerDelay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton powerDelay = (JButton)e.getSource();
                String txtPower = powerDelay.getText();
                if(txtPower.equals("On")){
                    powerDelay.setText("Off");
                    powerDelay.setBackground(Color.red);

                }else{
                    powerDelay.setText("On");
                    powerDelay.setBackground(Color.green);

                }
            }
        });
        playEffectedSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    try {
                        if (soundFile == null) {
                        } else {
                            FloatSample effectedSignal = SampleLoader.loadFloatSample(soundFile);

                            //Process delay
                            if (powerDelay.getText().equals("On")) {
                                double decay = slider1.getValue() * 0.01;
                                int feedback = slider3.getValue();
                                int delay = slider2.getValue();
                                effectedSignal = getDelayed((float) decay, feedback, delay);
                            }



                            FloatSample finalEffectedSignal = effectedSignal;
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        play(finalEffectedSignal);
                                    } catch (FileNotFoundException e1) {
                                        e1.printStackTrace();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
            }
        });
        loadDefaultSampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });
    }
    private FloatSample getDelayed(float decay, int feedback, int delay){
        try {
            if (soundFile == null) {
                getFile();
            } else {
                // Number parameters
                int delayms = delay; //in miliseconds
                int numberOfRepetitions = feedback;
                float echoAmplitude = 1f;
                float echoDecay = decay;

                FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                int numberOfChannels = samples.getChannelsPerFrame();

                double frame_rate = samples.getFrameRate();

                //Delayed sample
                FloatSample delayedsample = calculate_delay(delayms, numberOfRepetitions, echoAmplitude, echoDecay, samples,numberOfChannels);
                delayedsample.setFrameRate(frame_rate);
                delayedsample.setChannelsPerFrame(numberOfChannels);
                return delayedsample;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;

    }
    private FloatSample calculate_distortion(FloatSample samples,int numChannels, float threshold){
        int buffer_length = samples.getNumFrames();
        double frame_rate = samples.getFrameRate();

        float[] distorted_array = new float[buffer_length];
        float[] dsamples = new float[buffer_length];

        // Fill tables with original sample
        for (int i = 0; i < buffer_length; i++) {
            dsamples[i] = (float) samples.readDouble(i);
        }

        for (int i = 0; i < buffer_length; i++) {
            float x = dsamples[i];

            if (Math.abs(x)> threshold){
                distorted_array[i] =  (x/Math.abs(x)*(1-(float)Math.exp(Math.pow(x,2)/Math.abs(x))));
            }else{
                distorted_array[i] = dsamples[i];
            }
        }


        FloatSample distorted_sample = new FloatSample(distorted_array,numChannels);
        return distorted_sample;
    }

    private FloatSample calculate_low_pass(FloatSample samples,int numChannels){
        int buffer_length = samples.getNumFrames();
        double frame_rate = samples.getFrameRate();

        float[] low_pass_array = new float[buffer_length];
        float[] dsamples = new float[buffer_length];

        // Fill tables with original sample
        for (int i = 0; i < buffer_length; i++) {
            dsamples[i] = (float) samples.readDouble(i);
        }





        FloatSample low_pass_sample = new FloatSample(low_pass_array,numChannels);
        return low_pass_sample;
    }


    private FloatSample calculate_delay(int delayms, int numberOfRepetitions, float echoAmplitude, float echoDecay, FloatSample samples, int numChannels) throws IOException {
        int echoIndex = 0;
        float echoValue = 0f;

        // Get sample info
        int buffer_length = samples.getNumFrames();
        double frame_rate = samples.getFrameRate();

        //Delay calculated to number of frames
        int delayfr = (int) Math.ceil(frame_rate * delayms / 1000);


        float[] dsamples = new float[buffer_length];
        int delayedFrames = buffer_length + (delayfr * numberOfRepetitions);
        float[] delayedsample = new float[delayedFrames];

        // Fill tables with original sample
        for (int i = 0; i < buffer_length; i++) {
            dsamples[i] = (float) samples.readDouble(i);
            delayedsample[i] = dsamples[i];
        }

        System.out.println(samples.getNumFrames());

        // Add delays
        for (int i = 1; i <= numberOfRepetitions; i++) {
            echoAmplitude = echoAmplitude * echoDecay;
            for (int j = 0; j < buffer_length; j++) {
                echoIndex = j + (delayfr * i);
                echoValue = (dsamples[j] * echoAmplitude);
                delayedsample[echoIndex] = echoValue + delayedsample[echoIndex];
            }
        }

        FloatSample retSample = new FloatSample(delayedsample,numChannels);

        return  retSample;
    }

    private FloatSample calculate_varying_delay(int delay_change_after_x_frames, int delaymax, int delaymin, int numberOfRepetitions, float echoAmplitude, float echoDecay, FloatSample samples, int numChannels) throws IOException {
        //Calculates delay for varying time windows

        int echoIndex = 0;
        float echoValue = 0f;

        // Get sample info
        int buffer_length = samples.getNumFrames()*numChannels;
        double frame_rate = samples.getFrameRate();

        //Delay calculated to number of frames
        //int delayfr = (int) Math.ceil(frame_rate * delayms / 1000);
        int number_of_different_delays = delaymax-delaymin;

        int[] delays = new int[number_of_different_delays];

        int delayfr = (int)Math.ceil(frame_rate * (delaymax) / 1000);
        for(int i = 0; i < number_of_different_delays; i++){
            delays[i] = (int) Math.ceil(frame_rate * (i) / 1000);

        }

        float[] dsamples = new float[buffer_length];
        int delayedFrames = buffer_length + (delayfr * numberOfRepetitions);
        float[] delayedsample = new float[delayedFrames];

        // Fill tables with original sample
        for (int i = 0; i < buffer_length; i++) {
            dsamples[i] = (float) samples.readDouble(i);
            delayedsample[i] = dsamples[i];
        }

        // Add delays
        int delay_current = delays[0];
        int counter = 0;
        for (int i = 1; i <= numberOfRepetitions; i++) {
            echoAmplitude = echoAmplitude * echoDecay;
            for (int j = 0; j < buffer_length; j++) {
                if(j%delay_change_after_x_frames==0){
                    if(counter < number_of_different_delays-1){
                        counter+=1;
                    }else{counter = 0;}

                  delay_current = delays[counter];
                }

                if(j + delay_current < buffer_length){
                    echoIndex = j + (delayfr * i);
                    echoValue = (dsamples[j+delay_current] * echoAmplitude);
                    delayedsample[echoIndex] = echoValue + delayedsample[echoIndex];
                }
                else{
                    echoIndex = j + (delayfr * i);
                    echoValue = (dsamples[j] * echoAmplitude);
                    delayedsample[echoIndex] = echoValue + delayedsample[echoIndex];
                }
            }
        }

        FloatSample retSample = new FloatSample(delayedsample,numChannels);

        return  retSample;
    }


    private void play(FloatSample samples) throws IOException {
        Synthesizer synth = JSyn.createSynthesizer();


        try {

            LineOut lineOut = new LineOut();
            synth.add(lineOut = new LineOut());
            System.out.println("Sample has: channels  = " + samples.getChannelsPerFrame());
            System.out.println("            frames    = " + samples.getNumFrames());
            System.out.println("            rate      = " + samples.getFrameRate());
            System.out.println("            loopStart = " + samples.getSustainBegin());
            System.out.println("            loopEnd   = " + samples.getSustainEnd());

            if (samples.getChannelsPerFrame() == 1) {
                synth.add(samplePlayer = new VariableRateMonoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
                samplePlayer.output.connect(0, lineOut.input, 1);

            } else if (samples.getChannelsPerFrame() == 2) {
                synth.add(samplePlayer = new VariableRateStereoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
                samplePlayer.output.connect(1, lineOut.input, 1);
            } else {
                throw new RuntimeException("Can only play mono or stereo samples.");
            }
            synth.start();
            samplePlayer.rate.set(samples.getFrameRate());
            samples.setNumFrames(10000);
            lineOut.start();

            samplePlayer.dataQueue.queue(samples);
            synth.startUnit(lineOut);

            do {
                synth.sleepFor(1.0);
            } while (samplePlayer.dataQueue.hasMore());
            synth.sleepFor(0.5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        synth.stop();

    }

    public float logartimic(float val){
        float threshold = 0.6f;
        if (val < threshold)
            return val;
        float over = val - threshold;
        float log_val = (float)Math.log(over);
        return threshold + log_val;
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
    public void loadFile(){
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        System.out.println(classloader.getParent().getName());
        soundFile = new File(classloader.getResource("acoustic.wav").getFile());
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
        JFrame frame = new JFrame("Effector");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton b=new JButton("button1");
        frame.pack();

        buildGUI();
        frame.setVisible(true);
    }
    static public void buildGUI(){
        java.util.Hashtable<Integer,JLabel> labelTable = new java.util.Hashtable<Integer,JLabel>();
        labelTable.put(100, new JLabel("1.0"));
        labelTable.put(75, new JLabel("0.75"));
        labelTable.put(50, new JLabel("0.50"));
        labelTable.put(25, new JLabel("0.25"));
        labelTable.put(0, new JLabel("0.0"));

    }

}



