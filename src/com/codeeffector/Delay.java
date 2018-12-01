//package com.codeeffector;
//
//public class Delay {
//    public double dryWetMixPercent = 10.0;
//    public double feedbackPercent = 30;
//
//    private short processSample(short inputSample) {
//
//        if (bypassed) {
//            return inputSample;
//        }
//        // Read a delayed sample
//        short delayedSample = delayBuffer[readIndex++];
//
//        double dryLevel = ((100.0 - dryWetMixPercent) * inputSample) / 100.0;
//        double wetLevel = (dryWetMixPercent * delayedSample) / 100.0;
//        short outputSample = (short) (dryLevel + wetLevel);
//
//        inputSample += (delayedSample * feedbackPercent) / 100.0;
//
//        // Write an input sample
////        delayBuffer[writeIndex++] = inputSample;
//
//        // Update indices
//  //      readIndex  %= DELAY_BUFFER_SIZE;
//   //     writeIndex %= DELAY_BUFFER_SIZE;
//
//        return outputSample;
//    }
//
//    /**
//     * Process a buffer full of samples pulled from the sample provider
//     *
//     * @param buffer Buffer in which the samples are to be processed
//     *
//     * @return Count of number of bytes processed
//     */
//    public int getSamples(byte [] buffer) {
//
//        // Grab samples to manipulate from this modules sample provider
//        provider.getSamples(buffer);
//
//        int index = 0;
//        for (int i = 0; i < SamplePlayer.SAMPLES_PER_BUFFER; i++) {
//            // Get a sample to process
//            byte b2 = buffer[index];
//            byte b1 = buffer[index+1];
//
//            // Convert bytes into short sample
//            short s = (short)((((int) b2) << 8) + b1);
//
//            // Process the sample
//            s = processSample(s);
//
//            // Store the processed sample
//            buffer[index++] = (byte)(s >> 8);
//            buffer[index++] = (byte)(s & 0xFF);
//        }
//        return SamplePlayer.BUFFER_SIZE;
//    }
//
//}
