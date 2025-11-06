//Se lanza si el usuario no tiene permisos o el token de Supabase no es válido.

package com.sebsrvv.app.modules.meals.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}