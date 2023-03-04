package net.softsociety.spring5.service;

import java.util.ArrayList;

import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;
import net.softsociety.spring5.util.PageNavigator;

public interface BoardService {
	//글 저장
	public int write(Board board);
	//글 목록
	public ArrayList<Board> list(int start, int count, String type, String searchWord);
	//글 읽기
	public Board read(int num);
	//페이지 정보 객체 생성
	public PageNavigator getPageNavigator(int pagePerGroup, int countPerPage, int page, String type, String searchWord);
	//글 삭제
	public int delete(Board board);
	//글 수정
	public int update(Board board);
	//리플 저장
	public int writeReply(Reply reply);
	//리플 목록
	public ArrayList<Reply> readReply(int num);
	//리플 삭제
	public int deleteReply(Reply reply);
}
