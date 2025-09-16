package com.evlink.domain.faq.vo;

import org.apache.ibatis.type.Alias;
import lombok.Getter;
import lombok.Setter;

@Alias("faqvo")
@Getter
@Setter
public class FaqVO {
    private int faqId;        // faq_id
    private String question;  // question
    private String answer;    // answer
    private String useyn;     // use_yn
}