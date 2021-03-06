let groupClient;
let groupMessageContainer = $('.groupmsg-container');
$(document).ready(function(){

    // 그룹메세지창 스크롤 최하단으로
    groupMessageContainer.scrollTop(groupMessageContainer[0].scrollHeight);
    // 메세지전송 엔터키 적용
    enterPressAction('groupmsg-input','groupmsg-send');
    
    // 그룹채팅 연결
    let no = contentNo();
    const sockGroup = new SockJS("/group");
    groupClient = Stomp.over(sockGroup);
    groupClient.debug = null;
    groupClient.connect({}, function(){
        groupClient.subscribe(`/topic/group/${no}`, function(msg) {
            let msgInfo = JSON.parse(msg.body);
            if ( msgInfo.statusCodeValue == '500' ){
                errorAlert("잘못된 접근 입니다.");
                return false;
            }
            let groupContainer = $('.groupmsg-container');
            if ( groupContainer.html().includes('group-notfound') ){
                groupContainer.find('.group-notfound').remove();
            }
            let isScroll = false;
            // 채팅창이 최하단에 있을경우, 메세지 보낸사람이 본인인 경우에만 채팅창 하단으로 스크롤 갱신
            if ( groupMessageContainer.scrollTop() + groupMessageContainer.innerHeight() >= groupMessageContainer[0].scrollHeight || msgInfo.body.grmId == $('#data-id').val() ) {
                isScroll = true;
            }
            sendGroupMessage(msgInfo.body, isScroll);
        });
    });

    // 그룹메세지
    const    sendGroupMessage = (messageInfo, isScroll) => {
        let msg = `
                            <div class="groupmsg-box center-block animated bounce fast">
                                    <div class="row">
                                        <div class="col-xs-8 col-md-8">
                                            <img src="/static/image/profile/${messageInfo.mbrProfile}" class="groupmsg-photo messenger-connector"  
                                                    data-id="${messageInfo.grmId}" data-nick="${messageInfo.mbrNick}" data-container="body" data-placement="top" 
                                                    data-toggle="popover" data-trigger="hover" data-content="${messageInfo.mbrNick}와 채팅">
                                            <h4 class="groupmsg-name">${messageInfo.mbrNick}</h4>
                                        </div>
                                        <div class="col-xs-4 col-md-4 text-right groupmsg-date">${messageInfo.grmDate}</div>
                                    </div>
                                    <div class="row groupmsg-text">
                                        ${messageInfo.grmText}
                                    </div>
                                </div>
                        `;
        groupMessageContainer.append(msg);
        if ( isScroll == true ) groupMessageContainer.scrollTop(groupMessageContainer[0].scrollHeight);
        $('.groupmsg-box').last().find('.groupmsg-photo').popover();
    }

    // 그룹메세지 전송버튼
    $(document).on('click', '.groupmsg-send', function(){
        let message = $('.groupmsg-input').val();
        $('.groupmsg-input').val('');
        if ( message.trim() == '' ) { errorAlert("메세지를 입력해주세요."); return false;}
        groupClient.send(`/chat/${contentNo()}`, {}, JSON.stringify({"message":message}));
    });

});


