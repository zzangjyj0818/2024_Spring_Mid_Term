package webserver.mid.domain.vote.dto.res;

import lombok.Builder;

@Builder
public record VoteResponseDto(
        Long id,
        String title,
        boolean isVoted,
        boolean isOwner
) {
}
