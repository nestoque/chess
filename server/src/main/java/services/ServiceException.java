package services;

public class ServiceException extends Exception {
    private final int statusCode;

    public ServiceException(int statusCode, String message) {
        super(message); // add this to regular exception message
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}