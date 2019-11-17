package com.mazhangjing.lab.sound;

import org.junit.Test;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AliVoiceRecognizerTest {

    @Test
    public void doRecognition() throws InterruptedException {
        String appKey = "PZiaqW1Pnyw4VQE4";
        String accessToken = "9330442d5eab4a57a0a18cbe66b21ca4";
        AliVoiceRecognizer aliVoiceRecognizer = new AliVoiceRecognizer(appKey, accessToken);
        new Thread(() -> {
            try {
                aliVoiceRecognizer.doRecognition(2);
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }).start();
        TimeUnit.SECONDS.sleep(10);
        aliVoiceRecognizer.requestShutdown();
    }
}