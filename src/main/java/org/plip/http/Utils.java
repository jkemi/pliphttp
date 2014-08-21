package org.plip.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

public class Utils {

	public final static <T extends Appendable> T join(T dest, Iterable<?> s, String delimiter) throws IOException {
		final String sentinel = "<null>";
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			final Object o = iter.next();
			if (o != null) {
				dest.append(o.toString());
			} else {
				dest.append(sentinel);
			}
			if (iter.hasNext()) {
				dest.append(delimiter);
			}
		}

		return dest;
	}

	public final static StringBuilder join(StringBuilder dest, Iterable<?> s, String delimiter) {
		try {
			join((Appendable)dest, s, delimiter);
		} catch (IOException ex) {
			// Can't happen for StringBuilder
		}
		return dest;
	}

	public final static String join(Iterable<?> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		join(builder, s, delimiter);
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public final static <T extends Appendable> T format(T dest, String format, Object ...args) throws IOException {
		return (T)dest.append(String.format(format, args));
	}

	public final static <T> Iterator<T> iteratorEnumeration(final Enumeration<T> enumeration) {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public T next() {
				return enumeration.nextElement();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};

	}

	public final static <T> Iterable<T> iterableEnumeration(final Enumeration<T> enumeration) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return iteratorEnumeration(enumeration);
			}

		};
	}

	// same as String.indexOf(ch, fromIndex)
	public final static int indexOf(CharSequence s, int ch, int fromIndex) {
        final int max = s.length();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }

        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            for (int i = fromIndex; i < max; i++) {
                if (s.charAt(i) == ch) {
                    return i;
                }
            }
            return -1;
        } else {
            return indexOfSupplementary(s, ch, fromIndex);
        }
	}

	public static Iterator<CharSequence> splitTrimIterator(final CharSequence s, final int ch) {

		return new Iterator<CharSequence>() {
			int cur = 0;
			final int max = s.length();

			@Override
			public boolean hasNext() {
				return cur < max;
			}

			@Override
			public CharSequence next() {

				int start = cur;
				int end;

				int comma = indexOf(s, ch, cur);
				if (comma >= 0) {
					end = comma;
					cur = comma+1;
				} else {
					end = max;
					cur = max;
				}

		        while ((start < end) && (s.charAt(start) <= ' ')) {
		            start++;
		        }
		        while ((end > start) && (s.charAt(end - 1) <= ' ')) {
		            end--;
		        }

		        return s.subSequence(start, end);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

	}

	public static Iterable<CharSequence> splitTrim(final CharSequence s, final int ch) {
		return new Iterable<CharSequence>() {

			@Override
			public Iterator<CharSequence> iterator() {
				return splitTrimIterator(s,  ch);
			}

		};
	}

    private final static int indexOfSupplementary(CharSequence s, int ch, int fromIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char hi = Character.highSurrogate(ch);
            final char lo = Character.lowSurrogate(ch);
            final int max = s.length() - 1;
            for (int i = fromIndex; i < max; i++) {
                if (s.charAt(i) == hi && s.charAt(i + 1) == lo) {
                    return i;
                }
            }
        }
        return -1;
    }
}
