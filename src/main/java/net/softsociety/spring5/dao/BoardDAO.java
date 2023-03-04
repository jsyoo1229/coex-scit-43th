package net.softsociety.spring5.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import net.softsociety.spring5.domain.Board;
import net.softsociety.spring5.domain.Reply;

/**
 * 게시판 관련 매퍼 인터페이스
 */
@Mapper
public interface BoardDAO {
    //글 저장
	public int insertBoard(Board board);
	//글 목록
	public ArrayList<Board> list(HashMap<String, String> map, RowBounds r);
	//글 개수
	public int total(HashMap<String, String> map);
	//글 읽기
	public Board selectBoard(int num);
	//조회수 1증가
	public int add(int num);
	//글 삭제
	public int deleteBoard(Board board);
	//글 수정
	public int updateBoard(Board board);
	//리플 저장
	public int insertReply(Reply reply);
	//리플 목록
	public ArrayList<Reply> readReply(int boardnum);
	//리플 삭제
	public int deleteReply(Reply reply);

}
