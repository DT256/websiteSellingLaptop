package com.group4.controller;


import com.group4.entity.ChatEntity;
import com.group4.entity.UserEntity;
import com.group4.model.UserModel;
import com.group4.repository.ChatRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;


    @GetMapping("/getCustomerList")
    public ResponseEntity<List<UserEntity>> getCustomerList() {
        try {
            // Truy vấn các senderId đã nhắn tin cho receiverId được truyền vào
            List<UserEntity> customerList = chatRepository.findDistinctSendersByReceiverId();
            return ResponseEntity.ok(customerList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching customer list", e);
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatEntity sendMessageToHomepage(ChatEntity chatEntity) {
        Long senderID = chatEntity.getSenderID(); // Giả sử có cách lấy ID người gửi
        Long receiverID = chatEntity.getReceiverID();

        boolean isCustomerSendingToAdmin = isCustomer(senderID) && isAdmin(receiverID);
        boolean isAdminSendingToCustomer = isAdmin(senderID) && isCustomer(receiverID);

        if (!isCustomerSendingToAdmin && !isAdminSendingToCustomer) {
            throw new IllegalArgumentException("Quyền truy cập không hợp lệ");
        }

        chatEntity.setSentTime(new Date());
        chatRepository.save(chatEntity);
        return chatEntity;
    }

    // Ví dụ các phương thức kiểm tra vai trò người dùng
    private boolean isCustomer(Long userID) {
        return userID != 1;
    }

    private boolean isAdmin(Long userID) {
        return userID == 1;
    }


    @GetMapping("/getMessages")
    @ResponseBody
    public List<Map<String, Object>> getMessagesBetweenUsers(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        return chatRepository.findAll().stream()
                .filter(row -> (row.getSenderID().equals(senderId) && row.getReceiverID().equals(receiverId)) ||
                        (row.getSenderID().equals(receiverId) && row.getReceiverID().equals(senderId)))
                .map(row -> {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("id", row.getChatID());
                    messageMap.put("contentMessage", row.getContentMessage());
                    messageMap.put("timestamp", row.getSentTime());
                    messageMap.put("senderID", row.getSenderID());
                    messageMap.put("receiverID", row.getReceiverID());
                    return messageMap;
                })
                .collect(Collectors.toList());
    }




}