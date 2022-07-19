package de.uulm.sopra.team08.server.util;

public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);

}
