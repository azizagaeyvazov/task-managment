package az.taskmanagementsystem.exception;

import static az.taskmanagementsystem.exception.ErrorMessage.INVALID_ROLE_TYPE;

public class InvalidRoleTypeException extends RuntimeException{

    public InvalidRoleTypeException() {
        super(INVALID_ROLE_TYPE.getMessage());
    }
}
