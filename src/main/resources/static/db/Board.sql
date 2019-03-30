-- 게시글 등록
INSERT INTO BOARD( BRD_NO, BRD_TYPE, BRD_DIVIDE, BRD_ID, BRD_TITLE, BRD_CONTENT, BRD_GNO, BRD_STEP, BRD_INDENT, BRD_COUNT, BRD_AVAILABLE, BRD_DATE )
VALUES(1, 'COMMUNITY', 3, 'TEST1', '안녕하세요', 'ㅎㅎㅎㅎㅎ', 1, 0, 0, 0, 1, NOW() );

-- 답글로 먼저 등록된 게시글 STEP 증가
UPDATE BOARD SET BRD_STEP = BRD_STEP+1
WHERE
BRD_GNO = ( SELECT * FROM ( SELECT BRD_GNO FROM BOARD WHERE BRD_NO = 1 ) AS A )
AND
BRD_STEP > ( SELECT * FROM ( SELECT BRD_STEP FROM BOARD WHERE BRD_NO = 1 ) AS B );

