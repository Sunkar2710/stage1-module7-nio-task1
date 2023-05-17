package com.epam.mjc.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileReader {

    private final HashMap<String, String> hashMap = new HashMap<>();

    public Profile getDataFromFile(File file) {

        StringBuilder text = new StringBuilder();

        try (RandomAccessFile aFile = new RandomAccessFile(file, "r");
            FileChannel inChannel = aFile.getChannel()) {

            long fileSize = inChannel.size();

            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
            inChannel.read(buffer);
            buffer.flip();

            for (int i = 0; i < fileSize; i++) {
                text.append((char) buffer.get());
            }

            String regexKey = "^(.*)(?=:)";
            String regexValue = "(?<=: )(.*)";

            Pattern patternKey = Pattern.compile(regexKey, Pattern.MULTILINE);
            Pattern patternValue = Pattern.compile(regexValue);

            Matcher matcherKey = patternKey.matcher(text);
            Matcher matcherValue = patternValue.matcher(text);

            while (matcherKey.find() && matcherValue.find()) {
                hashMap.put(text.substring(matcherKey.start(), matcherKey.end()), text.substring(matcherValue.start(), matcherValue.end()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Profile(hashMap.get("Name"),
                Integer.parseInt(hashMap.get("Age")),
                hashMap.get("Email"),
                Long.parseLong(hashMap.get("Phone")));
    }
}
