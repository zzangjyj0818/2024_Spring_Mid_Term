package webserver.mid.domain.member.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import webserver.mid.domain.member.application.MemberService;
import webserver.mid.domain.member.dto.req.MemberRequestDto;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(MemberRequestDto memberRequestDto, HttpSession session) {
        return memberService.login(memberRequestDto.account(), memberRequestDto.password(), session) ?
                "redirect:/" : "loginError";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(MemberRequestDto memberRequestDto, HttpSession session) {
        return memberService.saveMember(memberRequestDto.account(), memberRequestDto.password(), session) ? "redirect:/" : "signupError";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            memberService.logout(session);
        }
        return "redirect:/login";
    }
}
