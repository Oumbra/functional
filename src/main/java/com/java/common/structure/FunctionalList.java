package com.java.common.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

public class FunctionalList<E> extends ArrayList<E> implements CastableList<E> {

	private static final long serialVersionUID = -1848430177116834737L;
	private int internalIndex = 0;

    public FunctionalList() {
    	super();
    }

    public FunctionalList(E...objs) {
    	this();
        Stream.of(objs).map(this::addChain);
    }

    @Override
    public Optional<E> toOption(int index) {
        return index > size() ? Optional.empty(): Optional.ofNullable(get(index));
    }

    @Override
    public <T> Optional<T> toOption(int index, Class<T> type) {
        return Optional.ofNullable(get(index, type));
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return index > size() ? null: (T) get(index);
    }

    public FunctionalList<E> addChain(E el) {
        add(el);
        return this;
    }

    public FunctionalList<E> addAllChain(List<E> els) {
        addAll(els);
        return this;
    }

    @Override
    public boolean hasNext() {
        return internalIndex < size();
    }

    @Override
    public boolean hasPrevious() {
        return internalIndex - 1 >= 0;
    }

    public E next() {
        if (!hasNext()) throw new NoSuchElementException();
        return get(internalIndex++);
    }

    @Override
    public <T> T next(Class<T> type){
        return (T) next();
    }

    @Override
    public E previous() {
        if (!hasPrevious()) throw new NoSuchElementException();
        return get(--internalIndex);
    }

    @Override
    public <T> T previous(Class<T> type){
        return (T) previous();
    }

    @Override
    public E get() {
        return get(internalIndex);
    }

    @Override
    public <T> T get(Class<T> type){
        return (T) get(internalIndex);
    }

    @Override
    public E first() {
        return get(0);
    }

    @Override
    public E last() {
        return get(size() - 1);
    }


    public static <T> FunctionalList<T> convert(List<T> list) {
        return new FunctionalList<T>().addAllChain(list);
    }
}
