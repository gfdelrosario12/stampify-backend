package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MemberService {
    Member createMember(Member member);
    Optional<Member> getById(Long id);
    List<Member> getByOrganization(Long orgId);
    Optional<Member> getByOrgAndMembershipNumber(Long orgId, String membershipNumber);
    Member updateMember(Member member);
    void deleteMember(Long id);
}
