package Enums;

public enum MessageType {
    REQUEST,
    REPLY,
    RELEASE,
    PRINT,
    PRINT_ACK,
    JOIN,
    JOIN_ACK,
    LEAVE;

    @Override
    public String toString() {
        return name();
    }
}
