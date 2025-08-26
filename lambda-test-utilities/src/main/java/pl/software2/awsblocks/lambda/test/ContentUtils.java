package pl.software2.awsblocks.lambda.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class ContentUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public static byte[] decode(String body) {
        return Base64.getDecoder().decode(body);
    }

    public static String decodeToString(String body) {
        return new String(Base64.getDecoder().decode(body));
    }

    public static String encode(String body) {
        return encode(body.getBytes());
    }

    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String unzip(byte[] bytes) throws Exception {
        var gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        return new String(gis.readAllBytes());
    }

    public static byte[] zip(String bytes) throws Exception {
        var outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(bytes.getBytes());
            gzip.flush();
        }
        return outputStream.toByteArray();
    }

    public static <RS> RS loadJsonResource(Object testClass, String fileName, Class<RS> clazz) {
        try (var steam = testClass.getClass().getClassLoader().getResourceAsStream(fileName)) {
            return objectMapper.readValue(steam, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectMapper objectMapper() {
        return objectMapper;
    }
}
