package com.hguerrand.errand.controller;

import com.hguerrand.errand.service.ChatService;
import com.hguerrand.errand.vo.ChatMessageVO;
import com.hguerrand.errand.vo.ChatRoomVO;
import com.hguerrand.errand.vo.MemberVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    private MemberVO getLoginUser(HttpSession session) {
        return (MemberVO) session.getAttribute("loginMember");
    }

    /** detail.jsp의 채팅하기 → 방 만들고 입장 */
    @GetMapping("/start")
    public String start(@RequestParam("id") int errandId, HttpSession session) {
        MemberVO user = getLoginUser(session);
        System.out.println("[CHAT/start] errandId=" + errandId + ", user=" + (user==null?"null":user.getMemberId()));
        if (user == null) return "redirect:/auth/login";

        int roomId = chatService.startRoom(errandId, user.getMemberId());
        System.out.println("[CHAT/start] roomId=" + roomId);
        return "redirect:/chat/room?roomId=" + roomId;
    }

    /** 채팅 목록 */
    @GetMapping("/list")
    public String list(@RequestParam(value = "tab", defaultValue = "all") String tab,
                       HttpSession session,
                       Model model) {
        MemberVO user = getLoginUser(session);
        if (user == null) return "redirect:/auth/login";

        String normalized = "all";
        if ("requester".equals(tab) || "해주세요".equals(tab)) normalized = "requester";
        if ("helper".equals(tab) || "해줄게요".equals(tab)) normalized = "helper";

        List<ChatRoomVO> rooms = chatService.listRooms(user.getMemberId(), normalized);
        model.addAttribute("rooms", rooms);
        model.addAttribute("tab", normalized);
        return "chat/list";
    }

    /** 채팅방 화면 */
    @GetMapping("/room")
    public String room(@RequestParam("roomId") int roomId,
                       HttpSession session,
                       Model model) {
        MemberVO user = getLoginUser(session);
        if (user == null) return "redirect:/auth/login";

        ChatRoomVO header = chatService.roomHeader(roomId, user.getMemberId());
        List<ChatMessageVO> messages = chatService.recent(roomId);

        model.addAttribute("header", header);
        model.addAttribute("messages", messages);
        model.addAttribute("myId", user.getMemberId());
        return "chat/room";
    }

    /** 폴링 */
    @GetMapping("/messages")
    @ResponseBody
    public Map<String, Object> messages(@RequestParam("roomId") int roomId,
                                        @RequestParam(value = "afterId", defaultValue = "0") int afterId,
                                        HttpSession session) {
        MemberVO user = getLoginUser(session);
        Map<String, Object> res = new HashMap<>();
        if (user == null) {
            res.put("ok", false);
            res.put("reason", "login_required");
            res.put("messages", java.util.Collections.emptyList());
            res.put("lastId", afterId);
            return res;
        }

        List<ChatMessageVO> list = (afterId <= 0) ? chatService.recent(roomId) : chatService.after(roomId, afterId);
        int lastId = list.isEmpty() ? afterId : list.get(list.size() - 1).getMessageId();

        res.put("ok", true);
        res.put("messages", list);
        res.put("lastId", lastId);
        return res;
    }

    /** 전송 */
    @PostMapping("/send")
    @ResponseBody
    public Map<String, Object> send(@RequestParam("roomId") int roomId,
                                    @RequestParam("content") String content,
                                    HttpSession session) {
        MemberVO user = getLoginUser(session);
        Map<String, Object> res = new HashMap<>();
        if (user == null) {
            res.put("ok", false);
            res.put("reason", "login_required");
            return res;
        }

        chatService.send(roomId, user.getMemberId(), content);
        res.put("ok", true);
        return res;
    }

}