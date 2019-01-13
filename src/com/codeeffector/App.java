package com.codeeffector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.*;
import com.jsyn.util.SampleLoader;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class App {
    private JButton delayButton;
    private JButton distortionButton;
    private JButton flangerButton;
    private JButton reverbButton;
    private JButton distortionOffButton;
    private JButton lowpassButton;
    private JButton chorusButton;
    private JButton button6;
    private JPanel panelMain;
    private JButton playEffectedSoundButton;
    private JButton loadSampleButton;
    private JButton lowPassButton;
    private JButton loadDefaultSampleButton;
    private JTextArea noSelectedSampleTextArea;
    public JSlider slider1;
    private JSlider slider2;
    private JSlider slider3;
    public JButton powerDelay;
    public JLabel decayVal;
    public JLabel feedbackVal;
    private JLabel delayVal;
    private JButton phaserButton;
    private JSlider distSlider;
    private JSlider slider5;
    private JLabel thresholdVal;
    private JLabel smoothingVal;
    private JSlider slider6;
    private JLabel volume_val;
    private JLabel maxDelayVal;
    private JButton powerFlanger;
    private JSlider maxDelaySlider;
    private JSlider minDelaySlider;
    private JSlider delayChangeSlider;
    private JLabel delayChangeVal;
    private JLabel minDelayVal;
    private JSlider chorusDelay;
    private JLabel chorusDelayVal;
    private JSlider chorusFeedback;
    private JLabel chorusFeedbackVal;
    private JSlider reverbDelay;
    private JButton powerReverb;
    private JButton powerChorus;
    private JButton powerPhaser;
    private JSlider phaserGain;
    private JSlider reverbFeedback;
    private JLabel reverbDelayVal;
    private JLabel reverbFeedbackVal;
    private JLabel phaserGainVal;
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


        phaserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {

                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

                        double frame_rate = samples.getFrameRate();

                        //Delayed sample
                        FloatSample phased_sample = all_pass(samples, 0.85f);
                        phased_sample.setFrameRate(frame_rate);
                        phased_sample.setChannelsPerFrame(2);

                        play(phased_sample);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });

        chorusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {

                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                        int numberOfChannels = samples.getChannelsPerFrame();

                        double frame_rate = samples.getFrameRate();

                        //Delayed sample
                        FloatSample delayedsample = calculate_delay(12, 1, 0.85f, 0.6f, samples,numberOfChannels);
                        delayedsample.setFrameRate(frame_rate);
                        delayedsample.setChannelsPerFrame(2);

                        play(delayedsample);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });

        flangerButton.addActionListener(new ActionListener() {
            /*
             * Flanger: copies the signal delays it for varying amounts throughout the sample and merge with original
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run(){

                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        int delay_ms_max = 25;
                        int delay_ms_min = 5;

                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

                        float[] dsamples = new float[samples.getNumFrames() * samples.getChannelsPerFrame()];
                        for (int i = 0; i < dsamples.length; i++)
                            dsamples[i] = (float) samples.readDouble(i);

                        double frame_rate = samples.getFrameRate();

                        //Delayed sample
                        FloatSample delayedsample = calculate_varying_delay(45, delay_ms_max, delay_ms_min,1, 1f, 1f, samples, samples.getChannelsPerFrame());
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
                        int delayms = slider2.getValue(); //in miliseconds
                        int numberOfRepetitions = slider3.getValue();
                        float echoAmplitude = 1f;
                        float echoDecay = slider1.getValue() * 0.01f;

                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);
                        int numberOfChannels = samples.getChannelsPerFrame();

                        double frame_rate = samples.getFrameRate();

                        //Delayed sample
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


                        FloatSample distortedSamples = calculate_distortion(samples,0.1f);
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
                redGreen(e);
            }
        });
        playEffectedSoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    try {
                        if (soundFile == null) {
                        } else {
                            FloatSample effectedSignal = SampleLoader.loadFloatSample(soundFile);

                            //Process Delay
                            if (powerDelay.getText().equals("On")) {
                                double decay = slider1.getValue() * 0.01;
                                int feedback = slider3.getValue();
                                int delay = slider2.getValue();
                                effectedSignal = getDelayed((float) decay, feedback, delay);
                            }

                            //Process Distortion
                            if (distortionOffButton.getText().equals("On")) {
                                int thresholdDist = distSlider.getValue();
                                effectedSignal = calculate_distortion(effectedSignal, thresholdDist*0.01f);
                            }
                            //Process Phaser
                            if (phaserButton.getText().equals("On")) {
                                float phaserGainVal = phaserGain.getValue() * 0.01f;
                                effectedSignal= all_pass(effectedSignal, phaserGainVal);

                            }

                            //Process Flanger
                            if (powerFlanger.getText().equals("On")) {
                                int delayChangeV = delayChangeSlider.getValue();
                                int minDelayV = minDelaySlider.getValue();
                                int maxDelayV = maxDelaySlider.getValue();
                                effectedSignal = calculate_varying_delay(delayChangeV, maxDelayV, minDelayV,1, 1f, 1f, effectedSignal, effectedSignal.getChannelsPerFrame());

                            }

                            //Process Chorus
                            if (powerChorus.getText().equals("On")) {
                                int chorusDelayV = chorusDelay.getValue();
                                int chorusFeedbackV = chorusFeedback.getValue();
                                effectedSignal = calculate_delay(chorusDelayV, chorusFeedbackV, 0.85f, 0.6f, effectedSignal,effectedSignal.getChannelsPerFrame());

                            }

                            //Process Reverb
                            if (powerReverb.getText().equals("On")) {
                                int reverbDelayV = reverbDelay.getValue();
                                int reverbFeedbackV = reverbFeedback.getValue();
                                effectedSignal = calculate_reverb(effectedSignal,effectedSignal.getChannelsPerFrame(),reverbFeedbackV, reverbDelayV);


                            }

                            //Process Lowpass
                            if (lowpassButton.getText().equals("On")) {
                                int lowpass = slider5.getValue();
                                effectedSignal = calculate_low_pass(effectedSignal, lowpass);
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
        reverbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

                        double frame_rate = samples.getFrameRate();
                        int numberOfChannels = samples.getChannelsPerFrame();


                        FloatSample reverbSamples = calculate_reverb(samples,numberOfChannels,20,6);
                        reverbSamples.setFrameRate(frame_rate);
                        play(reverbSamples);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        lowPassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (soundFile == null) {
                        getFile();
                    } else {
                        FloatSample samples = SampleLoader.loadFloatSample(soundFile);

                        double frame_rate = samples.getFrameRate();
                        int numberOfChannels = samples.getChannelsPerFrame();

                        FloatSample lowpassSamples = calculate_low_pass(samples,500);
                        lowpassSamples.setFrameRate(frame_rate);
                        play(lowpassSamples);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        distortionOffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redGreen(e);
            }
        });
        lowpassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redGreen(e);
            }
        });
        distSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                thresholdVal.setText(String.format("%.2f", source.getValue()*0.01));
            }
        });
        slider5.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                smoothingVal.setText(Integer.toString(source.getValue()));
            }
        });
        slider6.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                volume_val.setText(Integer.toString(source.getValue()));
            }
        });
        maxDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if(source.getValue()<= minDelaySlider.getValue()){
                    minDelaySlider.setValue(source.getValue()-1);
                }
                maxDelayVal.setText(Integer.toString(source.getValue()));

            }
        });
        minDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                minDelayVal.setText(Integer.toString(source.getValue()));
                maxDelaySlider.setMinimum(source.getValue() + 1);
            }
        });
        delayChangeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                delayChangeVal.setText(Integer.toString(source.getValue()));
            }
        });
        chorusDelay.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                chorusDelayVal.setText(Integer.toString(source.getValue()));
            }
        });
        chorusFeedback.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                chorusFeedbackVal.setText(Integer.toString(source.getValue()));

            }
        });
        powerFlanger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redGreen(e);
            }
        });
        powerChorus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redGreen(e);

            }
        });
        powerReverb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redGreen(e);

            }
        });
        powerPhaser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redGreen(e);

            }
        });
        reverbFeedback.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                reverbFeedbackVal.setText(Integer.toString(source.getValue()));
            }
        });
        reverbDelay.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                reverbDelayVal.setText(Integer.toString(source.getValue()));
            }
        });
        phaserGain.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                phaserGainVal.setText(String.format("%.2f", source.getValue()*0.01));
            }
        });
    }
    private void redGreen(ActionEvent e){
        JButton power = (JButton)e.getSource();
        String txtPower = power.getText();
        if(txtPower.equals("On")){
            power.setText("Off");
            power.setBackground(Color.red);

        }else{
            power.setText("On");
            power.setBackground(Color.green);

        }
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

    private FloatSample calculate_reverb(FloatSample samples,int numChannels, int repetitions, int delay) throws IOException {
        // Get sample info
        int buffer_length = samples.getNumFrames()*numChannels;
        double frame_rate = samples.getFrameRate();
        float[] dsamples = new float[buffer_length];
        float[] dsamples_lowpassed = new float[buffer_length];

        int delayfr = (int) Math.ceil(frame_rate * delay*repetitions / 1000);
        int delayedFrames = buffer_length + (delayfr * repetitions);
        float[] delayedsample = new float[delayedFrames];



        for (int i = 0; i < buffer_length; i++) {
            dsamples[i] = (float) samples.readDouble(i);
            delayedsample[i] = dsamples[i];

        }

        for (int i = 0; i < repetitions; i++) {
            float echoAmplitude = 1f;
            float echoDecay = 0.6f;
            int echoIndex = 0;
            float echoValue = 0f;

            float[] delay1 = calculate_reverb_delay(delay*(i+1), repetitions, 1.0f, 0.8f, samples, numChannels);
            FloatSample dd = new FloatSample(delay1,numChannels);
            FloatSample lowpassed = calculate_low_pass(dd, 200);
            for (int k = 0; k < buffer_length; k++) {
                dsamples_lowpassed[k] = (float) lowpassed.readDouble(k);
            }
            for (int o = 1; o <= repetitions; o++) {
                echoAmplitude = echoAmplitude * echoDecay;
                for (int j = 0; j < buffer_length; j++) {
                    echoIndex = j + (delayfr * o);
                    echoValue = (dsamples_lowpassed[j] * echoAmplitude);
                    delayedsample[echoIndex] = echoValue + delayedsample[echoIndex];
                }
            }
        }

        return  new FloatSample(delayedsample,numChannels);
    }
    private float[] calculate_reverb_delay(int delayms, int numberOfRepetitions, float echoAmplitude, float echoDecay, FloatSample samples, int numChannels) throws IOException {
        int echoIndex = 0;
        float echoValue = 0f;

        // Get sample info
        int buffer_length = samples.getNumFrames()*numChannels;
        double frame_rate = samples.getFrameRate();

        //Delay calculated to number of frames
        int delayfr = (int) Math.ceil(frame_rate * delayms / 1000);


        float[] dsamples = new float[buffer_length];
        int delayedFrames = buffer_length + (delayfr * numberOfRepetitions);
        float[] delayedsample = new float[delayedFrames];

        // Fill tables with original sample
        for (int i = 0; i < buffer_length; i++) {
            dsamples[i] = (float) samples.readDouble(i);
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

        return  delayedsample;
    }

    private FloatSample calculate_distortion(FloatSample samples, float threshold){
        int numChannels = samples.getChannelsPerFrame();
        int buffer_length = samples.getNumFrames()*numChannels;

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


        return new FloatSample(distorted_array,numChannels);
    }

    private FloatSample volume_percentagte(FloatSample samples, float percentage){
        //Percentage can be over 1 => volume up; or less than 1 => volume down.
        int buffer_length = samples.getNumFrames()*samples.getChannelsPerFrame();
        float[] dsamples = new float[buffer_length];

        for(int i=0; i < buffer_length; i++){
            dsamples[i] = percentage * (float)samples.readDouble(i);
        }

        return new FloatSample(dsamples ,samples.getChannelsPerFrame());
    }

    private FloatSample calculate_low_pass(FloatSample samples,int smoothing){
        int numChannels = samples.getChannelsPerFrame();
        int buffer_length = samples.getNumFrames()*numChannels;

        float[] dsamples = new float[buffer_length];
        float[] lowpassed_array = new float[buffer_length];

        // Fill tables with original sample
        for (int i = 0; i < buffer_length; i++) {
            dsamples[i] = (float) samples.readDouble(i);
        }
        float val = dsamples[0];
        for (int i = 1; i < buffer_length; i++) {
            float currentVal = dsamples[i];
            currentVal += (currentVal - val);
            val += (currentVal - val) / smoothing;
            lowpassed_array[i] = val;

        }

        return new FloatSample(lowpassed_array,numChannels);
    }
    private FloatSample all_pass(FloatSample sample, float gain) throws IOException {
        // y(n) = -gain * x(n) + x(n - 1) + gain * y(n - 1) => all pass from jsyn
        int buffer_length = sample.getNumFrames()*sample.getChannelsPerFrame();

        float [] output = new float [buffer_length];

        float x_prev = (float)sample.readDouble(0)*gain;
        float x_curr = 0;
        float out = 0;
        output[0] = gain*-x_prev;

        for (int i = 1; i < buffer_length; i++) {
            x_curr = (float)sample.readDouble(i);
            out = gain* (output[i-1] - x_curr) + x_prev;
            x_prev = x_curr;
            output[i] = out;
        }

        return new FloatSample(output,sample.getChannelsPerFrame());
    }

    private FloatSample calculate_delay(int delayms, int numberOfRepetitions, float echoAmplitude, float echoDecay, FloatSample samples, int numChannels) throws IOException {
        int echoIndex = 0;
        float echoValue = 0f;

        // Get sample info
        int buffer_length = samples.getNumFrames()*numChannels;
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


        // Add delays
        for (int i = 1; i <= numberOfRepetitions; i++) {
            echoAmplitude = echoAmplitude * echoDecay;
            for (int j = 0; j < buffer_length; j++) {
                echoIndex = j + (delayfr * i);
                echoValue = (dsamples[j] * echoAmplitude);
                delayedsample[echoIndex] = echoValue + delayedsample[echoIndex];
            }
        }

        return new FloatSample(delayedsample,numChannels);
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

        return new FloatSample(delayedsample,numChannels);
    }


    private void play(FloatSample samples) throws IOException {
        Synthesizer synth = JSyn.createSynthesizer();


        try {
            samples = volume_percentagte(samples, 2*(float)slider6.getValue()/100);

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

    private void getFile() throws IOException {
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
    private void loadFile(){
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
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton b=new JButton("button1");
        frame.pack();
        frame.setVisible(true);
    }


}



