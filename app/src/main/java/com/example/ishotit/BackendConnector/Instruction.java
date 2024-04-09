package com.example.ishotit.BackendConnector;

public class Instruction {

    public static void loadCurrentInstruction(ResponseCallback<String> callback) {
        callback.onResult("Take a photo of something that makes you happy!");
    }

    public interface ResponseCallback<T> {
        void onResult(T value);
    }

}
