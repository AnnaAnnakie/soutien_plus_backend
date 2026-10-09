package com.example.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    private final BytesEncryptor encryptor;

    public EncryptionService(@Value("${encryption.password}") String password,
                             @Value("${encryption.salt}") String salt) {
        this.encryptor = Encryptors.stronger(password, salt);
    }

    public byte[] encrypt(byte[] data) {
        return encryptor.encrypt(data);
    }

    public byte[] decrypt(byte[] encryptedData) {
        return encryptor.decrypt(encryptedData);
    }
}
