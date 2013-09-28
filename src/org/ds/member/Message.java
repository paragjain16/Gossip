package org.ds.member;

import java.util.List;

public class Message {
	MessageType type;
	List<Member> memberList;
	Integer leavingMemberId;

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public List<Member> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<Member> memberList) {
		this.memberList = memberList;
	}

	public Integer getLeavingMemberId() {
		return leavingMemberId;
	}

	public void setLeavingMemberId(Integer leavingMemberId) {
		this.leavingMemberId = leavingMemberId;
	}

}
