package webserver.mid.domain.member.dto.req;

public record MemberRequestDto(
        String account,
        String password
) {
}
