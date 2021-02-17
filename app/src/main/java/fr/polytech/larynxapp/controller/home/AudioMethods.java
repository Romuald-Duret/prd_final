package fr.polytech.larynxapp.controller.home;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class AudioMethods {
    public static AudioDispatcher dispatcher;
    public float pitchInHz;
    public int millSecond;

    public void getPitchFromFile(final AssetFileDescriptor afd, final Activity activity, TarsosDSPAudioFormat tarsosDSPAudioFormat, final TextView pitchText, final TextView noteText) {
        try {
            releaseDispatcher(dispatcher);

            FileInputStream fileInputStream = new FileInputStream(afd.getFileDescriptor());
            fileInputStream.skip(afd.getStartOffset());

            // I only need this to get the number of elapsed seconds if the dispatcher doesn't detect when the audio file is finished.
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            millSecond = Integer.parseInt(durationStr);

            dispatcher = new AudioDispatcher(new UniversalAudioInputStream(fileInputStream, tarsosDSPAudioFormat), 2048, 0);
            final AudioProcessor playerProcessor = new AndroidAudioPlayer(tarsosDSPAudioFormat, 16000, 0);

            dispatcher.addAudioProcessor(playerProcessor);

            PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {

                public void handlePitch(final PitchDetectionResult res, AudioEvent e) {
                    pitchInHz  = res.getPitch();
                    //if(pitchInHz > 0){Log.d("EBB Outside Run","Pitch:" + pitchInHz);}
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(pitchInHz > 0){
                                Log.d("EBB Inside Run","Pitch:" + pitchInHz);}
                            pitchText.setText(pitchInHz + "");
                            processPitch(pitchInHz);
                        }
                    });
                }

                public void processPitch(float pitchInHz) {

                    if(pitchInHz >= 110 && pitchInHz < 123.47) {
                        //A
                        noteText.setText("A");
                    }
                    else if(pitchInHz >= 123.47 && pitchInHz < 130.81) {
                        //B
                        noteText.setText("B");
                    }
                    else if(pitchInHz >= 130.81 && pitchInHz < 146.83) {
                        //C
                        noteText.setText("C");
                    }
                    else if(pitchInHz >= 146.83 && pitchInHz < 164.81) {
                        //D
                        noteText.setText("D");
                    }
                    else if(pitchInHz >= 164.81 && pitchInHz <= 174.61) {
                        //E
                        noteText.setText("E");
                    }
                    else if(pitchInHz >= 174.61 && pitchInHz < 185) {
                        //F
                        noteText.setText("F");
                    }
                    else if(pitchInHz >= 185 && pitchInHz < 196) {
                        //G
                        noteText.setText("G");
                    }
                }
            };

            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 44100, 2048, pitchDetectionHandler);
            dispatcher.addAudioProcessor(pitchProcessor);
            dispatcher.run();

            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void releaseDispatcher(AudioDispatcher dispatcher)
    {
        if(dispatcher != null)
        {
            if(!dispatcher.isStopped())
                dispatcher.stop();

            dispatcher = null;
        }
    }

    protected void onStop(AudioDispatcher dispatcher) {
        //super.onStop();
        releaseDispatcher(dispatcher);
    }

    //I don't need these guys yet
 /*public void stopRecording()
{
    releaseDispatcher();
}


@Override
protected void onStop() {
    super.onStop();
    releaseDispatcher();
}*/

}
