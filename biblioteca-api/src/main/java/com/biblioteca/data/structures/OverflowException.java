/**
 * Exceção lançada quando uma operação tenta inserir elementos em uma
 * estrutura que já atingiu sua capacidade máxima.
 */
package com.biblioteca.data.structures;

public class OverflowException extends RuntimeException {
    public OverflowException(String message) {
        super(message);
    }
}
