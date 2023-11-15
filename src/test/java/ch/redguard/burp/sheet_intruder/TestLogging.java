package ch.redguard.burp.sheet_intruder;

import burp.api.montoya.logging.Logging;

import java.io.PrintStream;

public class TestLogging implements Logging {

    @Override
    public PrintStream output() {
        return System.out;
    }

    @Override
    public PrintStream error() {
        return System.err;
    }

    @Override
    public void logToOutput(String message) {
        System.out.println(message);
    }

    @Override
    public void logToError(String message) {
        System.err.println(message);
    }

    @Override
    public void logToError(String message, Throwable cause) {
        logToError(message);
        logToError(cause.getMessage());
    }

    @Override
    public void logToError(Throwable cause) {
        logToError(cause.getMessage());
    }

    @Override
    public void raiseDebugEvent(String message) {
        logToOutput(message);
    }

    @Override
    public void raiseInfoEvent(String message) {
        logToOutput(message);
    }

    @Override
    public void raiseErrorEvent(String message) {
        logToError(message);
    }

    @Override
    public void raiseCriticalEvent(String message) {
        logToError(message);
    }
}
