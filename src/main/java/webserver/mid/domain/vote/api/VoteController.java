package webserver.mid.domain.vote.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webserver.mid.domain.vote.application.VoteService;
import webserver.mid.domain.vote.dto.req.CreateVoteRequestDto;

@Slf4j
@Controller
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        voteService.getVotes(model, session);
        return "main";
    }

    @PostMapping("/")
    public String searchVotes(@RequestParam("search") String search, Model model, HttpSession session) {
        voteService.searchVotes(search, model, session);
        return "main";
    }

    @GetMapping("/create")
    public String create() {
        return "createVote";
    }

    @GetMapping("/vote/{id}")
    public String vote(@PathVariable Long id, Model model, HttpSession httpSession) {
        voteService.getVote(id, model, httpSession);
        return "vote";
    }

    @PostMapping("/vote/{id}")
    public String vote(@PathVariable Long id, HttpSession httpSession, @RequestParam("itemId") Long itemId) {
        voteService.vote(id, httpSession, itemId);
        return "redirect:/";
    }

    @DeleteMapping("/vote/{id}")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public String deleteVote(@PathVariable Long id) {
        voteService.deleteVote(id);
        return "redirect:/";
    }

    @GetMapping("/result/{id}")
    public String result(@PathVariable Long id, Model model) {
        model.addAttribute("voteResult", voteService.getVoteResult(id));
        return "result";
    }

    @PostMapping("/create")
    public String createVote(HttpSession httpSession, CreateVoteRequestDto createVoteRequestDto) {
        voteService.createVote(httpSession, createVoteRequestDto);
        return "redirect:/";
    }
}


