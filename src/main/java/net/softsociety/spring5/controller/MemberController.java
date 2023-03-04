package net.softsociety.spring5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import net.softsociety.spring5.domain.Member;
import net.softsociety.spring5.service.MemberService;

/**
 * 회원 관련 처리 콘트롤러
 */
@Slf4j
@RequestMapping("member")
@Controller
public class MemberController {

	@Autowired
	MemberService service;
	
	/**
	 * 회원 가입 폼으로 이동
	 * @return 회원가입 양식 HTML
	 */
	@GetMapping("join")
	public String join() {
		
		return "memberView/joinForm";
	}
	
	@PostMapping("join")
	public String join(Member member) {
		log.debug("가입데이터 : {}", member);
		service.insert(member);
		return "redirect:/";
	}
	
	@GetMapping("idcheck")
	public String idcheck() {
		return "memberView/idcheck";
	}
	
	@PostMapping("idcheck")
	public String idcheck(String id, Model model) {
		log.debug("검색할 아이디 : {}", id);
		
		boolean res = service.idcheck(id);
		
		model.addAttribute("searchId", id);
		model.addAttribute("result", res);
		
		return "memberView/idcheck";
	}
	
	/**
	 * 로그인 폼으로 이동
	 * @return
	 */
	@GetMapping("loginForm")
	public String loginForm() {
		return "memberView/loginForm";
	}
	
	/**
	 * 개인정보 수정 폼으로 이동
	 */
	@GetMapping("mypage")
	public String mypage (@AuthenticationPrincipal UserDetails user, Model model){
		log.debug("인증정보 : {}", user.getUsername());
		//DB에서 현재 사용자 정보 읽어서 Member 객체로 받음
		//Model에 Member객체 담기
		//수정폼으로 이동
		Member member = service.getMember(user.getUsername());
		model.addAttribute("member", member);
		return "memberView/mypageForm";
	}
	
	/**
	 * 개인정보 수정 처리
	 */
	@PostMapping("mypage")
	public String mypage(Member member, @AuthenticationPrincipal UserDetails user) {
		//수정폼에서 사용자가 입력한 정보를 member로 전달받음
		//이름, 전화번호, 이메일, 주소
		//로그인한 ID를 읽어서 member객체에 추가저장
		//서비스로 전달하여 DB의 내용 수정
		member.setMemberid(user.getUsername());
		service.update(member);
		
		return "redirect:/";
	}
	
	

}
