package net.softsociety.spring5.service;

import net.softsociety.spring5.domain.Member;

/**
 * 회원정보 관련 처리
 */
public interface MemberService {
	/**
	 * 회원정보 저장 (가입)
	 * @param member 가입양식에서 전달된 회원정보
	 * @return 저장된 개수
	 */
	public int insert(Member member);
	
	/**
	 * 아이디 존재 확인
	 * @param id 찾을 아이디
	 * @return	해당 아이디 존재 여부 (있으면 true)
	 */
	public boolean idcheck(String id);
	
	/**
	 * 아이디로 회원 정보 찾기
	 * @param id 검색할 아이디
	 * @return	해당 회원의 정보
	 */
	public Member getMember(String id);
	
	/**
	 * 회원정보 수정
	 * @param member 수정할 정보
	 * @return 처리한 행 개수
	 */
	public int update(Member member);
}
