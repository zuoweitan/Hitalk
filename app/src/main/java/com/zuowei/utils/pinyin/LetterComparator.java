package com.zuowei.utils.pinyin;


import com.vivifram.second.hitalk.bean.address.LetterMark;

import java.util.Comparator;

public class LetterComparator<T extends LetterMark> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}
}
