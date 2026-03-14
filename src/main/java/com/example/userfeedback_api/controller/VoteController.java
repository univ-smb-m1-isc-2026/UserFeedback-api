package com.example.userfeedback_api.controller;

import com.example.userfeedback_api.dto.CreateVoteRequest;
import com.example.userfeedback_api.entity.Vote;
import com.example.userfeedback_api.service.VoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping
    public List<Vote> getAllVotes() {
        return voteService.getAllVotes();
    }

    @PostMapping
    public Vote createVote(@RequestBody CreateVoteRequest request) {
        return voteService.createVote(
                request.getValue(),
                request.getUserId(),
                request.getPostId(),
                request.getReplyId()
        );
    }
}