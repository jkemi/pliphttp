package org.plip.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;


public class HTTPUtils {

	public static Charset UTF8 = StandardCharsets.UTF_8;
	public static String MIME_JSON = "application/json";

	private static class QEntry implements Comparable<QEntry> {
		final CharSequence field;
		final double q;

		QEntry(CharSequence field, double q) {
			this.field = field;
			this.q = q;
		}

		@Override
		public int compareTo(QEntry o) {
			return Double.compare(q, o.q);
		}
	}

	private static Iterable<CharSequence> getSortedQIterable(CharSequence header) {

		ArrayList<QEntry> res = new ArrayList<QEntry>();

		for (CharSequence sub : Utils.splitTrim(header, ',')) {
			CharSequence type = null;
			double q = 1.0;
			for (CharSequence field : Utils.splitTrim(sub, ';')) {
				if (type == null) {
					type = field;
				} else {
					if (field.charAt(0)=='q' && field.charAt(1)=='=') {
						String f = field.toString().substring(2);
						try {
							q = Double.parseDouble(f);
						} catch (NumberFormatException e) {
						}
					}
				}
			}

			res.add(new QEntry(type, q));
		}

		Collections.sort(res);
		ArrayList<CharSequence> ret = new ArrayList<CharSequence>(res.size());
		for (QEntry e : res) {
			ret.add(e.field);
		}

		return ret;
	}


	public static Iterable<CharSequence> getQIterable(CharSequence header) {

		ArrayList<CharSequence> res = new ArrayList<CharSequence>();
		double lastq = 1.0;

		for (CharSequence sub : Utils.splitTrim(header, ',')) {
			CharSequence type = null;
			double q = 1.0;
			for (CharSequence field : Utils.splitTrim(sub, ';')) {
				if (type == null) {
					type = field;
				} else {
					if (field.charAt(0)=='q' && field.charAt(1)=='=') {
						String f = field.toString().substring(2);
						try {
							q = Double.parseDouble(f);
						} catch (NumberFormatException e) {
						}
					}
				}
			}

			if (q <= lastq) {
				res.add(type);
			} else {
				return getSortedQIterable(header);
			}
		}

		return res;
	}

	public static SortedSet<String> getQSet(Iterable<CharSequence> iter) {
		SortedSet<String> ret = new TreeSet<String>();

		for (CharSequence cs : iter) {
			ret.add(cs.toString());
		}

		return ret;
	}

	public static SortedSet<String> getQSet(CharSequence header) {
		return getQSet(getQIterable(header));
	}

}
