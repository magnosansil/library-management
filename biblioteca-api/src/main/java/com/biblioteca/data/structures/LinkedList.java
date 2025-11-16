package com.biblioteca.data.structures;

public class LinkedList<T> implements Listable<T> {
  private DoubleNode<T> head;
  private DoubleNode<T> tail;
  private int capacity;
  private int numberElements;

  public LinkedList() {
    this(10);
  }

  public LinkedList(int capacity) {
    head = null;
    tail = null;
    this.capacity = capacity;
    numberElements = 0;
  }

  @Override
  public void insert(Object data, int LogicIndex) {
    int index = LogicIndex;
    @SuppressWarnings("unchecked")
    T typedData = (T) data;
    if (isFull()) {
      throw new OverflowException("Lista cheia!");
    }
    if (index < 0 || index >= numberElements) {
      throw new IndexOutOfBoundsException("Índice inválido: " + index);
    }
    DoubleNode<T> previous = null;
    DoubleNode<T> next = head;
    DoubleNode<T> newNode = new DoubleNode<>();
    newNode.setData(typedData);
    for (int i = 0; i < index; i++) {
      previous = next;
      next = next.getNext();
    }
    if (previous != null) {
      newNode.setPrevious(previous);
      previous.setNext(newNode);
    } else {
      head = newNode;
    }
    if (next != null) {
      newNode.setNext(next);
      next.setPrevious(newNode);
    } else {
      tail = newNode;
    }
    numberElements++;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void append(Object data) {
    if (isFull()) {
      throw new OverflowException("Lista Cheia!");
    }

    DoubleNode<T> newNode = new DoubleNode<>();
    newNode.setData((T) data);

    if (!isEmpty()) {
      tail.setNext(newNode);
      newNode.setPrevious(tail);
      tail = newNode;
    } else {
      head = newNode;
      tail = newNode;
    }
    numberElements++;
  }

  @Override
  public Object select(int LogicIndex) {
    int index = LogicIndex;
    if (isEmpty()) {
      throw new UnderflowException("Lista Vazia!");
    }
    if (index < 0 || index >= numberElements) {
      throw new IndexOutOfBoundsException("Índice inválido: " + index);
    }

    return getNodeAt(index).getData();
  }

  @Override
  public Object[] selectAll() {
    Object[] buffer = new Object[numberElements];
    DoubleNode<T> aux = head;
    for (int i = 0; i < numberElements; i++) {
      buffer[i] = aux.getData();
      aux = aux.getNext();
    }
    return buffer;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void update(Object data, int LogicIndex) {
    if (isEmpty()) {
      throw new UnderflowException("Lista Vazia!");
    }

    int index = LogicIndex;
    if (index < 0 || index >= numberElements) {
      throw new IndexOutOfBoundsException("Índice inválido: " + index);
    }

    getNodeAt(index).setData((T) data);
  }

  @Override
  public Object delete(int LogicIndex) {
    int index = LogicIndex;
    if (isEmpty()) {
      throw new UnderflowException("Lista vazia!");
    }

    if (index < 0 || index > capacity) {
      throw new IndexOutOfBoundsException("Índice inválido");
    }

    DoubleNode<T> aux = getNodeAt(index);

    if (numberElements == 1) {
      head = null;
      tail = null;
    } else if (index == 0) {
      aux.getNext().setPrevious(null);
      head = aux.getNext();
    } else if (index == numberElements - 1) {
      aux.getPrevious().setNext(null);
      tail = aux.getPrevious();
    } else {
      aux.getPrevious().setNext(aux.getNext());
      aux.getNext().setPrevious(aux.getPrevious());
    }

    numberElements--;
    return aux.getData();
  }

  @Override
  public void clear() {
    head = null;
    tail = null;
    numberElements = 0;
  }

  @Override
  public boolean isEmpty() {
    return numberElements == 0;
  }

  @Override
  public boolean isFull() {
    return numberElements == capacity;
  }

  @Override
  public String print() {
    String buffer = "";
    DoubleNode<T> aux = head;
    for (int i = 0; i < numberElements; i++) {
      buffer += aux.getData();
      if (i != numberElements - 1) {
        buffer += ",";
      }
      aux = aux.getNext();
    }
    return "[" + buffer + "]";
  }

  private DoubleNode<T> getNodeAt(int index) {
    DoubleNode<T> current = head;
    for (int i = 0; i < index; i++) {
      current = current.getNext();
    }
    return current;
  }
}
