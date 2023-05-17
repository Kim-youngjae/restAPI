package com.ll.rest_api.boundedContext.member.service;

import com.ll.rest_api.base.jwt.JwtProvider;
import com.ll.rest_api.boundedContext.member.repository.MemberRepository;
import com.ll.rest_api.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Member join(String username, String password, String email) {
        Member member = Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        memberRepository.save(member);

        return member;
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public String genAccessToken(String username, String password) {
        Member member = findByUsername(username).orElse(null); // 유저가 존재하는지

        if (member == null) return null; // 해당 유저가 없으면 null 리턴

        if (!passwordEncoder.matches(password, member.getPassword())) { // 유저가 존재한다면 pw 가 일치하는지
            return null;
        }

        return jwtProvider.genToken(member.toClaims(), 60 * 60 * 24 * 365);
    }
}
