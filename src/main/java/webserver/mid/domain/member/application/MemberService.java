package webserver.mid.domain.member.application;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webserver.mid.domain.member.domain.entity.Member;
import webserver.mid.domain.member.domain.repository.MemberRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public boolean saveMember(String account, String password, HttpSession session) {
        Optional<Member> existingMember = memberRepository.findByAccount(account);
        if (existingMember.isPresent()) {
            return false;
        }

        Member member = Member.builder()
                .account(account)
                .password(password)
                .build();

        memberRepository.save(member);

        Optional<Member> memberOptional = memberRepository.findByAccount(account);
        if (memberOptional.isPresent()) {
            Member m = memberOptional.get();
            session.setAttribute("memberId", m.getMemberId());
        }
        return true;
    }

    @Transactional
    public boolean login(String account, String password, HttpSession session) {
        Optional<Member> memberOptional = memberRepository.findByAccount(account);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (member.getPassword().equals(password)) {
                session.setAttribute("memberId", member.getMemberId());
                return true;
            }
        }

        return false;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
