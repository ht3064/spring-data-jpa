package study.datajpa.dto;

import lombok.Builder;
import lombok.Data;
import study.datajpa.entity.Member;

@Data
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    //    @Builder
//    public MemberDto(Long id, String username, String teamName) {
//        this.id = id;
//        this.username = username;
//        this.teamName = teamName;
//    }

    @Builder // Dto는 Entity를 파라미터로 받아도 됨. 이거 적극 활용하자!
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.teamName = member.getTeam().getName();
    }
}
