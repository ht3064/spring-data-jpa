package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    // http://localhost:8080/members?page=0&size=3&sort=username,desc 요청 파라미터 예시
    // page와 size 기본값 변경 가능 -> 글로벌 설정: application.yml
    // 글로벌 설정보단 @PageableDefault 어노테이션 사용 (이 설정이 우선권을 가짐)
    public Page<Member> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/members/dto")
    // api는 무조건 dto로 변환해서 반환해야 함
    public Page<MemberDto> lists(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

//    @PostConstruct
    public void init() {
        Team team = Team.builder().name("team").build();
        teamRepository.save(team);
        for (int i = 0; i < 100; i++) {
            memberRepository.save(Member.builder().username("user" + i).age(i).team(team).build());
        }
    }
}
