package webserver.mid.domain.vote.dto.req;

import java.util.List;

public record CreateVoteRequestDto(
        String title,
        List<String> items
) {
}
