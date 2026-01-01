package com.stampify.passport.services;

import com.stampify.passport.models.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    public Optional<Member> getById(Long id) {
        return Optional.empty();
    }

    public List<Member> getByOrganization(Long orgId) {
        return List.of();
    }

    public Optional<Member> getByOrgAndMembershipNumber(Long orgId, String membershipNumber) {
        return Optional.empty();
    }

    public void deleteMember(Long id) {
    }
}
