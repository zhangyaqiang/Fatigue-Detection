package com.martin.ads.easymediacodec;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Ads on 2017/6/1.
 * 祝大家儿童节快乐
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AudioEncoderCore extends EncoderCore {
    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;	// 44.1[KHz] is only setting guaranteed to be available on all devices.
    private static final int BIT_RATE = 64000;
    public static final int SAMPLES_PER_FRAME = 1024;	// AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25; 	// AAC, frame/buffer/sec
    private AudioThread mAudioThread = null;
    private boolean capturing;
    private boolean stopped;
    protected final Object mSync = new Object();

    public AudioEncoderCore(MMediaMuxer MMediaMuxer) throws IOException {
        super(MMediaMuxer);
        final MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
//		audioFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, inputFile.length());
//      audioFormat.setLong(MediaFormat.KEY_DURATION, (long)durationInMs );
        if (VERBOSE) Log.i(TAG, "format: " + audioFormat);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mEncoder.start();
        if (mAudioThread == null) {
            mAudioThread = new AudioThread();
            mAudioThread.start();
            capturing=true;
            stopped=false;
        }
    }

    @Override
    public void release() {
        super.release();
        mAudioThread = null;
    }

    private static final int[] AUDIO_SOURCES = new int[] {
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
    };
    /**
     * Thread to capture audio data from internal mic as uncompressed 16bit PCM data
     * and write them to the MediaCodec encoder
     */
    private class AudioThread extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            try {
                final int minBufferSize = AudioRecord.getMinBufferSize(
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                int bufferSize = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
                if (bufferSize < minBufferSize)
                    bufferSize = ((minBufferSize / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;

                AudioRecord audioRecord = null;
                for (final int source : AUDIO_SOURCES) {
                    try {
                        audioRecord = new AudioRecord(
                                source, SAMPLE_RATE,
                                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
                        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
                            audioRecord = null;
                    } catch (final Exception e) {
                        audioRecord = null;
                    }
                    if (audioRecord != null) break;
                }
                if (audioRecord != null) {
                    try {
                        if (capturing) {
                            if (VERBOSE) Log.v(TAG, "AudioThread:start audio recording");
                            final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                            int readBytes;
                            audioRecord.startRecording();
                            drainEncoder(false);
                            try {
                                while(capturing) {
                                    // read audio data from internal mic
                                    buf.clear();
                                    readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                                    if (readBytes > 0) {
                                        // set audio data to encoder
                                        buf.position(readBytes);
                                        buf.flip();
                                        while(!mMuxerStarted) {
                                            Thread.sleep(5);
                                        }
                                        synchronized (mSync){
                                            Log.d("EasyMediacodec", "run: "+capturing);
                                            if(capturing){
                                                encode(buf, readBytes, System.nanoTime() / 1000L);
                                                drainEncoder(false);
                                            }
                                            else{
                                                encode(null, 0, System.nanoTime() / 1000L);
                                                drainEncoder(false);
                                                stopped=true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            } finally {
                                audioRecord.stop();
                            }
                        }
                    } finally {
                        audioRecord.release();
                    }
                } else {
                    Log.e(TAG, "failed to initialize AudioRecord");
                }
            } catch (final Exception e) {
                Log.e(TAG, "AudioThread#run", e);
            }
            if (VERBOSE) Log.v(TAG, "AudioThread:finished");
        }
    }

    void stopAudioThread(){
        Log.d("EasyMediacodec", "stopAudioThread: -1");
        synchronized (mSync){
            capturing=false;
            Log.d("EasyMediacodec", "stopAudioThread: -2 "+capturing);
        }
    }

    public boolean isStopped() {
        return stopped;
    }
}
