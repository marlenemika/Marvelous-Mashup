package de.uulm.sopra.team08.server.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    R apply(T t, U u, V v);

    default <W> TriFunction<T, U, V, W> andThen(@NotNull Function<? super R, ? extends W> after) {
        return (t, u, v) -> after.apply(this.apply(t, u, v));
    }

}
