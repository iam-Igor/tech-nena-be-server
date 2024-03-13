package isuruygor.demo.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(long id) {
        super("Item with id: " + id + " not found");
    }

    public NotFoundException(String message) {
        super(message);
    }
}

