/**
 * Exceção lançada quando uma operação requer elementos em uma estrutura
 * vazia (por exemplo, seleção ou remoção de elemento de lista vazia).
 */
package com.biblioteca.data.structures;

public class UnderflowException extends RuntimeException {
    public UnderflowException(String message) {
        super(message);
    }
}
