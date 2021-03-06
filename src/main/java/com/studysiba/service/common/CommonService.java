package com.studysiba.service.common;

import com.studysiba.domain.common.Criteria;
import com.studysiba.domain.common.PageVO;
import com.studysiba.domain.common.StateVO;
import com.studysiba.domain.member.MemberVO;
import com.studysiba.domain.member.PointVO;
import com.studysiba.domain.study.StudyVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface CommonService {

    /*
     *  구글 Social Login Url 반환
     */
    String getGoogleUrl();

    /*
     *  네이버 Social Login Url 반환
     */
    String getNaverUrl();

    /*
     *  유저 랭킹 1~3위 정보 조회
     */
    List<PointVO> viewUserTotalRanking() throws Exception;

    /*
     *  페이지 별 안내 게시글 반환
     *  @Param 메뉴
     *  @Return 안내글
     */
    HashMap<String,String> getIntroduceComment(String path);

    /*
     *  공통 파일 업로드
     *  @Param MultipartFile, menu
     *  @Return 업로드 파일 이름
     */
    String uploadFile(MultipartFile multipartFile, String menu) throws Exception;

    /*
     *  게시판 공통 파일 업로드
     *  @Param MultipartFile, menu, no
     *  @Return UploadVO
     */
    String contentUploadFile(MultipartFile multipartFile, String menu, int no) throws Exception;

    /*
     *  게시판 공통 파일 업데이트
     *  @Param MultipartFile, menu, no
     *  @Return UploadVO
     */
    String contentUpdateFile(MultipartFile multipartFile, String menu, int no) throws Exception;


    /*
     *  페이지 정보 조회
     *  @Param menu, criteria
     *  @Return 페이지정보 반환
     */
    PageVO getPageInfomation(String menu, Criteria criteria);

    /*
     *  그룹 공지사항 페이지 정보 조회
     *  @Param criteria, no
     *  @Return 페이지정보 반환
     */
    PageVO getGroupPageInfomation(Criteria criteria, int no);

    /*
     *  파일다운로드
     *  @Param menu, no
     *  @Return HashMap<String, Object>
     */
    HashMap<String, Object> downloadFile(String menu, int no) throws IOException;

    /*
     *  관리자 권한
     *  @Param requireAdmin
     */
    void isRequireAdmin(boolean requireAdmin);

    /*
     *  접속중인 멤버 리스트 조회
     *  @Return 접속중인 멤버 리스트 반환
     */
    List<MemberVO> connectedMemberList();

    /*
     *  스터디 리스트 조회
     *  @Return 스터디 리스트 반환
     */
    List<StudyVO> studyList();

    /*
     *  스터디시바 회원수 및 방문수 조회
     *  @Return 회원수 방문수 반환
     */
    HashMap<String,Integer> memberCount();

    /*
     *  에러에 따른 처리
     *  @Param request
     */
    void handleError(HttpServletRequest request);

    /*
     *  포인트 업데이트
     *  @Param id, point
     *  @Return 포인트 업데이트에 따른 상태코드 반환
     */
    StateVO setPoint(String id, int score);

    /*
     *  회원 방문수, 게시글수, 댓글수 조회
     *  @Return 회원 사이트 정보 반환
     */
    HashMap<String,Integer> memberInfoCount();
}
