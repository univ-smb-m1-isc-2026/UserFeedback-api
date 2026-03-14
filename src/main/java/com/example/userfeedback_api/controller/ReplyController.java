package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.CreateReplyRequest;
import com.example.userfeedback_api.entity.Reply;
import com.example.userfeedback_api.service.ReplyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping
    public List<Reply> getAllReplies() {
        return replyService.getAllReplies();
    }

    @PostMapping
    public Reply createReply(@RequestBody CreateReplyRequest request) {
        return replyService.createReply(
                request.getContent(),
                request.getVisibility(),
                request.getAuthorId(),
                request.getPostId()
        );
    }
}