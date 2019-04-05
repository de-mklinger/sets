/*
 * Copyright mklinger GmbH - https://www.mklinger.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mklinger.micro.sets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for Sets.
 *
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class Sets {
	private static final int MIN_INITIAL_CAPACITY = 4;

	/** No instantiation */
	private Sets() {
	}

	/**
	 * Returns a HashSet with a capacity that is sufficient to keep the
	 * underlying map from being resized as long as it grows no larger than
	 * expectedSize with the default load factor (0.75).
	 */
	public static <T> HashSet<T> newHashSet(final int expectedSize) {
		// See code in java.util.HashSet.HashSet(Collection<? extends E>)
		return new HashSet<>(Math.max((int) (expectedSize / .75f) + 1, MIN_INITIAL_CAPACITY));
	}

	/**
	 * Returns a HashSet containing the given elements with a capacity that is
	 * sufficient to keep the underlying map from being resized for the given
	 * elements with the default load factor (0.75).
	 */
	@SafeVarargs
	public static <T> HashSet<T> newHashSet(final T... elements) {
		final HashSet<T> set = newHashSet(elements.length);
		Collections.addAll(set, elements);
		return set;
	}

	/**
	 * Create a new immutable Set containing all entries of the given original set.
	 * @param original The original set
	 * @return A new immutable set
	 */
	public static <T> Set<T> newImmutableSet(final Collection<T> original) {
		if (original == null || original.isEmpty()) {
			return Collections.emptySet();
		}
		if (original.size() == 1) {
			return Collections.singleton(original.iterator().next());
		}
		return Collections.unmodifiableSet(new HashSet<>(original));
	}
}
