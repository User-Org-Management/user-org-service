package com.user_organization_management.exception;

import java.io.Serial;

public class EntityNotFoundException extends RuntimeException {
	@Serial
    private static final long serialVersionUID = 1L;
	public EntityNotFoundException(String message) {
        super(message);
    }
}
