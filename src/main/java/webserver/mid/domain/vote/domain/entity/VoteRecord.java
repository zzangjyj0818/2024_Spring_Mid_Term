package webserver.mid.domain.vote.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import webserver.mid.domain.member.domain.entity.Member;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "vote_record")
public class VoteRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_item_id", nullable = false)
    private VoteItem voteItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    public void updateVoteItem(VoteItem voteItem) {
        this.voteItem = voteItem;
    }

    public void deleteAll() {
        this.vote = null;
        this.voteItem = null;
        this.member = null;
    }
}
