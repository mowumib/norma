package com.hotelbooking.norma.exception;

public final class Message {


    public static final String SUCCESS_CREATE = "%s created successfully";
    public static final String SUCCESS_GET = "%s fetched successfully";
    public static final String SUCCESS_UPDATE = "%s updated successfully";
    public static final String SUCCESS_DELETE = "%s deleted successfully";
    public static final String SUCCESS_ACCEPT = "%s accepted successfully";
    public static final String SUCCESS_VALIDATE = "%s validated successfully";
    public static final String FAILED_VALIDATE = "%s is invalid";
    public static final String INVALID_REQUEST = "Invalid Request: %s";
    public static final String OPERATION_FAILURE = "Operation failed: %s";
    public static final String NOT_FOUND = "%s not found";
    public static final String NULL_ARGUMENT = "%s is empty.";
    public static final String ALREADY_EXISTS = "%s already exists";

    
    
    public static final String TOKEN_OBTAINED_SUCCESSFULLY = "Access token obtained successfully";
    public static final String DECRYPTED_SUCCESSFULLY = "Client ID and secret key decrypted successfully";
    public static final String REGISTERED_SUCCESSFULLY = "Client registered successfully";
    public static final String VERSION = "Version must be 1 to 30 characters and not contain special characters";
    public static final String AUTH_HEADER_ERROR = "Auth header name must not contain special characters.";
    public static final String API_KEY_HEADER_ERROR = "Api key header name must not contain special characters.";

    public static final String INVALID_CREDENTIALS = "Invalid Credentials";
    public static final String INVALID_TOKEN = "Invalid Token";
    public static final String INVALID_USER = "Invalid User";
    public static final String INVALID_PASSWORD = "XXXXXXX Password";
    public static final String INVALID_EMAIL = "Invalid Email";
    public static final String INVALID_PHONE = "Invalid Phone";
    public static final String INVALID_NAME = "Invalid Name";
    public static final String INVALID_ID = "Invalid ID";

    public static final String NOT_CANCELLED = "%s not cancelled";
    public static final String SUCCESS_CANCELLED = "%s cancelled successfully";
    public static final String ALREADY_AVAILABLE = "%s already available";
    public static final String SUCCESS_BOOKED = "%s booked successfully";
    public static final String BOOKING_COMPLETED = "%s completed successfully";

    public static final String EMPTY_PHOTO = "Please upload a photo";
    public static final String PHOTO_NOT_FOUND = "Photo not found";
    
    private Message() {
        throw new AssertionError("Cannot instantiate Message");
    }
}
   