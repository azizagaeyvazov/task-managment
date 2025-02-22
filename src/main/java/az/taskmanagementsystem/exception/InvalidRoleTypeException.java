package az.taskmanagementsystem.exception;

public class InvalidRoleTypeException extends RuntimeException{

    public InvalidRoleTypeException() {
        super(ErrorMessage.INVALID_ROLE_TYPE.getMessage());
    }
}
