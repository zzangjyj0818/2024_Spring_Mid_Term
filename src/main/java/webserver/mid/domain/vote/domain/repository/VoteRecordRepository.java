package webserver.mid.domain.vote.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webserver.mid.domain.member.domain.entity.Member;
import webserver.mid.domain.vote.domain.entity.Vote;
import webserver.mid.domain.vote.domain.entity.VoteItem;
import webserver.mid.domain.vote.domain.entity.VoteRecord;

import java.util.List;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query("SELECT vr FROM VoteRecord vr WHERE vr.member = :member AND vr.vote = :vote")
    VoteRecord findByMemberAndVote(@Param("member") Member currentMember, @Param("vote") Vote vote);

    @Query("SELECT vr FROM VoteRecord vr WHERE vr.vote = :vote")
    List<VoteRecord> findByVote(@Param("vote") Vote vote);

    @Query("SELECT COUNT(vr) > 0 FROM VoteRecord vr WHERE vr.member = :member AND vr.vote = :vote")
    boolean existsByMemberAndVote(@Param("member") Member currentMember, @Param("vote") Vote vote);

    @Query("SELECT COUNT(vr) FROM VoteRecord vr WHERE vr.voteItem = :item")
    int countByVoteItem(@Param("item") VoteItem item);
}