package com.mazhangjing.lab.sound;

import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.NlsClient;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizer;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizerListener;
import com.alibaba.nls.client.protocol.asr.SpeechRecognizerResponse;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * SpeechTranscriberDemo class
 *
 * 实时音频流识别Demo
 */
public class SpeechTranscriberDemo {
    private String appKey;
    private String accessToken;
    private NlsClient client;
    private SpeechTranscriberDemo(String appKey, String token) {
        this.appKey = appKey;
        this.accessToken = token;
        // Step0 创建NlsClient实例,应用全局创建一个即可,默认服务地址为阿里云线上服务地址
        client = new NlsClient(accessToken);
    }
    private static SpeechRecognizerListener getTranscriberListener() {
        return new SpeechRecognizerListener() {
            // 识别出中间结果.服务端识别出一个字或词时会返回此消息.仅当setEnableIntermediateResult(true)时,才会有此类消息返回
            @Override
            public void onRecognitionCompleted(SpeechRecognizerResponse response) {
                // 事件名称 RecognitionCompleted
                System.out.println("name: " + response.getName() +
                        // 状态码 20000000 表示识别成功
                        ", status: " + response.getStatus() +
                        // 一句话识别的完整结果
                        ", result: " + response.getRecognizedText());
            }
            @Override
            public void onRecognitionResultChanged(SpeechRecognizerResponse response) {
                // 事件名称 RecognitionResultChanged
                System.out.println("name: " + response.getName() +
                        // 状态码 20000000 表示识别成功
                        ", status: " + response.getStatus() +
                        ", result: " + response.getLexicalText() +
                        // 一句话识别的中间结果
                        ", result: " + response.getRecognizedText());
            }
        };
    }
    private void process(InputStream ins) {
        SpeechRecognizer recognizer = null;
        try {
            // Step1 创建实例,建立连接
            recognizer = new SpeechRecognizer(client, getTranscriberListener());
            recognizer.setAppKey(appKey);
            // 设置音频编码格式
            recognizer.setFormat(InputFormatEnum.PCM);
            // 设置音频采样率
            recognizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
            // 设置是否返回中间识别结果
            recognizer.setEnableIntermediateResult(true);
            // Step2 此方法将以上参数设置序列化为json发送给服务端,并等待服务端确认
            recognizer.start();
            // Step3 语音数据来自声音文件用此方法,控制发送速率;若语音来自实时录音,不需控制发送速率直接调用 recognizer.send(ins)即可
            recognizer.send(ins);
            // Step4 通知服务端语音数据发送完毕,等待服务端处理完成
            recognizer.stop();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            // Step5 关闭连接
            if (null != recognizer) {
                recognizer.close();
            }
        }
    }
    public void shutdown() {
        client.shutdown();
    }

    public static void main(String[] args) throws LineUnavailableException, IOException {
        //runTestWithSepStream();
        runTestWithFreStream();
    }

    private static void runTestWithSepStream() throws LineUnavailableException, IOException {
        String appKey = "PZiaqW1Pnyw4VQE4";
        String accessToken = "f6860ddbd72643a29bce548e63b79a4a";
        SpeechTranscriberDemo demo = new SpeechTranscriberDemo(appKey, accessToken);
        AudioFormat format = new AudioFormat(16 * 1000, 16, 1, true, false);
        TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(format);
        targetDataLine.open();
        targetDataLine.start();

        while (true) {
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Set StopMark to true");
                stopMark = true;
            }).start();
            ByteArrayOutputStream stream = doWithLine(targetDataLine);
            byte[] bytes = stream.toByteArray();
            System.out.println("After get Line");
            /*AudioInputStream audioInputstream = new AudioInputStream(new ByteArrayInputStream(bytes),
            format, bytes.length/format.getFrameSize());
            new SimpleAudioFunctionMakerToneUtilsImpl().playSound(audioInputstream);*/
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            demo.process(bi);
            bi.close();
        }
        //demo.shutdown();
    }

    private static void runTestWithFreStream() throws LineUnavailableException, IOException {
        String appKey = "PZiaqW1Pnyw4VQE4";
        String accessToken = "f6860ddbd72643a29bce548e63b79a4a";
        SpeechTranscriberDemo demo = new SpeechTranscriberDemo(appKey, accessToken);
        AudioFormat format = new AudioFormat(16 * 1000, 16, 1, true, false);
        TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(format);
        targetDataLine.open();
        targetDataLine.start();

        AudioInputStream audioInputstream = new AudioInputStream(targetDataLine);

        System.out.println("After get Line");
        demo.process(audioInputstream);
    }


    private static Boolean stopMark = false;

    private static ByteArrayOutputStream doWithLine(TargetDataLine targetDataLine) throws IOException {
        System.out.println("Start Record....");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[10000];
        stopMark = false;
        int c;
        while (!stopMark) {
            c = targetDataLine.read(buffer, 0, buffer.length);
            if (c > 0) {
                os.write(buffer, 0, c);
            }
        }
        os.close();
        System.out.println("Stop Record...");
        return os;
    }
}