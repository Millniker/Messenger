package com.chat.controllers;

import com.chat.entity.DTO.*;
import com.chat.repository.UserChatRepository;
import com.chat.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final UserChatRepository userChatRepository;
    private final MessageServiceImpl messageService;

    /**
     *
     *в данном методе и аналогичных с отправкой файлов, запрос посылался через form-data
     */
    @PostMapping(value = "/dialog/message")
    public ResponseEntity<Void> sendMessageDialog (@RequestPart(name="data") CreateDialogMessageDto createMessageDto, @RequestPart(required = false,name ="file") List<MultipartFile> file ){
        messageService.sendMessageInDialog(createMessageDto,file);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<Void> createChat(@RequestPart(
            name="data") CreateChatDto createChatDto, @RequestPart(required = false,name ="file") MultipartFile file){
        messageService.createChat(createChatDto,file);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateChat(@PathVariable UUID id, @RequestPart(name="data") UpdateChatDto updateChatDto, @RequestPart(required = false,name ="file") MultipartFile file){
        messageService.updateChat(id,updateChatDto,file);
        return ResponseEntity.ok().build();
    }
    @GetMapping( "/chats/{id}")
    public ResponseEntity<List<MessageDto>> getMessageInChat(@PathVariable UUID id){
        return ResponseEntity.ok(messageService.getMessages(id));
    }
    @PostMapping("/chats")
    public ResponseEntity<List<ChatDto>> getChats(@RequestBody PageDto pageDto){
        return ResponseEntity.ok(messageService.getChats(pageDto));
    }
    @GetMapping("/info/{id}")
    public ResponseEntity<ChatInfoDto> getChatInfo(@PathVariable UUID id){
        return ResponseEntity.ok(messageService.getChatInfo(id));
    }
    @PostMapping("/{id}/message")
    public ResponseEntity<Void> sendMessageChat(@PathVariable UUID id, @RequestPart(name="data") CreateChatMessageDto createChatMessageDto, @RequestPart(required = false,name ="file") List<MultipartFile> file){
        messageService.sendMessageInChat(id,createChatMessageDto,file);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/find")
    public ResponseEntity<List<FindedMessagesDto>> findMessage(@RequestBody FindMessageDto findMessageDto){
        return ResponseEntity.ok(messageService.findMessages(findMessageDto));
    }

}
