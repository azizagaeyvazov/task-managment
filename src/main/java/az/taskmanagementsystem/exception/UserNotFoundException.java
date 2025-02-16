package az.taskmanagementsystem.exception;

import static az.taskmanagementsystem.exception.ErrorMessage.USER_NOT_FOUND;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException() {
        super(USER_NOT_FOUND.getMessage());
    }
}
