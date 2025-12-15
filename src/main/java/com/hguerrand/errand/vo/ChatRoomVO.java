package com.hguerrand.errand.vo;

public class ChatRoomVO {
    private int roomId;
    private int errandId;
    private int requesterId;
    private int helperId;

    private String errandTitle;
    private String opponentName;
    private String opponentAvatar; // ✅ 추가

    private String lastMessage;
    private String lastAt;


    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getErrandId() { return errandId; }
    public void setErrandId(int errandId) { this.errandId = errandId; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public int getHelperId() { return helperId; }
    public void setHelperId(int helperId) { this.helperId = helperId; }

    public String getErrandTitle() { return errandTitle; }
    public void setErrandTitle(String errandTitle) { this.errandTitle = errandTitle; }

    public String getOpponentName() { return opponentName; }
    public void setOpponentName(String opponentName) { this.opponentName = opponentName; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastAt() { return lastAt; }
    public void setLastAt(String lastAt) { this.lastAt = lastAt; }

    public String getOpponentAvatar() { return opponentAvatar; }
    public void setOpponentAvatar(String opponentAvatar) { this.opponentAvatar = opponentAvatar; }


}