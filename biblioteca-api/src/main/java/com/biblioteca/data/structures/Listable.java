package com.biblioteca.data.structures;

public interface Listable<T> {
  void insert(Object data, int LogicIndex);

  void append(Object data);

  Object select(int LogicIndex);

  Object[] selectAll();

  void update(Object data, int LogicIndex);

  Object delete(int LogicIndex);

  void clear();

  boolean isEmpty();

  boolean isFull();

  String print();
}

