package net.softsociety.spring5.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
	int replynum;					//댓글 번호
	int boardnum;					//본문 글번호
	String memberid;				//작성자 아이디
	String replytext;				//댓글 내용
	String inputdate;				//작성일
}
