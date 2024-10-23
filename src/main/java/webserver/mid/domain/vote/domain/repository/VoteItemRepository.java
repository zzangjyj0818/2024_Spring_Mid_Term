package webserver.mid.domain.vote.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webserver.mid.domain.vote.domain.entity.Vote;
import webserver.mid.domain.vote.domain.entity.VoteItem;

import java.util.List;

@Repository
public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
    @Query("SELECT vi FROM VoteItem vi WHERE vi.vote = :vote")
    List<VoteItem> findByVote(@Param("vote") Vote vote);
}