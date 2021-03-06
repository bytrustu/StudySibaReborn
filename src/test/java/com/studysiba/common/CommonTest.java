package com.studysiba.common;

import com.studysiba.domain.member.MemberVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CommonTest {


    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    //@Test
    public void dataValidationTest() {
        System.out.println("이름숫자체크1 : " + DataValidation.checkEngAndNum("bytrustu"));
        System.out.println("이름숫자체크2 : " + DataValidation.checkEngAndNum("bytrustu하1202"));
        System.out.println("이름숫자체크3 : " + DataValidation.checkEngAndNum("bytrustu★1202"));
        System.out.println("이름숫자체크4 : " + DataValidation.checkEngAndNum("bytrustu12"));

        System.out.println("문자체크1 : " + DataValidation.checkOnlyCharacters("안녕123"));
        System.out.println("문자체크2 : " + DataValidation.checkOnlyCharacters("안녕★123"));
        System.out.println("문자체크3 : " + DataValidation.checkOnlyCharacters("안녕asd123"));

        System.out.println("이메일체크1 : " + DataValidation.checkEmail("bytrustu@gmail.com") );
        System.out.println("이메일체크2 : " + DataValidation.checkEmail("bytrustugmail.com") );
        System.out.println("이메일체크3 : " + DataValidation.checkEmail("bytrustu@gmailcom") );

        System.out.println("문자크기체크1 : " + DataValidation.textLengthComparison(5,"abcd"));
        System.out.println("문자크기체크2 : " + DataValidation.textLengthComparison(10,"가나다라마바"));
        System.out.println("문자크기체크3 : " + DataValidation.textLengthComparison(10,"bytrustu12"));

        System.out.println("이미지파일체크1 : " + DataValidation.checkImageFile("haha.jpg"));
        System.out.println("이미지파일체크2 : " + DataValidation.checkImageFile("haha.GIF"));
        System.out.println("이미지파일체크3 : " + DataValidation.checkImageFile("haha.jsp"));

        System.out.println("패스워드체크1 : " + DataValidation.checkPassword("bytrustu123!@#"));
        System.out.println("패스워드체크2 : " + DataValidation.checkPassword("bytrustu"));
        System.out.println("패스워드체크3 : " + DataValidation.checkPassword("bytrustu!@#"));
        System.out.println("패스워드체크4 : " + DataValidation.checkPassword("st1!"));
    }

    //@Test
    public void dataEncrypt() {
        String encode1 = passwordEncoder.encode("test");
        String encode2 = passwordEncoder.encode("안녕히가세요");

        System.out.println("인코딩 데이터1 : " + encode1);
        System.out.println("인코딩 데이터2 : " + encode2);

        System.out.println("데이터 매칭1-1 : "  + passwordEncoder.matches("안녕하세요",encode1));
        System.out.println("데이터 매칭1-2 : "  + passwordEncoder.matches("안녕히가세요",encode1));
        System.out.println("데이터 매칭1-3 : "  + passwordEncoder.matches("어서오세요",encode1));

        System.out.println("데이터 매칭2-1 : "  + passwordEncoder.matches("안녕하세요",encode2));
        System.out.println("데이터 매칭2-2 : "  + passwordEncoder.matches("안녕히가세요",encode2));
        System.out.println("데이터 매칭2-3 : "  + passwordEncoder.matches("어서오세요",encode2));
    }

    //@Test
    public void DataConvensionTest(){
        System.out.println("UUID 생성1 : " + DataConversion.returnUUID());
        System.out.println("UUID 생성2 : " + DataConversion.returnUUID());
        System.out.println("UUID 생성3 : " + DataConversion.returnUUID());

        System.out.println("랜덤번호 생성1 : " + DataConversion.returnRanNum(50));
        System.out.println("랜덤번호 생성2 : " + DataConversion.returnRanNum(5));
        System.out.println("랜덥번호 생성3 : " + DataConversion.returnRanNum(10));
    }

    //@Test
    public void DataTextLengthReturnTest(){
        System.out.println("데이터 크기만큼 반환 : " + DataValidation.textLengthReturns(12,"가나다라마바사아자차카"));
        System.out.println("데이터 크기만큼 반환 : " + DataValidation.textLengthReturns(12,"1234567890abcdefghijklmn"));
        System.out.println("데이터 크기만큼 반환 : " + DataValidation.textLengthReturns(12,"aaa"));
    }

    //@Test
    public void durationDate(){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        Calendar cal = Calendar.getInstance();
        String today = null;
        today = formatter.format(cal.getTime());
        Timestamp ts1 = Timestamp.valueOf(today);
        cal.add(Calendar.MINUTE,-10);
        today = formatter.format(cal.getTime());
        Timestamp ts2 = Timestamp.valueOf(today);
        cal.add(Calendar.DAY_OF_MONTH,-10);
        today = formatter.format(cal.getTime());
        Timestamp ts3 = Timestamp.valueOf(today);

        System.out.println(DataConversion.DurationFromNow(ts1));
        System.out.println(DataConversion.DurationFromNow(ts2));
        System.out.println(DataConversion.DurationFromNow(ts3));
    }


    //@Test
    public void testStr(){
        String test = "thank you very much";
        char[] arr = test.toCharArray();
        int checkNum = 0;
        for ( int i=0; i<test.length(); i++ ) {
            if ( arr[i] != ' ' ) {
                if ( checkNum % 2 == 0 ) {
                    arr[i] = Character.toUpperCase(arr[i]);
                }
                checkNum++;
            }
        }
        System.out.println(new String(arr));
    }

}
