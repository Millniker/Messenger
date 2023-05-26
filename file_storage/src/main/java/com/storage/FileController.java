package com.storage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fle")
public class FileController {
    private final FileService fileService;
    @PostMapping("/upload" )
    @SneakyThrows
    public String upload (@RequestParam ("file")MultipartFile file) {
        return fileService.upload(file.getBytes());
    }
    @SneakyThrows
    @GetMapping(value = "/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] download(@PathVariable("id") String id,String filename){
//        ContentDisposition contentDisposition =ContentDisposition.builder("inline")
//                .filename(filename)
//                .build();
//        HttpHeaders headers =new HttpHeaders();
//        headers.setContentDisposition(contentDisposition);
//        ResponseEntity.ok().headers(headers).body(content);
        return fileService.dowload(id);
    }
}
