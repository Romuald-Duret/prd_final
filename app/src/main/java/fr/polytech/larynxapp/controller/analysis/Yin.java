package fr.polytech.larynxapp.controller.analysis;

import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchDetector;

/**
 * Adapting Yin algorithm for TarsosDSP, override to change threshold
 */
public final class Yin implements PitchDetector {
    /**
     * The default YIN threshold value. Should be around 0.10~0.15. See YIN
     * paper for more information.
     */
    private static final double DEFAULT_THRESHOLD = 0.15;

    /**
     * The default size of an audio buffer (in samples).
     */
    public static final int DEFAULT_BUFFER_SIZE = 2048;

    /**
     * The default overlap of two consecutive audio buffers (in samples).
     */
    public static final int DEFAULT_OVERLAP = 1536;

    /**
     * The actual YIN threshold.
     */
    private final double threshold;

    /**
     * The audio sample rate. Most audio has a sample rate of 44.1kHz.
     */
    private final float sampleRate;

    /**
     * The buffer that stores the calculated values. It is exactly half the size
     * of the input buffer.
     */
    private final float[] yinBuffer;

    /**
     * The result of the pitch detection iteration.
     */
    private final PitchDetectionResult result;

    /**
     * Create a new pitch detector for a stream with the defined sample rate.
     * Processes the audio in blocks of the defined size.
     *
     * @param audioSampleRate
     *            The sample rate of the audio stream. E.g. 44.1 kHz.
     * @param bufferSize
     *            The size of a buffer. E.g. 1024.
     */
    public Yin(final float audioSampleRate, final int bufferSize) {
        this(audioSampleRate, bufferSize, DEFAULT_THRESHOLD);
    }

    /**
     * Create a new pitch detector for a stream with the defined sample rate.
     * Processes the audio in blocks of the defined size.
     *
     * @param audioSampleRate
     *            The sample rate of the audio stream. E.g. 44.1 kHz.
     * @param bufferSize
     *            The size of a buffer. E.g. 1024.
     * @param yinThreshold
     *            The parameter that defines which peaks are kept as possible
     *            pitch candidates. See the YIN paper for more details.
     */
    public Yin(final float audioSampleRate, final int bufferSize, final double yinThreshold) {
        this.sampleRate = audioSampleRate;
        this.threshold = yinThreshold;
        yinBuffer = new float[bufferSize / 2];
        result = new PitchDetectionResult();
    }

    /**
     * The main flow of the YIN algorithm. Returns a pitch value in Hz or -1 if
     * no pitch is detected.
     *
     * @return a pitch value in Hz or -1 if no pitch is detected.
     */
    public PitchDetectionResult getPitch(final float[] audioBuffer) {

        final int tauEstimate;
        final float pitchInHertz;

        // step 2
        difference(audioBuffer);

        // step 3
        cumulativeMeanNormalizedDifference();

        // step 4
        tauEstimate = absoluteThreshold();

        // step 5
        if (tauEstimate != -1) {
            final float betterTau = parabolicInterpolation(tauEstimate);

            // conversion to Hz
            pitchInHertz = sampleRate / betterTau;
        } else{
            // no pitch found
            pitchInHertz = -1;
        }

        result.setPitch(pitchInHertz);

        return result;
    }

    /**
     * Implements the difference function as described in step 2 of the YIN
     * paper.
     */
    private void difference(final float[] audioBuffer) {
        int index;
        int tau;
        float delta;
        for (tau = 0; tau < yinBuffer.length; tau++) {
            yinBuffer[tau] = 0;
        }
        for (tau = 1; tau < yinBuffer.length; tau++) {
            for (index = 0; index < yinBuffer.length; index++) {
                delta = audioBuffer[index] - audioBuffer[index + tau];
                yinBuffer[tau] += delta * delta;
            }
        }
    }


    /**
     * The cumulative mean normalized difference function as described in step 3
     * of the YIN paper.
     */
    private void cumulativeMeanNormalizedDifference() {
        int tau;
        yinBuffer[0] = 1;
        float runningSum = 0;
        for (tau = 1; tau < yinBuffer.length; tau++) {
            runningSum += yinBuffer[tau];
            yinBuffer[tau] *= tau / runningSum;
        }
    }

    /**
     * Implements step 4 of the AUDIO_YIN paper.
     */
    private int absoluteThreshold() {
        int tau;
        // first two positions in yinBuffer are always 1
        // So start at the third (index 2)
        for (tau = 2; tau < yinBuffer.length; tau++) {
            if (yinBuffer[tau] < threshold) {
                while (tau + 1 < yinBuffer.length && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    tau++;
                }
                // found tau, exit loop and return
                // store the probability
                // From the YIN paper: The threshold determines the list of
                // candidates admitted to the set, and can be interpreted as the
                // proportion of aperiodic power tolerated
                // within a periodic signal.
                //
                // Since we want the periodicity and and not aperiodicity:
                // periodicity = 1 - aperiodicity
                result.setProbability(1 - yinBuffer[tau]);
                break;
            }
        }


        // if no pitch found, tau => -1
        if (tau == yinBuffer.length || yinBuffer[tau] >= threshold) {
            tau = -1;
            result.setProbability(0);
            result.setPitched(false);
        } else {
            result.setPitched(true);
        }

        return tau;
    }

    /**
     * Implements step 5 of the AUDIO_YIN paper. It refines the estimated tau
     * value using parabolic interpolation. This is needed to detect higher
     * frequencies more precisely.
     *
     * @param tauEstimate The estimated tau value.
     * @return A better, more precise tau value.
     */
    private float parabolicInterpolation(final int tauEstimate) {
        final float betterTau;
        final int x0;
        final int x2;

        if (tauEstimate < 1) {
            x0 = tauEstimate;
        } else {
            x0 = tauEstimate - 1;
        }
        if (tauEstimate + 1 < yinBuffer.length) {
            x2 = tauEstimate + 1;
        } else {
            x2 = tauEstimate;
        }
        if (x0 == tauEstimate) {
            if (yinBuffer[tauEstimate] <= yinBuffer[x2]) {
                betterTau = tauEstimate;
            } else {
                betterTau = x2;
            }
        } else if (x2 == tauEstimate) {
            if (yinBuffer[tauEstimate] <= yinBuffer[x0]) {
                betterTau = tauEstimate;
            } else {
                betterTau = x0;
            }
        } else {
            float s0;
            float s1;
            float s2;
            s0 = yinBuffer[x0];
            s1 = yinBuffer[tauEstimate];
            s2 = yinBuffer[x2];
            betterTau = tauEstimate + (s2 - s0) / (2 * (2 * s1 - s2 - s0));
        }
        return betterTau;
    }
}