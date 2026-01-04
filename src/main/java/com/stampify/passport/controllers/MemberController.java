package com.stampify.passport.controllers;

import com.stampify.passport.dto.MemberDTO;
import com.stampify.passport.mappers.UserMapper;
import com.stampify.passport.models.Member;
import com.stampify.passport.repositories.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /* ================= GET ALL MEMBERS ================= */
    @GetMapping
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(UserMapper::toMemberDTO)
                .toList();
    }

    /* ================= GET MEMBER BY ID ================= */
    @GetMapping("/{id}")
    public MemberDTO getMemberById(@PathVariable Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return UserMapper.toMemberDTO(member);
    }
}
