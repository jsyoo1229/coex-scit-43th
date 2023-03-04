package net.softsociety.spring5.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;
import net.softsociety.spring5.service.BoardService;
import net.softsociety.spring5.util.FileService;
import net.softsociety.spring5.util.PageNavigator;

@Slf4j
@RequestMapping("board")
@Controller
public class BoardController {
	@Autowired
	BoardService service;
	
	//설정파일에 정의된 업로드할 경로를 읽어서 아래 변수에 대입
	@Value("${spring.servlet.multipart.location}")
	String uploadPath;
	
	//페이지당 글 수
	@Value("${user.board.page}")
	int countPerPage;
	
	//페이지 이동 링크 수
	@Value("${user.board.group}")
	int pagePerGroup;
	
	
	//글목록
	@GetMapping("list")
	public String list(
			@RequestParam(name="page", defaultValue = "1") int page
			, String type
			, String searchWord
			, Model model) {
		
		PageNavigator navi = 
			service.getPageNavigator(pagePerGroup, countPerPage, page, type, searchWord);
		
		ArrayList<Board> boardlist = service.list(
			navi.getStartRecord(), countPerPage, type, searchWord);
		
		model.addAttribute("boardlist", boardlist);
		model.addAttribute("navi", navi);
		model.addAttribute("type", type);
		model.addAttribute("searchWord", searchWord);
		
		return "boardView/list";
	}
	
	//글쓰기 폼
	@GetMapping("write")
	public String write() {
		return "boardView/writeForm";
	}

	//글 저장
	@PostMapping("write")
	public String write(
			Board board
			, @AuthenticationPrincipal UserDetails user
			, MultipartFile upload) {
		
		//첨부파일이 있으면 지정한 경로에 저장하고 파일명을 board객체에 추가
		if ( upload != null && !upload.isEmpty()) {
			String filename = FileService.saveFile(upload, uploadPath);
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(filename);
		}
		
		//로그인한 아이디 읽어서 board객체에 추가 
		board.setMemberid(user.getUsername());
		log.debug("저장할 글 정보 : ", board);
		
		//DB에 저장
		service.write(board);
		return "redirect:/board/list";
	}
	
	//글 읽기
	@GetMapping("read")
	public String read(
			@RequestParam(name="num", defaultValue="0") int num
			, Model model) {
		//본문글 정보
		Board board = service.read(num);
		model.addAttribute("board", board);
		//해당 글에 달린 리플 목록
		ArrayList<Reply> replylist = service.readReply(num);
		model.addAttribute("replylist", replylist);
		log.debug("{}글의 리플들 : {}", num, replylist);
		
		return "/boardView/read";
	}
	
	//첨부파일 다운로드
	@GetMapping("download")
	public String download(@RequestParam(name="num", defaultValue="0") int num
			, HttpServletResponse response) {
		Board board = service.read(num);
		if (board == null || board.getSavedfile() == null) {
			return "redirect:list";
		}
		String file = uploadPath + "/" + board.getSavedfile();
		FileInputStream in = null;
		ServletOutputStream out = null;
		try {
			//응답 정보의 헤더 세팅
			response.setHeader("Content-Disposition", 
				" attachment;filename="+ URLEncoder.encode(board.getOriginalfile(), "UTF-8"));
			
			in = new FileInputStream(file);
			out = response.getOutputStream();
			//파일 전송
			FileCopyUtils.copy(in, out);
			
			in.close();
			out.close();
		}
		catch (IOException e) {	//예외 메시지 출력
		}
		return "redirect:/";
	}
	
	//글 삭제
	@GetMapping("delete")
	public String delete(
			@RequestParam(name="num", defaultValue="0") int num
			, @AuthenticationPrincipal UserDetails user) {
//		글 읽기 화면에서 글번호가 전달됨
//		로그인한 사용자의 아이디를 읽음
		String id = user.getUsername();
//		글번호로 DB에서 글 내용을 읽음
		Board board = service.read(num);
//		해당번호의 글이 있는지 확인. 없으면 글목록으로
		if (board == null) return "redirect:list";
//		로그인한 본인의 글이 맞는지 확인. 아니면 글목록으로
		if (!board.getMemberid().equals(id)) return "redirect:list";
//		첨부된 파일이 있으면 파일삭제
		if (board.getSavedfile() != null) {
			FileService.deleteFile(uploadPath + "/" + board.getSavedfile());
		}
//		실제 글 DB에서 삭제
		service.delete(board);
//		글 목록으로 리다이렉트
		return "redirect:list";
	}
	
	//수정 폼으로 이동
	@GetMapping("update")
	public String update(int boardnum, Model model, @AuthenticationPrincipal UserDetails user) {
		//전달된 번호의 글정보 읽기
		Board board = service.read(boardnum);
		//본인글인지 확인. 아니면 글목록으로 이동.
		if (!board.getMemberid().equals(user.getUsername())) {
			return "redirect:list";
		}
		//글정보를 모델에 저장
		model.addAttribute("board", board);
		//수정폼 html로 포워딩	
		return "boardView/update.html";
	}
	//수정폼에서 보낸 내용 처리
	@PostMapping("update")
	public String update(Board board, @AuthenticationPrincipal UserDetails user, MultipartFile upload) {
		log.debug("저장할 글정보 : {}", board);
		log.debug("파일 정보: {}", upload);
		
		//전달된 board객체(글번호, 제목, 내용) 에 로그인한 아이디 추가 저장
		board.setMemberid(user.getUsername());
		Board oldBoard = null;
		String oldSavedfile = null;
		String savedfile = null;
		
		//첨부파일이 있는 경우 기존파일 삭제 후 새 파일 저장
		if (upload != null && !upload.isEmpty()) {
			oldBoard = service.read(board.getBoardnum());
			oldSavedfile = oldBoard == null ? null : oldBoard.getSavedfile();
			
			savedfile = FileService.saveFile(upload, uploadPath);
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
			log.debug("새파일:{}, 구파일:{}", savedfile, oldSavedfile);
		}
		
		int result = service.update(board);
		
		//글 수정 성공 and 첨부된 파일이 있는 경우 파일도 삭제
		if (result == 1 && savedfile != null) {
			FileService.deleteFile(uploadPath + "/" + oldSavedfile);
		}
		return "redirect:read?num=" + board.getBoardnum();
	}
	
	//리플 저장
	@PostMapping("writeReply")
	public String writeReply(Reply reply, @AuthenticationPrincipal UserDetails user) {
		//폼에서 전달된 본문글번호, 리플내용에 작성자 아이디 추가 저장
		reply.setMemberid(user.getUsername());
		//DB에 저장
		service.writeReply(reply);
		//읽던 글로 되돌아감	
		return "redirect:read?num=" + reply.getBoardnum();
	}
	
	//리플 삭제
	@GetMapping("deleteReply")
	public String replyWrite(
		Reply reply
		, @AuthenticationPrincipal UserDetails user) {
		
		reply.setMemberid(user.getUsername());
		int result = service.deleteReply(reply);
		
		return "redirect:/board/read?num=" + reply.getBoardnum();
	}
	
	
}






