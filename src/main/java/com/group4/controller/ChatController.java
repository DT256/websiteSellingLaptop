package com.group4.controller;


import com.group4.entity.ChatEntity;
import com.group4.entity.UserEntity;
import com.group4.repository.ChatRepository;
import com.group4.service.IChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    @Autowired
    private IChatService chatService;

    @GetMapping("/")
    public String homePage(HttpSession session, Model model) {
        // Kiểm tra xem khách đã có ID tạm trong session chưa
        String guestId = (String) session.getAttribute("GID");
        // Nếu chưa có ID, tạo ID tạm và lưu vào session
        if (guestId == null) {
            guestId = generateRandomNumberString(10);
            session.setAttribute("GID", guestId);
        }

        // Gửi ID tạm vào model để hiển thị lên trang
        model.addAttribute("GID", guestId);
        return "Mainhome"; // trả về view home (home.html hoặc home.jsp)
    }

    private String generateRandomNumberString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Thêm một chữ số ngẫu nhiên từ 0 đến 9
        }
        return sb.toString();
    }

    @GetMapping("/getCustomerList")
    public ResponseEntity<List<Map<String, Object>>> getCustomerList() {
        List<Map<String, Object>> customers = chatService.findDistinctSendersByReceiverId(); // receiverID = 1 (cố định hoặc từ token)
        return ResponseEntity.ok(customers);
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatEntity sendMessage(ChatEntity chatEntity) {
        Long senderID = chatEntity.getSenderID(); // Giả sử có cách lấy ID người gửi
        Long receiverID = chatEntity.getReceiverID();

        boolean isCustomerSendingToAdmin = isCustomer(senderID) && isAdmin(receiverID);
        boolean isAdminSendingToCustomer = isAdmin(senderID) && isCustomer(receiverID);

        if (!isCustomerSendingToAdmin && !isAdminSendingToCustomer) {
            throw new IllegalArgumentException("Quyền truy cập không hợp lệ");
        }

        chatEntity.setSentTime(new Date());
        chatService.save(chatEntity);
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
    public List<Map<String, Object>> loadMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        return chatService.findAll().stream()
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

