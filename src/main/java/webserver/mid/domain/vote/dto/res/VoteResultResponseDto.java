package webserver.mid.domain.vote.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
public record VoteResultResponseDto(
        String title,
        List<VoteItemResponseDto> voteItems
) {
    @Getter
    @Setter
    public static class VoteItemResponseDto {
        private String name;
        private int voteCount;
    }
}
