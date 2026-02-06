package com.sparkLab.study.security.auth.dto;

public record SignInReq(
        String accountId,
        String password
) {}

// record의 활용
// 적합
// 순수 데이터 전달 객체
// 요청(Request) DTO
// 응답(Response) DTO
// 외부 API 계약 객체
// 불변이어야 하는 값 묶음
// 부적합
// setter가 필요한 경우
// 상태 변경 로직이 있는 경우
// JPA Entity
// 상속이 필요한 경우
// 프록시가 필요한 경우 (Hibernate)
// 요청·응답 DTO는 record로 설계하고,

