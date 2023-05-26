package com.storage;

public interface FileService {
    String upload(byte[] content);
    byte[] dowload(String id);
}
