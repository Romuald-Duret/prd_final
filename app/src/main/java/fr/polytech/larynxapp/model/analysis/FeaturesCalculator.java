package fr.polytech.larynxapp.model.analysis;

import android.content.Context;
import android.renderscript.ScriptIntrinsicYuvToRGB;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import fr.polytech.larynxapp.MainActivity;
import fr.polytech.larynxapp.model.audio.AudioData;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * The class calculating fundamentalFreq (fundamental frequency), Jitter and Shimmer of the voice
 */
public class FeaturesCalculator {

	/**
	 * The ending of the area where the next period is to be searched.
	 */
	private int nextPeriodSearchingAreaEnd = (int) hzToPeriod( 40 );

	/**
	 * The list of the position of the maximum of the periods.
	 */
	private List<Integer> pitchPositions;

	/**
	 * The list of the periods' length.
	 */
	private List<Integer> periods;

	/**
	 * The data to analyse.
	 */
	private List<Short> data;

	/**
	 * The fundamental frequency of the data.
	 */
	private float fundamentalFreq;

	/**
	 * The list of pitches (voice frequencies)
	 */
	private List<Float> pitches;

	private Context context;

	/**
	 * FeaturesCalculator sole builder.
	 *
	 * @param audioData the audio data containing the data to analyse.
	 */
	public FeaturesCalculator(AudioData audioData, List<Float> pitches, Context context) {
		this.data = audioData.getData_processed();
		this.pitches = pitches;
		this.context = context;
		periods = new ArrayList<>();
		pitchPositions = new ArrayList<>();
		fundamentalFreq = 0f;
		initPeriodsSearch();
		searchPitchPositions();
	}

	/**
	 * The method initializing the research of the periods.
	 *
	 * Finds the beginning and the ending of the area where the first period is to be searched.
	 * Filters the data into the dataFiltered list.
	 */
	private void initPeriodsSearch() {

		//init fundamentalFreq
		calculatefundamentalFreq();

		//set the first search area
		final double confidenceLevel = 5 / 100.;

		nextPeriodSearchingAreaEnd = (int) Math.floor( hzToPeriod( fundamentalFreq ) * ( 1 + confidenceLevel ) );
	}

	/**
	 * The method calculating the periods in the data and searching the pitches positions.
	 *
	 * Fills the pitchPositions and the periods lists.
	 */
	private void searchPitchPositions() {

		for(float pitch : pitches)
			periods.add((int) hzToPeriod(pitch));

		int periodMaxPitch;
		int periodMaxPitchIndex = 0;
		int periodBeginning     = 0;

		//search each periods maxima
		for ( int period = 0; period < periods.size() - 1; period++ ) {
			periodMaxPitch = 0;

			//search a maximum
			for ( int i = periodBeginning; i < periodBeginning + periods.get( period ); i++ ) {
				if(i < data.size()){
					if ( periodMaxPitch < data.get( i ) ) {
						periodMaxPitch = data.get( i );
						periodMaxPitchIndex = i;
					}
				}

			}

			periodBeginning += periods.get( period );
			pitchPositions.add( periodMaxPitchIndex );
		}

	}

	/**
	 * Returns the period length of the given value in Hz considering the sample rate (44.1kHz).
	 *
	 * @param hz the value in Hz
	 * @return the equivalent period length
	 */
	private float hzToPeriod(float hz ) {
		int sampling = 44100;
		return sampling / hz;
	}

	// FEATURE NUMBER 1 : SHIMMER

	/**
	 * The method calculating the Shimmer (corresponds to dda in Praat)
	 *
	 * @return the Shimmer
	 */
	public double getShimmer() {

		int           minAmp     = 0;
		int           maxAmp;
		double          amplitudeDiffSum = 0; // sum of difference between every two peak-to-peak amplitudes
		double          amplitudeSum      = 0; // sum of all the peak-to-peak amplitudes
		double  amplitude = 0;
		List<Double> amplitudes = new ArrayList<Double>();
		List<Integer> ampPk2Pk   = new ArrayList<>(); // this list contains all the peak-to-peak amplitudes
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
//			System.out.println(maxAmp);
//			System.out.println(minAmp);
//			System.out.println(Math.log(Math.abs(maxAmp)/Math.abs(minAmp)));
//			double element = Math.abs(20 * Math.log(Math.abs(maxAmp)/Math.abs(minAmp)));
//			System.out.println(element);
//			elements.add(element);

			// add peak-to-peak amplitude into the list
			//ampPk2Pk.add( maxAmp - minAmp );
			// reset the min amplitude
			//minAmp = 0;
		}

		for(int j = 0; j < amplitudes.size() - 1; j++){
//			System.out.println("amplitudes.get(j+1)");
//			System.out.println(amplitudes.get(j+1));
//			System.out.println("amplitudes.get(j)");
//			System.out.println(amplitudes.get(j));
//			double test1 = (amplitudes.get(j+1))/(amplitudes.get(j));
//			System.out.println("amplitudes.get(j+1)/amplitudes.get(j)");
//			System.out.println((amplitudes.get(j+1))/(amplitudes.get(j)));
//
//			System.out.println("Math.log(10)");
//			System.out.println(Math.log(10));
//
//
//			double test = Math.log(amplitudes.get(j+1)/amplitudes.get(j))/Math.log(10);
//			System.out.println(test);
			double element = Math.abs(20*(Math.log(amplitudes.get(j+1)/amplitudes.get(j))/Math.log(10)));
//			System.out.println(element);
			sum = sum + element;
		}

		double result1 = sum/amplitudes.size();

		return result1;

//		// SHIMMER FORMULA (RELATIVE)
//		for ( int i = 0; i < ampPk2Pk.size() - 1; i++ ) {
//			amplitudeDiffSum += Math.abs( ampPk2Pk.get( i ) - ampPk2Pk.get( i + 1 ) );
//			amplitudeSum += ampPk2Pk.get( i );
//		}
//		// add the last peak-to-peak amplitude into sum
//		if ( !ampPk2Pk.isEmpty() ) {
//			amplitudeSum += ampPk2Pk.get( ampPk2Pk.size() - 1 );
//		}
//		// calculate shimmer (relative)
//		double result = ( (double) amplitudeDiffSum / (periods.size() - 1) ) / ( (double) amplitudeSum / periods.size() );
//
//		System.out.println("shimmer");
//		System.out.println(result);
//		return result;


	}

	// FEATURE NUMBER 2 : JITTER

	/**
	 * The method calculating the Jitter (corresponds to ddp in Praat)
	 *
	 * @return the Jitter
	 */
	public double getJitter() {
		double sumOfDifferenceOfPeriods = 0.0;        // sum of difference between every two periods
		double sumOfPeriods             = 0.0;        // sum of all periods
		double numberOfPeriods          = periods.size();   //set as double for double division

		// JITTER FORMULA (RELATIVE)
		for ( int i = 0; i < periods.size() - 1; i++ ) {
			sumOfDifferenceOfPeriods += Math.abs( periods.get( i ) - periods.get( i + 1 ) );
			sumOfPeriods += periods.get( i );
		}

		// add the last period into sum
		if ( !periods.isEmpty() ) {
			sumOfPeriods += periods.get( periods.size() - 1 );
		}

		double meanPeriod = sumOfPeriods / numberOfPeriods;

		// calculate jitter (relative)
		return ( sumOfDifferenceOfPeriods / ( numberOfPeriods - 1 ) ) / meanPeriod;
	}

	// FEATURE NUMBER 3 : FUNDAMENTAL FREQUENCY

	/**
	 * Getter for the fundamental frequency
	 *
	 * @return the fundamental frequency
	 */
	public double getfundamentalFreq() {
		if ( fundamentalFreq == 0f )
			calculatefundamentalFreq();

		return fundamentalFreq;
	}

	/**
	 * The method finding the fundamental frequency of the data.
	 *
	 * To increase efficiency, this method only test the frequencies between 40Hz to 400Hz.
	 */
	private void calculatefundamentalFreq() {
		//System.out.println("1111111111111111");
		int count;
		float f0 = 0;
		System.out.println("pitches");
		System.out.println(pitches);
		//System.out.println(222222222);
		for(count = 0; count < pitches.size(); count++)
		{
			f0 += pitches.get(count);
		}
		if(count != 0)
			fundamentalFreq =  f0 / count;
		else
			fundamentalFreq = 0;
	}
}
