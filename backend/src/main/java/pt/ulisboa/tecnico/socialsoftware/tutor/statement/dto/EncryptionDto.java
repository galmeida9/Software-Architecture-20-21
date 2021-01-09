package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.ResourceUtils;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.ACCESS_DENIED;

public class EncryptionDto implements Serializable {
    private String data;
    private String iv;

    public EncryptionDto(Serializable data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            SecretKey key = loadKey(ResourceUtils.getFile("classpath:aes-hex.key"));
            this.data = encrypt(key, this.data);
        } catch (Exception e) {
            throw new TutorException(ACCESS_DENIED);
        }
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // 128 default; 192 and 256 also possible
        return keyGenerator.generateKey();
    }

    private SecretKey loadKey(File file) throws IOException, DecoderException {
        String data = new String(readFileToByteArray(file));
        return new SecretKeySpec(decodeHex(data), "AES");
    }

    private String encrypt(SecretKey key, String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // Generating IV.
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        this.iv = new String(Base64.encodeBase64(iv));
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(value.getBytes());

        return Base64.encodeBase64String(encrypted);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    @Override
    public String toString() {
        return "EncryptionDto{" +
                "data='" + data + '\'' +
                ", iv='" + iv + '\'' +
                '}';
    }
}
