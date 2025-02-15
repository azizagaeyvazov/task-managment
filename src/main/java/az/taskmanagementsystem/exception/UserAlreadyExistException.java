package az.taskmanagementsystem.exception;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException() {
        super(ErrorMessage.USER_ALREADY_EXIST.getMessage());
    }
}
