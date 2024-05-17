package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = Member.builder()
                .username("memberA")
                .build();

        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = Member.builder()
                .username("member1")
                .build();
        Member member2 = Member.builder()
                .username("member2")
                .build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();
        Member m2 = Member.builder()
                .username("AAA")
                .age(20)
                .build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy() {
        List<Member> top3SortBy = memberRepository.findTop3SortBy();
    }

    @Test
    public void testQuery() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();
        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();
        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();
        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();
        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = Member.builder()
                .username("AAA")
                .age(10)
                .build();
        Member m2 = Member.builder()
                .username("BBB")
                .age(20)
                .build();
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember = " + findMember);
    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(Member.builder().username("member1").age(10).build());
        memberRepository.save(Member.builder().username("member2").age(10).build());
        memberRepository.save(Member.builder().username("member3").age(10).build());
        memberRepository.save(Member.builder().username("member4").age(10).build());
        memberRepository.save(Member.builder().username("member5").age(10).build());

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> MemberDto.builder()
                .member(member)
                .build());

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(Member.builder().username("member1").age(10).build());
        memberRepository.save(Member.builder().username("member2").age(19).build());
        memberRepository.save(Member.builder().username("member3").age(20).build());
        memberRepository.save(Member.builder().username("member4").age(21).build());
        memberRepository.save(Member.builder().username("member5").age(40).build());

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = Team.builder().name("teamA").build();
        Team teamB = Team.builder().name("teamB").build();
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = Member.builder().username("member1").age(10).team(teamA).build();
        Member member2 = Member.builder().username("member2").age(10).team(teamB).build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        //then
    }

    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(Member.builder().username("member1").age(10).build());

        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.changeName("member2");

//        findMember.changeName("member2");

        em.flush();

        //then
    }

    @Test
    public void lock() throws Exception {
        //given
        memberRepository.save(Member.builder().username("member1").age(10).build());

        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

        //then
    }

    @Test
    public void callCustom() throws Exception {
        //given
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void queryByExample() throws Exception {
        //given
        Team teamA = Team.builder().name("teamA").build();
        em.persist(teamA);

        Member m1 = Member.builder().username("m1").age(0).team(teamA).build();
        Member m2 = Member.builder().username("m2").age(0).team(teamA).build();
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Team team = Team.builder().name("teamA").build();
        Member member = Member.builder().username("m1").team(team).build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        //then
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() throws Exception {
        //given
        Team teamA = Team.builder().name("teamA").build();
        em.persist(teamA);

        Member m1 = Member.builder().username("m1").age(0).team(teamA).build();
        Member m2 = Member.builder().username("m2").age(0).team(teamA).build();
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

//        for (UsernameOnlyDto usernameOnly : result) {
//            System.out.println("usernameOnly = " + usernameOnly.getUsername());
//        }

        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);
        }

    }

    @Test
    public void nativeQuery() throws Exception {
        //given
        Team teamA = Team.builder().name("teamA").build();
        em.persist(teamA);

        Member m1 = Member.builder().username("m1").age(0).team(teamA).build();
        Member m2 = Member.builder().username("m2").age(0).team(teamA).build();
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }

}