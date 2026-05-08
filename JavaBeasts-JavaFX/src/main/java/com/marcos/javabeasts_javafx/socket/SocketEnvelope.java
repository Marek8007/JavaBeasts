package com.marcos.javabeasts_javafx.socket;

public class SocketEnvelope<T> {

    private String status;
    private T data;

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}
