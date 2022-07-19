package de.uulm.sopra.team08.util;

import org.jetbrains.annotations.Nullable;

public class ArrayUtils {

    private ArrayUtils() {}

    /**
     * A helper method to get the the element at i in the given array.
     *
     * @param arr An array to get the element of.
     * @param i   The index of the element.
     * @param <T> The Type of the parameter.
     * @return The element at i or {@code null} if the index is out of bounds.
     */
    @Nullable
    public static <T> T getI(T[] arr, int i) {
        try {
            return arr[i];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
