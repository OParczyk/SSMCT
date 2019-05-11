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
		float overlap=.1f;
		switch (segmentType) {
		case FREQENCY_LIST:
			segments = new Double[(int) Math.ceil(input.length * (1/overlap) / numberOfSamples)][(int) numberOfSamples / 2];
			for (int i = 0; i < segments.length; i++) {

				currentSamples = Arrays.copyOfRange(input, (int) (i * overlap * numberOfSamples),
						(int) ((i * overlap + 1) * numberOfSamples));
				segments[i] = abs(dft(currentSamples));
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
		Complex exponent;
		for (int n = 0; n < (int) Math.floor(N / 2); n++) {
			sum = Complex.ZERO;
			for (int k = 0; k < N; k++) {
				exponent = Complex.I.multiply(-2.0D * Math.PI * n * k / N);
				sum = sum.add(exponent.exp().multiply((input[k] - 127) / 127.0D));
			}
			ret[n] = sum;
		}

		return ret;
	}

	private Double[] abs(Complex[] input) {
		Double[] ret = new Double[input.length];
		for (int i = 0; i < input.length; i++) {
			ret[i] = Math.log10(input[i].abs() * 2.0D)*20.0;
		}
		return ret;
	}

}
