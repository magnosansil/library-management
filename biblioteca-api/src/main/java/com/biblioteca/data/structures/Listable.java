/**
 * Interface que define o contrato para estruturas de lista utilizadas no projeto.
 *
 * <p>Os métodos utilizam índices lógicos (0-based) e operam com objetos genéricos.
 * Implementações devem lançar as exceções apropriadas conforme contrato
 * (por exemplo, {@code OverflowException}, {@code UnderflowException} ou
 * {@code IndexOutOfBoundsException}).</p>
 *
 * @param <T> tipo genérico dos elementos (informativo para implementações)
 */
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
