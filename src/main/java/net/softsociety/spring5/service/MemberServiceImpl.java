package net.softsociety.spring5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.softsociety.spring5.dao.MemberDAO;
import net.softsociety.spring5.domain.Member;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	MemberDAO dao;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Override
	public int insert(Member member) {
		//비밀번호 암호화
		String pw = encoder.encode(member.getMemberpw());
		member.setMemberpw(pw);		
		
		int n = dao.insert(member);
		return n;
	}

	@Override
	public boolean idcheck(String id) {
//		Member member = dao.select(id);
//		boolean res;
//		if (member == null) {
//			res = false;
//		}
//		else {
//			res = true;
//		}
//		return res;
		
		return dao.select(id) != null;
	}

	@Override
	public Member getMember(String id) {
		Member member = dao.select(id);
		return member;
	}

	@Override
	public int update(Member member) {
		//수정할 비밀번호 있으면 암호화
		if (member.getMemberpw() != null && member.getMemberpw().length() != 0) {
			String pw = encoder.encode(member.getMemberpw());
			member.setMemberpw(pw);
		}
		int n = dao.update(member);
		return n;
	}
	
	

}
