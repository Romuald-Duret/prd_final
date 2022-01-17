package fr.polytech.larynxapp.controller.analysis;

import android.content.Context;
import android.util.Log;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.github.mikephil.charting.utils.Utils;
import fr.polytech.larynxapp.model.audio.AudioData;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FeaturesCalculatorv2 {
    private static List<Integer> pitchPositions;
    private final int BASE_FRAGMENT;
    private final int OFFSET;
    private List<Float> T;
    private Context context;
    private List<Short> data;
    private float fundamentalFreq;
    private int nextPeriodSearchingAreaEnd;
    private List<Integer> periods;
    private List<Float> pitches;

    private float hzToPeriod(float f) {
        return ((float) 44100) / f;
    }

    public FeaturesCalculatorv2(AudioData audioData, List<Float> list) {
        this.nextPeriodSearchingAreaEnd = (int) hzToPeriod(40.0f);
        this.fundamentalFreq = 0.0f;
        this.BASE_FRAGMENT = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.OFFSET = 100;
        this.T = new ArrayList();
        this.data = audioData.getData_processed();
        this.pitches = list;
        this.periods = new ArrayList();
        pitchPositions = new ArrayList();
        this.fundamentalFreq = 0.0f;
        initPeriodsSearch();
        searchPitchPositions();
    }

    public FeaturesCalculatorv2(AudioData audioData) {
        this.nextPeriodSearchingAreaEnd = (int) hzToPeriod(40.0f);
        this.fundamentalFreq = 0.0f;
        this.BASE_FRAGMENT = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.OFFSET = 100;
        this.T = new ArrayList();
        this.data = audioData.getData_processed();
        this.pitches = new ArrayList();
        this.periods = new ArrayList();
        pitchPositions = new ArrayList();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void initPeriodsSearch() {
        calculatefundamentalFreq();
        double hzToPeriod = (double) hzToPeriod(this.fundamentalFreq);
        Double.isNaN(hzToPeriod);
        this.nextPeriodSearchingAreaEnd = (int) Math.floor(hzToPeriod * 1.05d);
    }

    public void searchPitchPositions() {
        for (Float f : this.pitches) {
            this.periods.add(Integer.valueOf((int) hzToPeriod(f.floatValue())));
        }
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.periods.size() - 1; i3++) {
            short s = 0;
            for (int i4 = i; i4 < this.periods.get(i3).intValue() + i; i4++) {
                if (i4 < this.data.size() && s < this.data.get(i4).shortValue()) {
                    s = this.data.get(i4).shortValue();
                    i2 = i4;
                }
            }
            i += this.periods.get(i3).intValue();
            pitchPositions.add(Integer.valueOf(i2));
        }
    }

    public double getShimmer() {
        int i;
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        int i3 = 0;
        while (i3 < pitchPositions.size() - 1) {
            short shortValue = this.data.get(pitchPositions.get(i3).intValue()).shortValue();
            int intValue = pitchPositions.get(i3).intValue();
            short s = 0;
            i = i3 + 1;
            if (intValue < pitchPositions.get(i).intValue()) {
                if (s > this.data.get(intValue).shortValue()) {
                    s = this.data.get(intValue).shortValue();
                }
                intValue++;
            }
            arrayList.add(Double.valueOf((double) (shortValue - s)));
            i3 = i;
        }

        double d = Utils.DOUBLE_EPSILON;
        while (i2 < arrayList.size() - 1) {
            int i4 = i2 + 1;
            d += Math.abs((Math.log(((Double) arrayList.get(i4)).doubleValue() / ((Double) arrayList.get(i2)).doubleValue()) / Math.log(10.0d)) * 20.0d);
            i2 = i4;
        }
        double size = (double) arrayList.size();
        Double.isNaN(size);
        return d / size;
    }

    public double getJitter() {
        double size = (double) this.periods.size();
        double d = Utils.DOUBLE_EPSILON;
        double d2 = 0.0d;
        int i = 0;
        while (i < this.periods.size() - 1) {
            int i2 = i + 1;
            double abs = (double) Math.abs(this.periods.get(i).intValue() - this.periods.get(i2).intValue());
            Double.isNaN(abs);
            d += abs;
            double intValue = (double) this.periods.get(i).intValue();
            Double.isNaN(intValue);
            d2 += intValue;
            i = i2;
        }
        if (!this.periods.isEmpty()) {
            List<Integer> list = this.periods;
            double intValue2 = (double) list.get(list.size() - 1).intValue();
            Double.isNaN(intValue2);
            d2 += intValue2;
        }
        Double.isNaN(size);
        double d3 = d2 / size;
        Double.isNaN(size);
        return (d / (size - 1.0d)) / d3;
    }

    public double getfundamentalFreq() {
        if (this.fundamentalFreq == 0.0f) {
            calculatefundamentalFreq();
        }
        return (double) this.fundamentalFreq;
    }

    private void calculatefundamentalFreq() {
        System.out.println("pitches");
        System.out.println(this.pitches);
        int i = 0;
        float f = 0.0f;
        while (i < this.pitches.size()) {
            f += this.pitches.get(i).floatValue();
            i++;
        }
        if (i != 0) {
            this.fundamentalFreq = f / ((float) i);
        } else {
            this.fundamentalFreq = 0.0f;
        }
    }

    private Float PeriodToPitch(float f) {
        return Float.valueOf(((float) 14700) / f);
    }

    public List<Float> calculatePitches() {
        ArrayList arrayList = new ArrayList();
        this.data.size();
        int i = 0;
        int i2 = 0;
        short s = 0;
        for (int i3 = 0; i3 < 200; i3++) {
            if (s < this.data.get(i3).shortValue()) {
                s = this.data.get(i3).shortValue();
                i2 = i3;
            }
        }
        Log.v("startPos", String.valueOf(i2));
        int i4 = i2 + 100;
        while (i2 < 1000) {
            if (this.data.get(i4).shortValue() > 0) {
                int i5 = 0;
                short s2 = 0;
                while (i4 < i2 + ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    if (s2 < this.data.get(i4).shortValue()) {
                        s2 = this.data.get(i4).shortValue();
                        i5 = i4;
                    }
                    i4++;
                }
                pitchPositions.add(Integer.valueOf(i5));
                arrayList.add(Integer.valueOf(i5));
                i4 = i5 + 100;
                i2 = i5;
            } else {
                i4++;
            }
        }
        while (i < pitchPositions.size() - 1) {
            int i6 = i + 1;
            float intValue = (float) (pitchPositions.get(i6).intValue() - pitchPositions.get(i).intValue());
            this.T.add(Float.valueOf(intValue));
            this.pitches.add(PeriodToPitch(intValue));
            i = i6;
        }
        pitchPositions.clear();
        return this.pitches;
    }

    public double getShimmerv2(){
        int           minAmp     = 0; // figures the minium of the amplitude.
        int           maxAmp; // figures the maxium of the amplitude.
        double  amplitude = 0;
        List<Double> amplitudes = new ArrayList<Double>();
        double sum = 0;
        for ( int i = 0; i < pitchPositions.size() - 1; i++ ) {
            // get each pitch
            maxAmp = data.get( pitchPositions.get( i ) );
            for ( int j = pitchPositions.get( i ); j < pitchPositions.get( i + 1 ); j++ ) {
                if ( minAmp > data.get( j ) ) {
                    minAmp = data.get( j );
                }
            }
            amplitude = maxAmp - minAmp;
            amplitudes.add(amplitude);
            minAmp = 9999;
        }
        for(int j = 0; j < amplitudes.size() - 1; j++){
            double element = Math.abs(20*(Math.log(amplitudes.get(j+1)/amplitudes.get(j))));
            sum = sum + element;
        }
        double result1 = sum/(amplitudes.size() - 1);
        return result1;
    }
}
