package az.taskmanagementsystem.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException() {
        super(ErrorMessage.USER_NOT_FOUND.getMessage());
    }
}
