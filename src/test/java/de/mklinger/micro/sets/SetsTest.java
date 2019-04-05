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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class SetsTest {
	@Test
	public void initialCapacityTest() {
		final int size = 100;
		final HashSet<String> set = Sets.newHashSet(100);
		final int initialCapacity = getCapacity(set);

		for (int i = 0; i < size; i++) {
			set.add("entry-" + i);
			assertThat(getCapacity(set), is(initialCapacity));
		}
	}

	private int getCapacity(final HashSet<?> hashSet) {
		try {
			final Field field = HashSet.class.getDeclaredField("map");
			field.setAccessible(true);
			final HashMap<?, ?> map = (HashMap<?, ?>) field.get(hashSet);
			return getCapacity(map);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private int getCapacity(final HashMap<?, ?> hashMap) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Method method = HashMap.class.getDeclaredMethod("capacity");
		method.setAccessible(true);
		return (int) method.invoke(hashMap);
	}

	@Test
	public void newHashSetTest() {
		final HashSet<String> set1 = new HashSet<>();
		Collections.addAll(set1, "one", "two", "three");
		final HashSet<String> set2 = Sets.newHashSet("one", "two", "three");
		Assert.assertEquals(set1, set2);
	}

	@Test
	public void newImmutableSetTest() {
		newImmutableSetTest(100);
	}

	@Test
	public void newImmutableSetTestEmpty() {
		newImmutableSetTest(0);
	}

	@Test
	public void newImmutableSetSingleton() {
		newImmutableSetTest(1);
	}

	private void newImmutableSetTest(final int size) {
		final Set<String> src = new HashSet<>(size);
		for (int i = 0; i < size; i++) {
			src.add("entry-" + i);
		}
		final Set<String> immutable = Sets.newImmutableSet(src);
		Assert.assertEquals(src, immutable);

		assertImmutable(immutable);

		if (size > 1) {
			assertStrictImmutable(immutable);
		}
	}

	private void assertImmutable(final Set<String> immutable) {
		assertException(UnsupportedOperationException.class, () -> immutable.add("bla"));
		assertException(UnsupportedOperationException.class, () -> immutable.addAll(Collections.singletonList("bla")));
	}

	private void assertStrictImmutable(final Set<String> immutable) {
		assertException(UnsupportedOperationException.class, () -> immutable.iterator().remove());
		assertException(UnsupportedOperationException.class, () -> immutable.clear());
		assertException(UnsupportedOperationException.class, () -> immutable.remove("bla"));
		assertException(UnsupportedOperationException.class, () -> immutable.removeAll(Collections.singletonList("bla")));
		assertException(UnsupportedOperationException.class, () -> immutable.removeIf(s -> s.startsWith("entry")));
		assertException(UnsupportedOperationException.class, () -> immutable.retainAll(Collections.singletonList("bla")));
	}

	private void assertException(final Class<? extends Throwable> exceptionType, final Runnable r) {
		try {
			r.run();
			Assert.fail("Expected exception of type " + exceptionType.getName() + ", but no exception was thrown");
		} catch (final Throwable e) {
			if (!exceptionType.isAssignableFrom(e.getClass())) {
				Assert.fail("Expected exception of type " + exceptionType.getName() + ", but was " + e.getClass().getName());
			}
		}
	}
}
