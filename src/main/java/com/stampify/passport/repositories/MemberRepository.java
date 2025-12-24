package com.stampify.passport.repositories;
import com.stampify.passport.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByOrganizationId(Long orgId);
    Optional<Member> findByOrganizationIdAndMembershipNumber(Long orgId, String membershipNumber);
}