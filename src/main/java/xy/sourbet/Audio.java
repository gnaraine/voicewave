package main.java.xy.sourbet;

import javax.sound.sampled.*;
import java.io.IOException;

public class Audio {
    private static Audio instance;

    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    //audio
    AudioFormat audioFormat = new AudioFormat(16000, 8, 2, true, true);
    int sampleSize = 1024;
    float[] samples = new float[sampleSize * audioFormat.getChannels()];

    int movingAveragePass = 100;
    boolean movingAveragePassA = true;
    boolean movingAveragePassB = true;

    void openMic() throws LineUnavailableException, IOException {

        TargetDataLine line;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        line = (TargetDataLine) AudioSystem.getLine(info);
        AudioInputStream audioInputStream = new AudioInputStream(line);
        line.open(audioFormat);
        line.start();
        byte[] audioBytes = new byte[samples.length];
        play_loop:
        do {
            if ((audioInputStream.read(audioBytes)) == -1) {
                line.close();
                break play_loop;
            }
            samples = byteToFloat(audioBytes, samples);
            movingAverage();
            samples = window(samples);
        } while (true);
    }

    public float[] byteToFloat(byte[] bytes, float[] samples) {
        for (int i = 0; i < bytes.length; i++) {
            samples[i] = bytes[i];
        }
        return samples;
    }

    public void movingAverage() {
        float averageA = 0;
        float averageB = 0;
        for (int i = 0; i < movingAveragePass; i++) {
            for (int a = 0; a < samples.length; a = a + 2) {
                if (movingAveragePassA) {
                    averageA = (samples[a] + averageA) / 2;
                    samples[a] = averageA;
                }
                if (movingAveragePassB) {
                    averageB = (samples[a + 1] + averageB) / 2;
                    samples[a + 1] = averageB;
                }
            }
        }
    }

    public float[] window(float[] samples) {
        int channels = audioFormat.getChannels();
        int singleChannelSampleLength = sampleSize / channels;
        for (int ch = 0, k, i; ch < channels; ch++) {
            for (i = ch, k = 0; i < singleChannelSampleLength; i += channels) {
                samples[i] *= Math.sin(Math.PI * k++ / (singleChannelSampleLength - 1));
            }
        }
        return samples;
    }
}
