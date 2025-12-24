package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    public Member createMember(Member member) {
        return null;
    }

    public Optional<Member> getById(Long id) {
        return Optional.empty();
    }

    public List<Member> getByOrganization(Long orgId) {
        return List.of();
    }

    public Optional<Member> getByOrgAndMembershipNumber(Long orgId, String membershipNumber) {
        return Optional.empty();
    }

    public Member updateMember(Member member) {
        return null;
    }

    public void deleteMember(Long id) {
    }
}
