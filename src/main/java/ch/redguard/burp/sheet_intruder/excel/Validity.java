package ch.redguard.burp.sheet_intruder.excel;

public class Validity {
    private final boolean valid;
    private final String reason;

    Validity(boolean valid, String reason) {
        this.valid = valid;
        this.reason = reason;
    }

    public final boolean isValid() {
        return valid;
    }

    public final String getReason() {
        return reason;
    }

}
