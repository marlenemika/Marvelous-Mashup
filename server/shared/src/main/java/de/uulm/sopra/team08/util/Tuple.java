package de.uulm.sopra.team08.util;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * A simple generic class to return two values from a function.
 *
 * @param <T>
 * @param <E>
 */
public final class Tuple<T, E> {

    @SerializedName("entityID")
    public final T first;
    @SerializedName("ID")
    public final E second;


    /**
     * Creates a new Tuple. Contains two elements.
     *
     * @param first  element of Type T
     * @param second element of Type E
     */
    public Tuple(T first, E second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Converts a Tuple containing two Integers into a JsonArray with two elements.
     *
     * @param tuple Integer Tuple to be converted
     * @return JsonArray containing two Integers
     */
    public static JsonArray toJsonArray(Tuple<Integer, Integer> tuple) {
        JsonArray tupleArray = new JsonArray();
        tupleArray.add(tuple.first);
        tupleArray.add(tuple.second);
        return tupleArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(first, tuple.first) && Objects.equals(second, tuple.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" +
               first +
               ", " +
               second +
               ')';
    }

}