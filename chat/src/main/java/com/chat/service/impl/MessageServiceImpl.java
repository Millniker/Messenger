package com.chat.service.impl;

import com.chat.entity.Attachment;
import com.chat.entity.Chat;
import com.chat.entity.DTO.*;
import com.chat.entity.Message;
import com.chat.entity.UserChat;
import com.chat.entity.enums.Type;
import com.chat.exceptions.CommonException;
import com.chat.mapper.ChatMapper;
import com.chat.repository.AttachmentRepository;
import com.chat.repository.ChatRepository;
import com.chat.repository.MessageRepository;
import com.chat.repository.UserChatRepository;
import com.chat.service.MessageService;
import com.common.model.CreateNotifyDTO;
import com.common.security.props.SecurityProps;
import com.common.service.impl.JwtServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.common.model.NotifyType.NewMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserChatRepository userChatRepository;
    private final ChatRepository chatRepository;
    private final Clock clock;
    private final ChatMapper chatMapper;
    private final SecurityProps securityProps;
    private final StreamBridge streamBridge;
    private final JwtServiceImpl jwtService;
    @Value("${max.file.count}")
    private int maxFileCount;
    private final AttachmentRepository attachmentRepository;

    /**
     *
     * посылает сообщение в лс выбранному другу пользователя
     */
    @Transactional
    public void sendMessageInDialog(CreateDialogMessageDto createMessageDto, List<MultipartFile> files) {
        if(files!=null){
            if (files.size()> maxFileCount) {
                throw CommonException.builder().message("Превышен лимит прикрепленных файлов").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        if(checkBan(createMessageDto.getUserId())){
            throw CommonException.builder().message("Этот пользователь добавил вас в черный список").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        if(!checkFriends(createMessageDto.getUserId())){
            throw CommonException.builder().message("Вы не можете писать не своему другу").httpStatus(HttpStatus.BAD_REQUEST).build();
        }

        Chat chat = userChatRepository.findAllByUserChatOptions(createMessageDto.getUserId(), jwtService.getCurrentUserId());
        if (chat == null) {
            chat = new Chat(
                    UUID.randomUUID(),
                    Type.Dialog,
                    getUserName(createMessageDto.getUserId()),
                    null,
                    LocalDate.now(clock),
                    null,
                    new ArrayList<>()
            );
            chat=chatRepository.save(chat);
            UserChat mainUserChat = new UserChat(
                    UUID.randomUUID(),
                    createMessageDto.getUserId(),
                    chat.getId()
            );
            UserChat secondUserChat = new UserChat(
                    UUID.randomUUID(),
                    jwtService.getCurrentUserId(),
                    chat.getId()
            );
            userChatRepository.save(mainUserChat);
            userChatRepository.save(secondUserChat);
        }
        List<Message> messages = chat.getMessage();
        Message message = chatMapper.toEntity(createMessageDto);
        message = messageRepository.save(message);
        message.setSendDate(LocalDate.now(clock));
        message.setChatId(chat.getId());
        message.setSenderId(jwtService.getCurrentUserId());
        if(files!=null) {
            if (files.size() != 0) {
                for (MultipartFile file : files) {
                    if(!file.isEmpty()){
                        String id = saveFile(file);
                        Attachment attachment = new Attachment(
                            UUID.randomUUID(),
                            message.getId(),
                            UUID.fromString(id),
                            file.getOriginalFilename(),
                            chat.getId()
                            );
                    attachment = attachmentRepository.save(attachment);
                    message.getAttachments().add(attachment);
                }
            }
            }
        }
        message = messageRepository.save(message);
        messages.add(message);
        chat.setMessage(messages);
        chatRepository.save(chat);
        sendByStreamBridge(new CreateNotifyDTO(
                createMessageDto.getUserId(),
                NewMessage,
                message.getMessageText()
        ));
    }

    /**
     *метод для посылания сообщений в чат
     */
    @Transactional
    public void sendMessageInChat(UUID id,CreateChatMessageDto messageDto, List<MultipartFile> files){
        if(files!=null){
            if (files.size()> maxFileCount) {
                throw CommonException.builder().message("Превышен лимит прикрепленных файлов").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        Message message = chatMapper.toEntity(messageDto);
        Chat chat = chatRepository.findById(id).orElse(null);
        if(chat ==null){
            throw CommonException.builder().message("Чат не найден").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        if(userInChat(jwtService.getCurrentUserId(),chat.getId())){
            throw CommonException.builder().message("Вы не состоите в этом чате").httpStatus(HttpStatus.FORBIDDEN).build();
        }
        message.setSendDate(LocalDate.now(clock));
        message.setSenderId(jwtService.getCurrentUserId());
        message=messageRepository.save(message);
        message.setChatId(chat.getId());
        List<Message> messages = chat.getMessage();
        if(files!=null) {
                for (MultipartFile file : files
                ) {
                    if(!file.isEmpty()){
                    Attachment attachment = new Attachment(
                            UUID.randomUUID(),
                            chat.getId(),
                            UUID.fromString(saveFile(file)),
                            file.getOriginalFilename(),
                            chat.getId()
                    );
                    attachment = attachmentRepository.save(attachment);
                    message.getAttachments().add(attachment);
                    }
                }

        }
        message=messageRepository.save(message);
        messages.add(message);
        chat.setMessage(messages);
        chatRepository.save(chat);
    }

    /**
     *
     * метод для получения информации о диалоге или чате
     */
    @Transactional(readOnly = true)
    public ChatInfoDto getChatInfo(UUID id){
        Chat chat = chatRepository.findById(id).orElse(null);
        if(chat == null){
            throw new RuntimeException("Нет чата");
        }
        if(chat.getType() == Type.Dialog){
            String FIO = getUserName(userChatRepository.findUsersByDialogOptions(jwtService.getCurrentUserId(),id).getUserId());
            chat.setName(FIO);
        }
        return chatMapper.toInfoDto(chat);
    }

    /**
     *
     * метод для создания чата
     */
    @Transactional
    public void createChat(CreateChatDto createUpdateChatDto, MultipartFile file){
        if(file==null || file.isEmpty()){
            throw CommonException.builder().message("Необходимо выбрать автарку чата").httpStatus(HttpStatus.BAD_REQUEST).build();
        }
        String avatarId = saveFile(file);
        Chat chat = chatMapper.toChatEntity(createUpdateChatDto);
        chat.setDate(LocalDate.now(clock));
        chat.setAdmin(jwtService.getCurrentUserId());
        chat.setAvatar(UUID.fromString(avatarId));
        chat.setType(Type.Chat);
        chat = chatRepository.save(chat);
        List<UUID> users = createUpdateChatDto.getUsers();
        for (UUID id:users
             ) {
            if(checkFriends(id)) {
                if(checkBan(id)) {
                    userChatRepository.save(new UserChat(
                            UUID.randomUUID(),
                            id,
                            chat.getId()));
                }
                else {
                    throw CommonException.builder().message("Пользователь добавил вас в черный список " + id).httpStatus(HttpStatus.BAD_REQUEST).build();
                }
            }
            else {
                throw CommonException.builder().message("Вы не можете добавить не своего друга "+id).httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        userChatRepository.save(new UserChat(
                UUID.randomUUID(),
                jwtService.getCurrentUserId(),
                chat.getId()));
        chatRepository.save(chat);
    }

    /**
     *метод для обновления информации в чате
     */
    @Transactional
    public void updateChat(UUID chatId,UpdateChatDto updateChatDto, MultipartFile file) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        String avatar = null;
        if (file != null) {
            if (!file.isEmpty()) {
                avatar = saveFile(file);
            }
        }
        if(chat==null){
            throw CommonException.builder().message("Чата не существует").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        if(userInChat(jwtService.getCurrentUserId(),chat.getId())){
            throw CommonException.builder().message("Вы не состоите в этом чате").httpStatus(HttpStatus.FORBIDDEN).build();
        }
        chat.setName(updateChatDto.getName().length()!=0 ? updateChatDto.getName(): chat.getName());
        chat.setAvatar(avatar!=null ? UUID.fromString(avatar): chat.getAvatar());
        List<UUID> users = updateChatDto.getUsers();
        for (UUID id:users
        ) {
            if(checkFriends(id)) {
                if (checkBan(id)) {
                    if (userInChat(id, chatId)) {
                        userChatRepository.save(new UserChat(
                                UUID.randomUUID(),
                                id,
                                chat.getId()));
                    }
                 else {
                    throw CommonException.builder().message("Пользователь уже в этом чате "+id).httpStatus(HttpStatus.BAD_REQUEST).build();
                }
                }
                else {
                throw CommonException.builder().message("Пользователь добавил вас в черный список "+id).httpStatus(HttpStatus.BAD_REQUEST).build();}

            }else {
                throw CommonException.builder().message("Вы не можете добавить не своего друга").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
        }
        chatRepository.save(chat);
    }

    /**
     *метод для получения сообщений из чата
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getMessages (UUID id){
        Chat chat =chatRepository.findById(id).orElse(null);
        if(chat==null){
            throw CommonException.builder().message("Чата не существует").httpStatus(HttpStatus.NOT_FOUND).build();
        }
        if(userInChat(jwtService.getCurrentUserId(),chat.getId())){
            throw CommonException.builder().message("Вы не состоите в этом чате").httpStatus(HttpStatus.FORBIDDEN).build();
        }
        List<Message> sendMessage = chat.getMessage();
        Comparator<MessageDto> comparator = Comparator.comparing(MessageDto::getSendDate).reversed();
        List<MessageDto> messages = new ArrayList<>();
        for (Message message: sendMessage
             ) {
            messages.add(new MessageDto(
                    message.getId(),
                    message.getSendDate(),
                    message.getMessageText(),
                    getUserName(message.getSenderId()),
                    getAvatar(message.getSenderId()),
                    message.getAttachments().stream().map(AttachmentDto::toAttacmentDto).collect(Collectors.toList())
            ));
        }
        messages.sort(comparator);
        return messages;

    }

    /**
     *получение списка чатов
     */
    @Transactional(readOnly = true)
    public List<ChatDto> getChats (PageDto pageDto) {
        int startIndex = (pageDto.getPage() - 1) *  pageDto.getSize();
        Set<Chat> chats;
        if(pageDto.getChatName().isEmpty()){
            chats = userChatRepository.findChatsOption(jwtService.getCurrentUserId());
        }
        else {
            chats = userChatRepository.findChatsByUserAndSearch(jwtService.getCurrentUserId(),pageDto.getChatName());

        }
        List<ChatDto>chatDTOs = chats.stream().map(this::toChatDto).toList();
        int endIndex = Math.min(startIndex +  pageDto.getSize(), chats.size());
        if(endIndex<startIndex){
            return new ArrayList<>();
        }
        return chatDTOs.subList(startIndex, endIndex);
    }

    /**
     *поиск сообщений по заданным критериям
     */
    @Transactional(readOnly = true)
    public List<FindedMessagesDto> findMessages(FindMessageDto findMessageDto){
        List<Message> messages = userChatRepository.findMessageOption(jwtService.getCurrentUserId(),findMessageDto.getSearchText());
        messages.addAll(userChatRepository.findAttachmentOption(jwtService.getCurrentUserId(),findMessageDto.getSearchText()));
        Comparator<Message> comparator = Comparator.comparing(Message::getSendDate).reversed();
        List<FindedMessagesDto> messagesDTo = new ArrayList<>();
        messages.sort(comparator);
        for (Message message: messages
        ) {
            List<Attachment> attachment = message.getAttachments();
            boolean isAttachment = attachment.size() != 0;
            Chat chat = chatRepository.findById(message.getChatId()).orElse(null);
            assert chat != null;
            messagesDTo.add(new FindedMessagesDto(
                    message.getChatId(),
                    chat.getName(),
                    message.getMessageText(),
                    isAttachment,
                    message.getSendDate(),
                    attachment.stream().map(Attachment::getFileName).collect(Collectors.toList())
            ));
        }
        return messagesDTo;
    }

    /**
     *
     *метод для посылания запрос для сохранения файла
     */
    private String saveFile(MultipartFile file) {
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        Resource invoicesResource = file.getResource();
        LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", invoicesResource);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parts, httpHeaders);
        try {
            ResponseEntity<String> response = template.postForEntity(securityProps.getIntegrations().getUrl()[1],httpEntity,String.class );
            return response.getBody();
        }
        catch (HttpClientErrorException e){
            throw CommonException.builder().message(e.getMessage()).httpStatus(e.getStatusCode()).build();
        }
    }

    /**
     *метод для проверки находится ли человек в бане у другого
     */
    private Boolean checkBan (UUID id){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("API_KEY", securityProps.getIntegrations().getApiKey());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        CheckBanDto body = new CheckBanDto(jwtService.getCurrentUserId(),id);
        HttpEntity<CheckBanDto> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Boolean> response = template.postForEntity(securityProps.getIntegrations().getUrl()[3],entity,Boolean.class );
            return response.getBody();
        }
        catch (HttpClientErrorException e){
            throw CommonException.builder().message(e.getMessage()).httpStatus(e.getStatusCode()).build();
        }
    }
    private String getUserName(UUID id){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        URI uri = UriComponentsBuilder.fromUriString(securityProps.getIntegrations().getUrl()[0]).build(id);
        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .header("API_KEY", securityProps.getIntegrations().getApiKey())
                .build();
        ResponseEntity<String>  response = template.exchange(requestEntity, String.class );
        return response.getBody();
    }
    private UUID getAvatar(UUID id){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        URI uri = UriComponentsBuilder.fromUriString(securityProps.getIntegrations().getUrl()[5]).build(id);
        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .header("API_KEY", securityProps.getIntegrations().getApiKey())
                .build();
        ResponseEntity<UUID>  response = template.exchange(requestEntity, UUID.class );
        return response.getBody();
    }
    private Boolean checkFriends(UUID id){
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("API_KEY", securityProps.getIntegrations().getApiKey());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        CheckFriendDto body = new CheckFriendDto(jwtService.getCurrentUserId(),id);
        HttpEntity<CheckFriendDto> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Boolean> response = template.postForEntity(securityProps.getIntegrations().getUrl()[4],entity,Boolean.class );
            return response.getBody();
        }
        catch (HttpClientErrorException e){
            throw CommonException.builder().message(e.getMessage()).httpStatus(e.getStatusCode()).build();
        }
    }
    private Boolean userInChat(UUID userId, UUID chatId){
        List<UUID> users = userChatRepository.findByChatIdOptions(chatId);
        for (UUID user:users
             ) {
            if(user.equals(userId)){
                return false;
            }
        }
        return true;
    }
    private ChatDto toChatDto(Chat chat){
        List<Message> sendMessage = chat.getMessage();
        Comparator<Message> comparator = Comparator.comparing(Message::getSendDate).reversed();
        sendMessage.sort(comparator);
        Message lastMessage =null;
        boolean attachment = false;
        if(sendMessage.size()!=0) {
            lastMessage= sendMessage.get(sendMessage.size() - 1);
            if(lastMessage.getAttachments().size()!=0){
                attachment=true;
            }
        }
        if(chat.getType()==Type.Dialog){
            chat.setName(getUserName(userChatRepository.findUsersByDialogOptions(jwtService.getCurrentUserId(),chat.getId()).getUserId()));
        }
        return new ChatDto(
                chat.getId(),
                chat.getName(),
                lastMessage!=null?lastMessage.getMessageText():null,
                attachment,
                lastMessage!=null?lastMessage.getSendDate():null,
                lastMessage!=null?lastMessage.getSenderId():null
        );
    }
    private void sendByStreamBridge(CreateNotifyDTO notifyDTO) {
        streamBridge.send("NewNotify-out-0", notifyDTO);
    }


}
