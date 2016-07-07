package com.annimon.stream;

import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.IntFunction;
import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ints.*;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A sequence of primitive int-valued elements supporting sequential operations. This is the {@code int}
 * primitive specialization of {@link Stream}
 *
 */
public final class IntStream {

    /**
     * Returns an empty stream.
     *
     * @return the new empty stream
     */
    public static IntStream empty() {
        return new IntStream(new PrimitiveIterator.OfInt() {
            @Override
            public int nextInt() {
                return 0;
            }

            @Override
            public boolean hasNext() {
                return false;
            }
        });
    }

    /**
     * Returns stream whose elements are the specified values.
     *
     * @param values the elements of the new stream
     * @return the new stream
     */
    public static IntStream of(final int... values) {
        Objects.requireNonNull(values);
        return new IntStream(new PrimitiveIterator.OfInt() {

            private int index = 0;

            @Override
            public int nextInt() {
                return values[index++];
            }

            @Override
            public boolean hasNext() {
                return index < values.length;
            }
        });
    }

    /**
     * Returns stream which contains single element passed as param
     *
     * @param t element of the stream
     * @return the new stream
     */
    public static IntStream of(final int t) {
        return new IntStream(new PrimitiveIterator.OfInt() {

            private int index = 0;

            @Override
            public int nextInt() {
                index++;
                return t;
            }

            @Override
            public boolean hasNext() {
                return index == 0;
            }
        });
    }

    /**
     * Creates a lazily concatenated stream whose elements are all the
     * elements of the first stream followed by all the elements of the
     * second stream.
     * @param a the first stream
     * @param b the second stream
     * @return the concatenation of the two input streams
     */
    public static IntStream concat(final IntStream a, final IntStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        return new IntStream(new PrimitiveIterator.OfInt() {

            private boolean firstStreamIsCurrent = true;

            @Override
            public int nextInt() {
                return firstStreamIsCurrent ? a.iterator.nextInt() : b.iterator.nextInt();
            }

            @Override
            public boolean hasNext() {
                if(firstStreamIsCurrent) {
                    if(a.iterator.hasNext())
                        return true;

                    firstStreamIsCurrent = false;
                }

                return b.iterator.hasNext();
            }
        });
    }

    /**
     * Returns an infinite sequential unordered stream where each element is
     * generated by the provided {@code IntSupplier}.  This is suitable for
     * generating constant streams, streams of random elements, etc.
     *
     * @param s the {@code IntSupplier} for generated elements
     * @return a new infinite sequential {@code IntStream}
     */
    public static IntStream generate(final IntSupplier s) {
        return new IntStream(new PrimitiveIterator.OfInt() {
            @Override
            public int nextInt() {
                return s.getAsInt();
            }

            @Override
            public boolean hasNext() {
                return true;
            }
        });
    }

    /**
     * Returns an infinite sequential ordered {@code IntStream} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * prooducing a {@code Stream} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
     * <p> The first element (position {@code 0}) in the {@code IntStream} will be
     * the provided {@code seed}.  For {@code n > 0}, the element at position
     * {@code n}, will be the result of applying the function {@code f} to the
     * element at position {@code n - 1}.
     *
     * @param seed the initial element
     * @param f a function to be applied to to the previous element to produce
     *          a new element
     * @return A new sequential {@code IntStream}
     */
    public static IntStream iterate(final int seed, final IntUnaryOperator f) {
        return new IntStream(new PrimitiveIterator.OfInt() {

            private int current = seed;

            @Override
            public int nextInt() {

                int old = current;
                current = f.applyAsInt(current);

                return old;
            }

            @Override
            public boolean hasNext() {
                return true;
            }
        });
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    public static IntStream range(final int startInclusive, final int endExclusive) {

        if(startInclusive >= endExclusive)
            return empty();

        return new IntStream(new PrimitiveIterator.OfInt() {

            private int current = startInclusive;

            @Override
            public int nextInt() {
                return current++;
            }

            @Override
            public boolean hasNext() {
                return current < endExclusive;
            }
        });
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    public static IntStream rangeClosed(int startInclusive, int endInclusive) {
        return range(startInclusive, endInclusive+1);
    }

    private final PrimitiveIterator.OfInt iterator;

    IntStream(PrimitiveIterator.OfInt iterator) {
        this.iterator = iterator;
    }

    /**
     * Returns stream's internal iterator
     * @return stream's internal iterator
     */
    public PrimitiveIterator.OfInt iterator() {
        return iterator;
    }

    /**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
     *
     * <p> This is an intermidiate operation.
     *
     * @param predicate non-interfering, stateless predicate to apply to each
     *                  element to determine if it should be included
     * @return the new stream
     */
    public IntStream filter(final IntPredicate predicate) {
        Objects.requireNonNull(predicate);

        return new IntStream(new PrimitiveIterator.OfInt() {

            private int next;

            @Override
            public int nextInt() {
                return next;
            }

            @Override
            public boolean hasNext() {
                while(iterator.hasNext()) {
                    next = iterator.next();
                    if(predicate.test(next)) {
                        return true;
                    }
                }

                return false;
            }
        });
    }

    /**
     * Returns a stream consisting of the elements of this stream that don't
     * match the given predicate.
     *
     * <p> This is an intermidiate operation.
     *
     * @param predicate non-interfering, stateless predicate to apply to each
     *                  element to determine if it should not be included
     * @return the new stream
     */
    public IntStream filterNot(final IntPredicate predicate) {
        return filter(IntPredicate.Util.negate(predicate));
    }

    /**
     * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermidiate operation.
     *
     * @param mapper a non-interfering stateless function to apply to
     *               each element
     * @return the new stream
     */
    public IntStream map(final IntUnaryOperator mapper) {
        return new IntStream(new PrimitiveIterator.OfInt() {
            @Override
            public int nextInt() {
                return mapper.applyAsInt(iterator.nextInt());
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
        });
    }

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.
     *
     * <p>This is an intermidiate operation.
     * @param mapper a non-interfering stateless function to apply to each
     *               element which produces an {@code IntStream} of new values
     * @return the new stream
     * @see Stream#flatMap(Function)
     */
    public IntStream flatMap(final IntFunction<? extends IntStream> mapper) {
        return new IntStream(new PrimitiveIterator.OfInt() {

            private PrimitiveIterator.OfInt inner;

            @Override
            public int nextInt() {
                return inner.nextInt();
            }

            @Override
            public boolean hasNext() {

                if(inner != null && inner.hasNext()) {
                    return true;
                }

                while(iterator.hasNext()) {
                    int arg = iterator.next();

                    IntStream result = mapper.apply(arg);
                    if(result == null) {
                        continue;
                    }

                    if(result.iterator.hasNext()) {
                        inner = result.iterator;
                        return true;
                    }
                }

                return false;
            }
        });
    }

    /**
     * Returns a stream consisting of the distinct elements of this stream.
     *
     * <p>This is a stateful intermediate operation.
     *
     * @return the new stream
     */
    public IntStream distinct() {
        // While functional and quick to implement, this approach is not very efficient.
        // An efficient version requires an int-specific map/set implementation.

        final Stream<Integer> dist = boxed().distinct();

        return new IntStream(new PrimitiveIterator.OfInt() {

            Iterator<? extends Integer> inner = dist.getIterator();

            @Override
            public int nextInt() {
                return inner.next().intValue();
            }

            @Override
            public boolean hasNext() {
                return inner.hasNext();
            }
        });
    }

    /**
     * Returns a stream consisting of the elements of this stream in sorted
     * order.
     *
     * <p>This is a stateful intermediate operation.
     *
     * @return the new stream
     */
    public IntStream sorted() {

        return new IntStream(new PrimitiveIterator.OfInt() {

            int index = 0;
            int[] array;

            @Override
            public int nextInt() {
                return array[index++];
            }

            @Override
            public boolean hasNext() {
                SpinedBuffer.OfInt buffer = new SpinedBuffer.OfInt();
                forEach(buffer);
                array = buffer.asPrimitiveArray();
                Arrays.sort(array);

                return index < array.length;
            }
        });
    }

    /**
     * Returns a stream consisting of the elements of this stream, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting stream. Handy method for debugging purposes.
     *
     * <p>This is an intermediate operation.
     * @param action the action to be performed on each element
     * @return the new stream
     */
    public IntStream peek(final IntConsumer action) {
        return new IntStream(new PrimitiveIterator.OfInt() {
            @Override
            public int nextInt() {
                int value = iterator.nextInt();
                action.accept(value);
                return value;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
        });
    }

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than {@code maxSize} in length.
     *
     * <p> This is a short-circuiting stateful intermediate operation.
     *
     * @param maxSize the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    public IntStream limit(final long maxSize) {

        if(maxSize < 0) {
            throw new IllegalArgumentException();
        }

        return new IntStream(new PrimitiveIterator.OfInt() {

            private long index;

            @Override
            public int nextInt() {
                index++;
                return iterator.nextInt();
            }

            @Override
            public boolean hasNext() {
                return index < maxSize;
            }
        });
    }

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after discarding the first {@code n} elements of the stream.
     * If this stream contains fewer than {@code n} elements then an
     * empty stream will be returned.
     *
     * <p>This is a stateful intermediate operation.
     *
     * @param n the number of leading elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public IntStream skip(final long n) {
        if(n < 0)
            throw new IllegalArgumentException(Long.toString(n));

        if(n == 0)
            return this;
        else
            return new IntStream(new PrimitiveIterator.OfInt() {
                long skipped = 0;
                @Override
                public int nextInt() {
                    return iterator.nextInt();
                }

                @Override
                public boolean hasNext() {

                    while(iterator.hasNext()) {

                        if(skipped == n) break;

                        skipped++;
                        iterator.nextInt();
                    }

                    return iterator.hasNext();
                }
            });
    }

    /**
     * Performs an action for each element of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @param action a non-interfering action to perform on the elements
     */
    public void forEach(IntConsumer action) {
        while(iterator.hasNext()) {
            action.accept(iterator.nextInt());
        }
    }

    /**
     * Returns an array containing the elements of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return an array containing the elements of this stream
     */
    public int[] toArray() {
        SpinedBuffer.OfInt b = new SpinedBuffer.OfInt();

        forEach(b);

        return b.asPrimitiveArray();
    }

    /**
     * Performs a reduction on the elements of this stream, using the provided
     * identity value and an associative accumulation function, and returns the
     * reduced value.
     *
     * <p>The {@code identity} value must be an identity for the accumulator
     * function. This means that for all {@code x},
     * {@code accumulator.apply(identity, x)} is equal to {@code x}.
     * The {@code accumulator} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * @param identity the identity value for the accumulating function
     * @param op an associative non-interfering stateless function for
     *           combining two values
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     */
    public int reduce(int identity, IntBinaryOperator op) {
        int result = identity;
        while(iterator.hasNext()) {
            int value = iterator.nextInt();
            result = op.applyAsInt(result, value);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of this stream, using an
     * associative accumulation function, and returns an {@code OptionalInt}
     * describing the reduced value, if any.
     *
     * <p>The {@code op} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * @param op an associative, non-interfering, stateless function for
     *           combining two values
     * @return the result of the reduction
     * @see #reduce(int, IntBinaryOperator)
     */
    public OptionalInt reduce(IntBinaryOperator op) {
        boolean foundAny = false;
        int result = 0;
        while(iterator.hasNext()) {
            int value = iterator.nextInt();

            if(!foundAny) {
                foundAny = true;
                result = value;
            } else {
                result = op.applyAsInt(result, value);
            }
        }
        return foundAny ? OptionalInt.of(result) : OptionalInt.empty();
    }


    /**
     * Returns the sum of elements in this stream.
     *
     * @return the sum of elements in this stream
     */
    public int sum() {
        int sum = 0;
        while(iterator.hasNext()) {
            sum += iterator.nextInt();
        }

        return sum;
    }

    /**
     * Returns an {@code OptionalInt} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return an {@code OptionalInt} containing the minimum element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty
     */
    public OptionalInt min() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left < right ? left : right;
            }
        });
    }

    /**
     * Returns an {@code OptionalInt} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return an {@code OptionalInt} containing the maximum element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty
     */
    public OptionalInt max() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left > right ? left : right;
            }
        });
    }

    /**
     * Returns the count of elements in this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return the count of elements in this stream
     */
    public long count() {
        long count = 0;
        while(iterator.hasNext()) {
            iterator.nextInt();
            count++;
        }
        return count;
    }

    /**
     * Returns whether any elements of this stream match the provided
     * predicate. May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the stream is empty then
     * {@code false} is returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @param predicate a non-interfering stateless predicate to apply
     *                  to elements of this stream
     * @return {@code true} if any elements of the stream match the provided
     * predicate, otherwise {@code false}
     */
    public boolean anyMatch(IntPredicate predicate) {
        while(iterator.hasNext()) {
            if(predicate.test(iterator.nextInt()))
                return true;
        }

        return false;
    }

    /**
     * Returns whether all elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @param predicate a non-interfering stateless predicate to apply to
     *                  elements of this stream
     * @return {@code true} if either all elements of the stream match the
     * provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean allMatch(IntPredicate predicate) {
        while(iterator.hasNext()) {
            if(!predicate.test(iterator.nextInt()))
                return false;
        }

        return true;
    }

    /**
     * Returns whether no elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @param predicate a non-interfering stateless predicate to apply to
     *                  elements of this stream
     * @return {@code true} if either no elements of the stream match the
     * provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean noneMatch(IntPredicate predicate) {

        if(!iterator.hasNext())
            return true;

        while(iterator.hasNext()) {
            if(predicate.test(iterator.nextInt()))
                return false;
        }

        return true;
    }

    /**
     * Returns an {@link OptionalInt} describing the first element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalInt} describing the first element of this stream,
     * or an empty {@code OptionalInt} if the stream is empty
     */
    public OptionalInt findFirst() {
        if(iterator.hasNext()) {
            return OptionalInt.of(iterator.nextInt());
        } else {
            return OptionalInt.empty();
        }
    }

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to an {@code Integer}.
     *
     * <p>This is an lazy intermediate operation.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     * each boxed to an {@code Integer}
     */
    public Stream<Integer> boxed() {
        return Stream.of(iterator);
    }


}
