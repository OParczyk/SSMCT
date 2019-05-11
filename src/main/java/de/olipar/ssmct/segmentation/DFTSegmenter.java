package de.olipar.ssmct.segmentation;

import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;

import de.olipar.ssmct.annotation.Param;
import de.olipar.ssmct.annotation.ParameterDisplayType;
import de.olipar.ssmct.annotation.ParameterType;

public class DFTSegmenter implements Segmenter {

	private enum SegmentType {
		FREQENCY_LIST, CHROMA_LIST;
	}

	private int sampleRate;
	private int numberOfSamples;
	private SegmentType segmentType = SegmentType.FREQENCY_LIST;

	public DFTSegmenter(
			@Param(displayType = ParameterDisplayType.NUMBER, type = ParameterType.INT, min = 1, max = Integer.MAX_VALUE, name = "Samples per second") Integer sampleRate,
			@Param(displayType = ParameterDisplayType.NUMBER, type = ParameterType.INT, min = 1, max = Integer.MAX_VALUE, name = "Number of samples per transform, should be even") Integer numberOfSamples) {
		this.sampleRate = sampleRate.intValue();
		this.numberOfSamples = numberOfSamples.intValue();
	}

	@Override
	public Comparable[][] getSegments(byte[] input) {
		Double[][] segments;
		byte[] currentSamples;
		switch (segmentType) {
		case FREQENCY_LIST:
			segments = new Double[(int) Math.ceil(sampleRate * 1.0 / numberOfSamples)][(int) numberOfSamples / 2];
			for (int i = 0; i < segments.length; i++) {
				if (input.length < (i + 1) * numberOfSamples)
					currentSamples = Arrays.copyOfRange(input, i * numberOfSamples, (i + 1) * numberOfSamples);
				else
					currentSamples = Arrays.copyOfRange(input, i * numberOfSamples, input.length);
				segments[i]=abs(dft(currentSamples));
			}
			return segments;

		case CHROMA_LIST:
			segments = new Double[(int) Math.ceil(sampleRate * 1.0 / numberOfSamples)][12];
			return segments;
		}
		return null;
	}

	private Complex[] dft(byte[] input) {
		int N = input.length;
		Complex[] ret = new Complex[(int) Math.floor(N / 2)];
		Complex sum;
		for (int n = 0; n < (int) Math.floor(N / 2); n++) {
			sum = Complex.ZERO;
			for (int k = 0; k < N; k++) {
				Complex exponent = Complex.I.multiply(-2 * Math.PI * n * k / N);
				sum.add(exponent.exp());
			}
			ret[n] = sum;
		}

		return ret;
	}

	private Double[] abs(Complex[] input) {
		Double[] ret = new Double[input.length];
		for (int i = 0; i < input.length; i++) {
			ret[i] = input[i].abs();
		}
		return ret;
	}

}
