package com.auth2.server.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

public class JwtRsaUtil {
    private String keyStoreFile;
    private char[] password;
    private KeyStore store;
    private Object lock = new Object();
 
    private static JwtRsaUtil instance = null;
 
    public static JwtRsaUtil getInstanceWithPassword() {
        synchronized (JwtRsaUtil.class) {
            if (instance == null) {
                synchronized (JwtRsaUtil.class) {
                    instance = new JwtRsaUtil("/jwt_truststore.jks", "joyshebao".toCharArray());
                }
            }
            return instance;
        }
    }

    public static JwtRsaUtil getInstance() {
        synchronized (JwtRsaUtil.class) {
            if (instance == null) {
                synchronized (JwtRsaUtil.class) {
                    instance = new JwtRsaUtil("/keystore.jks", null);
                }
            }
            return instance;
        }
    }
 
    private JwtRsaUtil(String _jksFilePath, char[] password) {
        this.keyStoreFile = _jksFilePath;
        this.password = password;
    }

    private JwtRsaUtil(String _jksFilePath) {
        this.keyStoreFile = _jksFilePath;
    }
 
    public KeyPair getKeyPair(String alias) {
        return getKeyPair(alias, this.password);
    }
 
    public KeyPair getKeyPair(String alias, char[] password) {
        try {
            synchronized (this.lock) {
                if (this.store == null) {
                    synchronized (this.lock) {
//                        InputStream is = this.getClass().getResourceAsStream(keyStoreFile);
                        InputStream is = new ClassPathResource(alias).getInputStream();
                        try {
                            this.store = KeyStore.getInstance("JKS");
                            this.store.load(is, this.password);
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) this.store.getKey(alias, password);
            RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
            return new KeyPair(publicKey, key);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load keys from store: " + this.keyStoreFile, e);
        }
    }
 
    public KeyPair getPublicPair(String alias) {
        return getPublicPair(alias, this.password);
    }
 
    public KeyPair getPublicPair (String alias, char[] password) {
        try {
            synchronized (this.lock) {
                if (this.store == null) {
                    synchronized (this.lock) {
                        InputStream is = this.getClass().getResourceAsStream(keyStoreFile);
                        try {
                            this.store = KeyStore.getInstance("JKS");
                            this.store.load(is, this.password);
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
 
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) this.store.getKey(alias, password);
            PublicKey publicKey= this.store.getCertificate(alias).getPublicKey();
 
            return new KeyPair(publicKey, key);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load keys from store: " + this.keyStoreFile, e);
        }
    }
}