package util;

import java.io.IOException;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class Utilities {

    public static String getRequestBody() throws IOException {
        String requestBody = new String(readAllBytes(get(System.getProperty("user.dir") + "/src/test/resources/testData/fetchUserDetails.json")));
        return requestBody;
    }

}
