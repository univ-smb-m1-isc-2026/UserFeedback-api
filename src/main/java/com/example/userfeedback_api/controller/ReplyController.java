package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.CreateReplyRequest;
import com.example.userfeedback_api.dto.DeleteReplyRequest;
import com.example.userfeedback_api.dto.UpdateReplyRequest;
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
    public List<Reply> getVisibleReplies(@RequestParam Long userId, @RequestParam Long postId) {
        return replyService.getVisibleReplies(userId, postId);
    }

    @PostMapping
    public Reply createReply(@RequestBody CreateReplyRequest request) {
        return replyService.createReply(
                request.getContent(),
                request.isPublic(),
                request.getGroupId(),
                request.getAuthorId(),
                request.getPostId()
        );
    }

    @PutMapping("/{replyId}")
    public Reply updateReply(@PathVariable Long replyId, @RequestBody UpdateReplyRequest request) {
        return replyService.updateReply(
                replyId,
                request.getContent(),
                request.getUserId()
        );
    }

    @PutMapping("/{replyId}/delete")
    public Reply deleteReply(@PathVariable Long replyId, @RequestBody DeleteReplyRequest request) {
        return replyService.deleteReply(replyId, request.getUserId());
    }
}