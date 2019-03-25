package com.studysiba.service.member;

import com.studysiba.common.DataConversion;
import com.studysiba.common.DataValidation;
import com.studysiba.domain.member.MemberVO;
import com.studysiba.mapper.member.MemberMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

@Service
@Log4j
public class MemberServiceImpl implements MemberService {

    @Resource
    MemberMapper memberMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    HttpSession httpSession;

    /*
     *  회원초대장전송
     *  @Param 아이디
     *  @Return 초대장발송여부
     */
    @Transactional
    @Override
    public boolean inviteUser(String mbrId) throws Exception {

        // 초대 여부 반환값
        boolean inviteState = false;

        MemberVO memberVO = new MemberVO();
        memberVO.setMbrId(mbrId);

        int registrationStatus = memberMapper.registrationStatus(memberVO);
        log.info("가입상태확인 : " + registrationStatus);

        // 회원등록되어있을경우 ( 0:ERROR , 1:진행 )
        if (registrationStatus == 1) {
            String checkStatus = memberMapper.getType(memberVO);
            log.info("회원타입확인 : " + checkStatus);

            // 회원가입 대기상태일경우 ( NONE )
            if (checkStatus.toUpperCase().equals("NONE")) {
                sendEmail(memberVO, "invite");
                inviteState = true;
            }
        }
        return inviteState;
    }

    /*
     *  회원비밀번호변경메일발송
     *  @Param 이메일
     *  @Return 메일발송여부
     */
    @Override
    public boolean sendMailPasswordChanger(String mbrEmail) throws Exception {
        boolean sendState = false;
        MemberVO memberVO = new MemberVO();
        memberVO.setMbrEmail(mbrEmail);
        String checkMailById = memberMapper.checkMailState(memberVO);
        if (checkMailById  != null) {
            sendState = true;
            memberVO.setMbrId(checkMailById);
            sendEmail(memberVO, "password");
        }
        return sendState;
    }


    /*
     *  메일전송
     *  @Param MemberVO
     */
    public void sendEmail(MemberVO memberVO, String type) throws Exception {
        // 인증코드 갱신

        MimeMessage mail = javaMailSender.createMimeMessage();

        // 이메일 & 코드 조회 memberVO.setMbrCode(DataConversion.returnUUID());
        //        memberMapper.renewAuthenticationCode(memberVO);
        memberVO.setMbrEmail(memberMapper.getEmail(memberVO));
        memberVO.setMbrCode(memberMapper.getCode(memberVO));
        log.info("회원이메일확인 : " + memberVO.getMbrEmail());
        log.info("회원코드확인 : " + memberVO.getMbrCode());

        StringBuffer htmlStr = new StringBuffer();
        String siteUrl = "localhost:8282";

        // 초대장 보내질 양식
        switch (type) {
            case "invite":
                htmlStr.append("<a href='http://" + siteUrl + "/member/mail/invite");
                htmlStr.append("/" + memberVO.getMbrId());
                htmlStr.append("/" + memberVO.getMbrCode());
                htmlStr.append("'>");
                htmlStr.append("<img src='https://i.imgur.com/yr5w0qi.jpg' style='width:100%'></a><br/>");
                htmlStr.append("상단 초대장 클릭하시면 인증이 됩니다. 감사합니다^^");
                log.info("초대장 양식 : " + htmlStr.toString());
                // 초대장 제목
                mail.setSubject("[ 초대장발급 - " + memberVO.getMbrId() + "님 ] 스터디시바");
                break;

            case "password":
                htmlStr.append("<a href='http://" + siteUrl + "/member/mail/changepass");
                htmlStr.append("/" + memberVO.getMbrId());
                htmlStr.append("/" + memberVO.getMbrCode());
                htmlStr.append("'>");
                htmlStr.append("<img src='https://i.imgur.com/yr5w0qi.jpg' style='width:100%'></a><br/>");
                htmlStr.append("상단 이미지를 클릭하시면 이동 됩니다. 감사합니다^^");
                log.info("초대장 양식 : " + htmlStr.toString());
                // 초대장 제목
                mail.setSubject("[ 비밀번호변경 - " + memberVO.getMbrId() + "님 ] 스터디시바");
                break;
        }

        // 초대장 내용 타입
        mail.setText(htmlStr.toString(), "utf-8", "html");
        // 초대장 보내어질 이메일 주소
        mail.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(memberVO.getMbrEmail()));
        javaMailSender.send(mail);
    }

    /*
     *  회원인증확인
     *  @Param 아이디, 코드
     *  @Return 인증확인여부
     */
    @Override
    public String emailAuthentication(String mbrId, String mbrCode) {
        MemberVO memberVO = new MemberVO();
        memberVO.setMbrId(mbrId);
        memberVO.setMbrCode(mbrCode);
        // 회원인증확인
        int informationCheckStatus = memberMapper.informationCheckStatus(memberVO);
        if (informationCheckStatus == 1) {
            // 회원 활성화 및 코드갱신
            memberVO.setMbrCode(DataConversion.returnUUID());
            memberMapper.changeStatus(memberVO);
            httpSession.setAttribute("stateCode", "AUTH_STATE_SUCCESS");
        } else {
            httpSession.setAttribute("stateCode", "AUTH_STATE_ERROR");
        }
        return informationCheckStatus == 1 ? "AUTH_STATE_SUCCESS" : "AUTH_STATE_ERROR";
    }

    /*
     *  패스워드변경메일인증확인
     *  @Param 아이디, 코드
     *  @Return 인증확인여부
     */
    @Override
    public String recoveryPassword(String mbrId, String mbrCode) {
        MemberVO memberVO = new MemberVO();
        memberVO.setMbrId(mbrId);
        memberVO.setMbrCode(mbrCode);
        // 회원인증확인
        int passwordMailState = memberMapper.informationCheckStatus(memberVO);
        if (passwordMailState == 1) {
            // 코드갱신
            memberVO.setMbrCode(DataConversion.returnUUID());
            memberMapper.renewAuthenticationCode(memberVO);
            httpSession.setAttribute("stateCode", "PASSAUTH_STATE_SUCCESS");
            httpSession.setAttribute("authId", memberVO.getMbrId());
        } else {
            httpSession.setAttribute("stateCode", "PASSAUTH_STATE_ERROR");
        }
        return passwordMailState == 1 ? "PASSAUTH_STATE_SUCCESS" : "PASSAUTH_STATE_ERROR";
    }

    /*
     *  회원가입
     *  @Param MemberVO
     *  @Return 회원가입절차에 따른 상태메세지
     */
    @Transactional
    @Override
    public String register(MemberVO memberVO) throws Exception {
        String stateCode = "";
        String[] variableNames = {"mbrId", "mbrPass", "mbrNick", "mbrEmail", "mbrProfile"};

        // 입력 된 데이터 NULL 및 공백확인 [ 모든 데이터가 있을경우 VALUES_STATE_GOOD ]
        stateCode = DataValidation.findEmptyValue(memberVO, variableNames);
        if (stateCode.equals("VALUES_STATE_GOOD")) {
            // 아이디 검증 [ 영어 또는 숫자 포함 / 문자 길이 12까지 ]
            if (!DataValidation.checkEngAndNum(memberVO.getMbrId()) || !DataValidation.textLengthComparison(12, memberVO.getMbrId())) {
                stateCode = "ID_STATE_ERROR";
                // 비밀번호 검증 [ 영어, 숫자 포함 / 문자 길이 5-16까지 ]
            } else if (!DataValidation.checkPassword(memberVO.getMbrPass())) {
                stateCode = "PASS_STATE_ERROR";
                // 닉네임 검증 [ 한글 또는 영어 또는 숫자 포함 / 문자크기 10까지 ]
            } else if (!DataValidation.checkOnlyCharacters(memberVO.getMbrNick()) || !DataValidation.textLengthComparison(10, memberVO.getMbrNick())) {
                stateCode = "NICK_STATE_ERROR";
                // 이메일 검증
            } else if (!DataValidation.checkEmail(memberVO.getMbrEmail())) {
                stateCode = "EMAIL_STATE_ERROR";
                // 프로필사진 확장자 검증  [ JPG, JPEG, PNG, GIF, BMP ]
            } else if (!DataValidation.checkImageFile(memberVO.getMbrProfile())) {
                stateCode = "PROFILE_STATE_ERROR";

                // 회원가입 진행
            } else {
                // 아이디가 이미 등록되어져 있는 경우
                if (memberMapper.idReduplicationCheck(memberVO) == 1) {
                    stateCode = "ID_STATE_USED";
                    // 아이디가 이메일 승인 대기중인 경우
                    if (stateCode.equals("ID_STATE_USED")) {
                        stateCode = memberMapper.emailApprovalStatus(memberVO).toUpperCase().equals("NONE") ? "ID_STATE_WAITAPPROVAL" : "ID_STATE_USED";
                    }
                } else {
                    // 아이디 중복 이 없을경우 이메일 중복 확인
                    if (memberMapper.emailReduplicationCheck(memberVO) == 1) {
                        stateCode = "EMAIL_STATE_USED";
                    } else {
                        if (memberMapper.nickReduplicationCheck(memberVO) == 1) {
                            stateCode = "NICK_STATE_USED";
                        } else {
                            // 회원정보가 등록이 된 경우 => 패스워드 암호화 및 인증코드 발급
                            memberVO.setMbrPass(passwordEncoder.encode(memberVO.getMbrPass()));
                            memberVO.setMbrCode(DataConversion.returnUUID());
                            stateCode = memberMapper.memberRegistration(memberVO) == 1 ? "MEMBER_STATE_SUCCESS" : "SYSTEM_STATE_ERROR";
                        }
                    }
                }
            }
        } else {
            // 데이터가 NULL 및 공백이 있을경우 상태코드로 변환
            stateCode = stateCode.toUpperCase().replace("MBR", "").concat("_STATE_EMPTY");
        }
        return stateCode;
    }

    /*
     *  회원로그인
     *  @Param MemberVO
     *  @Return 회원로그인 따른 상태메세지
     */
    @Override
    public String normalLoginAuthentication(MemberVO memberVO) {
        String stateCode = "LOGIN_STATE_ERROR";
        // 회원정보데이터 조회
        MemberVO memberData = memberMapper.viewMemberInformation(memberVO);
        if (memberData != null) {
            // 입력된 비밀번호와 회원정보데이터와 매칭
            if (passwordEncoder.matches(memberVO.getMbrPass(), memberData.getMbrPass())) {
                // 회원상태정보가 승인검토중일경우 ID_STATE_WAITAPPROVAL 반환
                if (memberData.getMbrType().toUpperCase().equals("NONE")) return "ID_STATE_WAITAPPROVAL";
                stateCode = "LOGIN_STATE_SUCCESS";
                // 방문 조회수
                int visitCount = memberMapper.totalVisitCountCheck(memberVO);
                // 세션등록
                httpSession.setAttribute("auth", memberData.getMbrAuth());
                httpSession.setAttribute("id", memberData.getMbrId());
                httpSession.setAttribute("nick", memberData.getMbrNick());
                httpSession.setAttribute("email", memberData.getMbrEmail());
                httpSession.setAttribute("profile", memberData.getMbrProfile());
                httpSession.setAttribute("connect", memberData.getMbrCode());
                httpSession.setAttribute("visit", visitCount);
                httpSession.setAttribute("stateCode", stateCode);
            }
        }
        return stateCode;
    }

    /*
     *  미승인회원정보삭제
     *  @Param MemberVO
     *  @Return 회원로그인 따른 상태메세지
     */
    @Override
    public String deleteInformation(MemberVO memberVO) {
        int deleteState = memberMapper.deleteInformation(memberVO);
        return deleteState == 1 ? "INFODEL_STATE_SUCCESS" : "INFODEL_STATE_ERROR";
    }

    /*
     *  이메일인증을통한비밀번호변경
     *  @Param MemberVO
     *  @Return 비밀번호변경여부에 따른 상태메세지
     */
    @Transactional
    @Override
    public String changePasswordEmailAuth(MemberVO memberVO) {
        String changePasswordState = "PASS_CHANGE_ERROR";
        // 비밀번호 유효성 검사 실패시
        if ( !DataValidation.checkPassword(memberVO.getMbrPass()) ) return changePasswordState;
        String encodePassword = passwordEncoder.encode(memberVO.getMbrPass());
        memberVO.setMbrPass(encodePassword);
        int changeState = memberMapper.changePasswordEmailAuth(memberVO);
        if ( changeState == 1 ) {
            changePasswordState = "PASS_CHANGE_SUCCESS";
            httpSession.removeAttribute("authId");
            // 인증코드 갱신
            memberVO.setMbrCode(DataConversion.returnUUID());
            memberMapper.renewAuthenticationCode(memberVO);
        }
        return changePasswordState;
    }




}
