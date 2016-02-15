package io.vivarium.util;

public class UserFacingError extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public UserFacingError(String message)
    {
        super(message);
    }
}
