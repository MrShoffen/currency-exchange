package org.mrshoffen.exchange.utils.exchange.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<E> {

    List<E> findAll();

    Optional<E> save(E entity); //md rework to optional
}
