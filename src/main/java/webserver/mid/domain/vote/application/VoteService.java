package webserver.mid.domain.vote.application;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import webserver.mid.domain.member.domain.entity.Member;
import webserver.mid.domain.member.domain.repository.MemberRepository;
import webserver.mid.domain.vote.domain.entity.Vote;
import webserver.mid.domain.vote.domain.entity.VoteItem;
import webserver.mid.domain.vote.domain.entity.VoteRecord;
import webserver.mid.domain.vote.domain.repository.VoteItemRepository;
import webserver.mid.domain.vote.domain.repository.VoteRecordRepository;
import webserver.mid.domain.vote.domain.repository.VoteRepository;
import webserver.mid.domain.vote.dto.req.CreateVoteRequestDto;
import webserver.mid.domain.vote.dto.res.VoteResponseDto;
import webserver.mid.domain.vote.dto.res.VoteResultResponseDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteRecordRepository voteRecordRepository;


    public void createVote(HttpSession httpSession, CreateVoteRequestDto createVoteRequestDto) {
        Member member = getMemberFromSession(httpSession);
        Vote vote = Vote.builder()
                .member(member)
                .title(createVoteRequestDto.title())
                .build();

        voteRepository.save(vote);
        saveVoteItems(createVoteRequestDto, vote);
    }

    @Cacheable("votes")
    @Transactional(readOnly = true)
    public void getVotes(Model model, HttpSession session) {
        Member member = getMemberFromSession(session);
        List<Vote> votes = voteRepository.findAll();
        List<VoteResponseDto> res = votes.stream()
                .map(vote -> createVoteResponseDto(vote, member))
                .collect(Collectors.toList());

        model.addAttribute("votes", res);
    }

    @Cacheable("votes")
    @Transactional(readOnly = true)
    public void searchVotes(String search, Model model, HttpSession session) {
        if (search.isEmpty()) {
            getVotes(model, session);
            return;
        }

        Member member = getMemberFromSession(session);
        List<Vote> votes = voteRepository.findByTitleContaining(search);
        List<VoteResponseDto> res = votes.stream()
                .map(vote -> createVoteResponseDto(vote, member))
                .collect(Collectors.toList());

        model.addAttribute("votes", res);
    }

    @Transactional
    public void deleteVote(Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElse(null);
        if (vote != null) {
            deleteVoteRecordsByVote(vote);
            deleteVoteItems(vote);
            voteRepository.delete(vote);
        }
    }

    @Transactional(readOnly = true)
    public void getVote(Long id, Model model, HttpSession httpSession) {
        Member member = getMemberFromSession(httpSession);
        Vote vote = voteRepository.findById(id).orElse(null);
        if (vote != null) {
            List<VoteItem> voteItems = voteItemRepository.findByVote(vote);
            VoteRecord existingRecord = voteRecordRepository.findByMemberAndVote(member, vote);
            List<Map<String, Object>> itemsWithSelection = getItemsWithSelection(voteItems, existingRecord);

            model.addAttribute("title", vote.getTitle());
            model.addAttribute("items", itemsWithSelection);
        }
    }

    @Cacheable("voteResults")
    @Transactional(readOnly = true)
    public VoteResultResponseDto getVoteResult(Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElse(null);
        if (vote != null) {
            List<VoteItem> voteItems = voteItemRepository.findByVote(vote);
            List<VoteResultResponseDto.VoteItemResponseDto> voteItemDTOs = getVoteItemResponseDtos(voteItems);

            return VoteResultResponseDto.builder()
                    .title(vote.getTitle())
                    .voteItems(voteItemDTOs)
                    .build();
        }
        return null;
    }

    @Transactional
    public void vote(Long id, HttpSession httpSession, Long itemId) {
        Member member = getMemberFromSession(httpSession);
        Vote vote = voteRepository.findById(id).orElse(null);
        if (vote != null) {
            VoteItem voteItem = getVoteItemById(vote, itemId);
            VoteRecord existingRecord = voteRecordRepository.findByMemberAndVote(member, vote);
            saveOrUpdateVoteRecord(member, vote, voteItem, existingRecord);
        }
    }

    private Member getMemberFromSession(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        return memberRepository.findById(memberId).orElse(null);
    }

    private VoteResponseDto createVoteResponseDto(Vote vote, Member member) {
        boolean exist = voteRecordRepository.existsByMemberAndVote(member, vote);
        boolean isOwner = vote.getMember().getMemberId().equals(member.getMemberId());

        return VoteResponseDto.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .isVoted(exist)
                .isOwner(isOwner)
                .build();
    }

    private void saveVoteItems(CreateVoteRequestDto createVoteRequestDto, Vote vote) {
        List<VoteItem> voteItems = createVoteRequestDto.items().stream()
                .map(item -> VoteItem.builder()
                        .vote(vote)
                        .item(item)
                        .build())
                .collect(Collectors.toList());
        voteItemRepository.saveAll(voteItems);
    }


    private void deleteVoteRecordsByVote(Vote vote) {
        List<VoteRecord> voteRecords = voteRecordRepository.findByVote(vote);
        voteRecordRepository.deleteAll(voteRecords);
    }

    private void deleteVoteItems(Vote vote) {
        List<VoteItem> voteItems = voteItemRepository.findByVote(vote);
        for (VoteItem item : voteItems) {
            deleteVoteRecordsByVoteItem(item);
            voteItemRepository.delete(item);
        }
    }

    private void deleteVoteRecordsByVoteItem(VoteItem voteItem) {
        List<VoteRecord> voteRecords = voteRecordRepository.findByVoteItem(voteItem);
        voteRecordRepository.deleteAll(voteRecords);
    }

    private List<Map<String, Object>> getItemsWithSelection(List<VoteItem> voteItems, VoteRecord existingRecord) {
        return voteItems.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("item", item.getItem());
            map.put("isSelected", existingRecord != null && existingRecord.getVoteItem().equals(item));
            return map;
        }).collect(Collectors.toList());
    }

    private VoteItem getVoteItemById(Vote vote, Long itemId) {
        return voteItemRepository.findByVote(vote).stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    private void saveOrUpdateVoteRecord(Member member, Vote vote, VoteItem voteItem, VoteRecord existingRecord) {
        if (existingRecord != null) {
            existingRecord.updateVoteItem(voteItem);
        } else {
            voteRecordRepository.save(
                    VoteRecord.builder()
                            .member(member)
                            .vote(vote)
                            .voteItem(voteItem)
                            .build()
            );
        }
    }

    private List<VoteResultResponseDto.VoteItemResponseDto> getVoteItemResponseDtos(List<VoteItem> voteItems) {
        List<VoteResultResponseDto.VoteItemResponseDto> voteItemDTOs = new ArrayList<>();
        for (VoteItem item : voteItems) {
            int voteCount = voteRecordRepository.countByVoteItem(item);
            VoteResultResponseDto.VoteItemResponseDto voteItemDTO = new VoteResultResponseDto.VoteItemResponseDto();
            voteItemDTO.setName(item.getItem());
            voteItemDTO.setVoteCount(voteCount);
            voteItemDTOs.add(voteItemDTO);
        }
        return voteItemDTOs;
    }
}