package com.evlink.domain.community.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.evlink.domain.community.vo.EmotionVO;

@Mapper
public interface EmotionDao {
	List<EmotionVO> showList(@Param("startRow") int startRow,
            @Param("pageSize") int pageSize,
            @Param("searchType") String searchType,
            @Param("searchValue") String searchValue);

	int totalcount(@Param("searchType") String searchType,@Param("searchValue") String searchValue);
	
	@Insert("INSERT INTO tb_board (user_id, title, content, emotion)VALUES (#{user_id}, #{title}, #{content}, #{emotion})")
	void insertContent(EmotionVO vo);
	
	@Select("select user_id, title, content, reg_dt, upd_dt from tb_board where board_id = #{board_id}")
	EmotionVO showDetail(long emo_id);
	
	@Update("UPDATE tb_board SET hit = hit + 1 WHERE board_id = #{board_id}")
	void uphit(long emo_id);
	
	@Update("update tb_board set title= #{title}, content=#{content} where board_id = #{board_id}")
	void update(EmotionVO vo);
	
	@Delete("DELETE from tb_board where board_id=#{board_id}")
	void delete(long emo_id);
	
}
