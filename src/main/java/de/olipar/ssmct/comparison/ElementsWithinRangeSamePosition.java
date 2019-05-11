package de.olipar.ssmct.comparison;

import java.util.Comparator;

import de.olipar.ssmct.annotation.Param;
import de.olipar.ssmct.annotation.ParameterDisplayType;
import de.olipar.ssmct.annotation.ParameterType;

public class ElementsWithinRangeSamePosition implements Comparator<Comparable<? extends Number>[]> {
	private final byte delta;

	public ElementsWithinRangeSamePosition(
			@Param(displayType = ParameterDisplayType.NUMBER, name = "Delta", type = ParameterType.BYTE, min = 0, max = Byte.MAX_VALUE) byte delta) {
		if (delta < 0)
			throw new IllegalArgumentException("Delta must not be negative!");
		this.delta = delta;
	}

	@Override
	public int compare(Comparable<? extends Number>[] o1, Comparable<? extends Number>[] o2) {
		int ret = 0;
		for (int i = 0; i < Math.min(o1.length, o2.length); i++) {
			if (Math.abs((double) o1[i] - (double) o2[i]) <= delta)
				ret++;
		}
		return ret;
	}

}
