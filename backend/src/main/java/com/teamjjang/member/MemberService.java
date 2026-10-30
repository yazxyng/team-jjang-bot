package com.teamjjang.member;

import com.teamjjang.project.ProjectService;
import com.teamjjang.common.ApiException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository members;
    private final ProjectService projects;
    public MemberService(MemberRepository members, ProjectService projects) {
        this.members = members; this.projects = projects;
    }
    public List<MemberDtos.View> list(Long projectId) {
        projects.require(projectId);
        return members.findByProject_IdOrderByIdAsc(projectId).stream().map(MemberDtos.View::from).toList();
    }
    @Transactional
    public MemberDtos.View create(Long projectId, MemberDtos.Create input) {
        return MemberDtos.View.from(members.save(new Member(projects.require(projectId), input.name().trim(), input.role().trim())));
    }

    @Transactional
    public MemberDtos.View update(Long projectId, Long memberId, MemberDtos.Update input) {
        Member member=members.findByIdAndProject_Id(memberId, projectId).orElse(null);
        if (member==null) {
            throw ApiException.notFound("팀원");
        }

        String name=input.name().trim();
        String role=input.role().trim();
        member.updateInfo(name, role);
        return MemberDtos.View.from(member);
    }
}
