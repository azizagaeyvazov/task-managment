package az.taskmanagementsystem.exception;

import static az.taskmanagementsystem.exception.ErrorMessage.USER_ALREADY_EXIST;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException() {
        super(USER_ALREADY_EXIST.getMessage());
    }
}
