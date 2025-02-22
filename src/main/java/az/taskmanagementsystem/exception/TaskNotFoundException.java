package az.taskmanagementsystem.exception;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(){
        super(ErrorMessage.TASK_NOT_FOUND.getMessage());
    }
}
