package webserver.mid.domain.vote.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import webserver.mid.domain.member.domain.entity.Member;

@Entity
@Getter
@Builder
@NoArgsConstructor
@Table(name = "vote")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
    @Id
    @Column(name = "vote_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}

