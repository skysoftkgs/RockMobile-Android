package org.church.rockmobile.model;

public class InviteUserModel {
	UserModel user;
	int invitationStatus;
	
	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}
	
	public int getInvitationStatus() {
		return invitationStatus;
	}

	public void setInvitationStatus(int invitationStatus) {
		this.invitationStatus = invitationStatus;
	}
}
