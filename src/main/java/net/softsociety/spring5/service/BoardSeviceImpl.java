package net.softsociety.spring5.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.softsociety.spring5.dao.BoardDAO;
import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;
import net.softsociety.spring5.util.PageNavigator;

@Transactional
@Service
@Slf4j
public class BoardSeviceImpl implements BoardService {

    @Autowired
    private BoardDAO boardDAO;

	@Override
	public int write(Board board) {
		int result = boardDAO.insertBoard(board);
		return result;
	}

	
	@Override
	public PageNavigator getPageNavigator(
		int pagePerGroup, int countPerPage, int page, String type, String searchWord) {
		//검색 대상과 검색어
		HashMap<String, String> map = new HashMap<>();
		map.put("type", type);
		map.put("searchWord", searchWord);
		//검색 결과 개수
		int t = boardDAO.total(map);
		//페이지 이동 링크수, 페이지당 글수, 현재페이지, 전체 글수를 전달하여 객체 생성
		PageNavigator navi = new PageNavigator(pagePerGroup, countPerPage, page, t);
		
		return navi;
	}

	@Override
	public ArrayList<Board> list(int start, int count, String type, String searchWord) {
		//검색 대상과 검색어
		HashMap<String, String> map = new HashMap<>();
		map.put("type", type);
		map.put("searchWord", searchWord);
		//조회 결과 중 위치, 개수 지정
		RowBounds rb = new RowBounds(start, count);
		
		ArrayList<Board> boardlist = boardDAO.list(map, rb);
		return boardlist;
	}

	@Override
	public Board read(int num) {
		//조회수 1증가
		boardDAO.add(num);
		
		//글 정보 읽기
		Board board = boardDAO.selectBoard(num);
		return board;
	}

	@Override
	public int delete(Board board) {
		int result = boardDAO.deleteBoard(board);
		return result;
	}

	@Override
	public int update(Board board) {
		int result = boardDAO.updateBoard(board);
		return result;
	}


	@Override
	public int writeReply(Reply reply) {
		int n = boardDAO.insertReply(reply);
		return n;
	}

	@Override
	public ArrayList<Reply> readReply(int boardnum) {
		ArrayList<Reply> replylist = boardDAO.readReply(boardnum);
		return replylist;
	}
	
	@Override
	public int deleteReply(Reply reply) {
		int result = boardDAO.deleteReply(reply);
		return result;
	}
}
