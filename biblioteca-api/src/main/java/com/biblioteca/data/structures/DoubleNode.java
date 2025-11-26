/**
 * Nó duplamente encadeado genérico utilizado pela implementação de lista.
 *
 * <p>Armazena um valor genérico {@code T} e referências para o próximo e o
 * nó anterior na lista. Fornece getters e setters simples para uso pela
 * estrutura de dados {@code LinkedList}.</p>
 *
 * @param <T> tipo dos dados armazenados no nó
 */
package com.biblioteca.data.structures;

public class DoubleNode<T> {
    private T data;
    private DoubleNode<T> next, previous;

    /**
     * Retorna o dado armazenado no nó.
     *
     * @return dado do tipo {@code T}
     */
    public T getData() {
        return data;
    }

    /**
     * Retorna a referência para o próximo nó na lista.
     *
     * @return próximo nó ou {@code null} se for o último
     */
    public DoubleNode<T> getNext() {
        return next;
    }

    /**
     * Retorna a referência para o nó anterior na lista.
     *
     * @return nó anterior ou {@code null} se for o primeiro
     */
    public DoubleNode<T> getPrevious() {
        return previous;
    }

    /**
     * Define o dado armazenado no nó.
     *
     * @param data dado a ser armazenado
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Define a referência para o próximo nó.
     *
     * @param next próximo nó
     */
    public void setNext(DoubleNode<T> next) {
        this.next = next;
    }

    /**
     * Define a referência para o nó anterior.
     *
     * @param previous nó anterior
     */
    public void setPrevious(DoubleNode<T> previous) {
        this.previous = previous;
    }
}
