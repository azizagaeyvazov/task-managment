package az.taskmanagementsystem.exception;

public class TokenNotFoundException extends RuntimeException{

    public TokenNotFoundException() {
        super(ErrorMessage.TOKEN_NOT_FOUND.getMessage());
    }
}
