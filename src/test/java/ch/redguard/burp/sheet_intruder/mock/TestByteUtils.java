package ch.redguard.burp.sheet_intruder.mock;

import burp.api.montoya.utilities.ByteUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class TestByteUtils implements ByteUtils {

    @Override
    public int indexOf(byte[] data, byte[] searchTerm) {
        return indexOf(data, searchTerm, true);
    }

    @Override
    public int indexOf(byte[] data, byte[] searchTerm, boolean caseSensitive) {
        return indexOf(data, searchTerm, caseSensitive, 0, data.length);
    }

    @Override
    public int indexOf(byte[] data, byte[] searchTerm, boolean caseSensitive, int from, int to) {
        if (!caseSensitive) {
            data = new String(data).toLowerCase().getBytes();
            searchTerm = new String(searchTerm).toLowerCase().getBytes();
        }

        for (int i = from; i < to - searchTerm.length + 1; i++) {
            if (Arrays.equals(Arrays.copyOfRange(data, i, i + searchTerm.length), searchTerm)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int indexOf(byte[] data, Pattern pattern) {
        return indexOf(data, pattern, 0, data.length);
    }

    @Override
    public int indexOf(byte[] data, Pattern pattern, int from, int to) {
        // Convert byte array to String for pattern matching
        String dataString = convertToString(data);

        String subDataString = dataString.substring(from, to);
        java.util.regex.Matcher matcher = pattern.matcher(subDataString);
        if (matcher.find()) {
            return from + matcher.start();
        }
        return -1;
    }

    @Override
    public int countMatches(byte[] data, byte[] searchTerm) {
        return countMatches(data, searchTerm, true);
    }

    @Override
    public int countMatches(byte[] data, byte[] searchTerm, boolean caseSensitive) {
        return countMatches(data, searchTerm, caseSensitive, 0, data.length);
    }

    @Override
    public int countMatches(byte[] data, byte[] searchTerm, boolean caseSensitive, int from, int to) {
        int count = 0;
        int idx = from;
        while ((idx = indexOf(data, searchTerm, caseSensitive, idx, to)) != -1) {
            count++;
            idx += searchTerm.length;
        }
        return count;
    }

    public int countMatches(byte[] data, Pattern pattern) {
        return countMatches(data, pattern, 0, data.length);
    }

    @Override
    public int countMatches(byte[] data, Pattern pattern, int from, int to) {
        int count = 0;
        int index = from;
        while (index >= 0 && index < to) {
            index = indexOf(data, pattern, index, to);
            if (index >= 0) {
                count++;
                index++;
            }
        }
        return count;
    }


    @Override
    public String convertToString(byte[] bytes) {
        return new String(bytes);
    }

    @Override
    public byte[] convertFromString(String string) {
        return string.getBytes();
    }
}
