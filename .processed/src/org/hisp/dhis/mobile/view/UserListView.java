package org.hisp.dhis.mobile.view;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.User;
import org.hisp.dhis.mobile.recordstore.RecipientRecordStore;
import org.hisp.dhis.mobile.recordstore.UserRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class UserListView extends AbstractView implements CommandListener {
	private static final String CLASS_TAG = "UserListView";

	private Vector userVector;

	private Command backCommand;

	private Command addCommand;

	private Command addAllCommand;

	private List userList;

	private Alert exitAlert;

	private NameBasedMIDlet nameBasedMIDlet;

	public UserListView(DHISMIDlet dhisMIDlet) {
		super(dhisMIDlet);
		this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
		userVector = new Vector();

	}

	public void prepareView() {
		this.userVector = null;
		this.backCommand = null;
		this.addCommand = null;
		this.addAllCommand = null;
		this.userList = null;
		this.exitAlert = null;

		try {
			this.userVector = UserRecordStore.loadUsers();
			this.getUserList().deleteAll();

			for (int i = 0; i < userVector.size(); i++) {
				User user = (User) userVector.elementAt(i);
				this.getUserList().append(
						user.getFirstName() + " " + user.getSurname(), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.gc();

	}

	public void showView() {
		LogMan.log(LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "User List"
				+ " Screen...");
		prepareView();
		switchDisplayable(null, this.getUserList());

	}

	public void commandAction(Command command, Displayable displayable) {
		Vector pendingUserVector = new Vector();

		if (command == this.getBackCommand()) {
			this.nameBasedMIDlet.getFindUserView().showView();
		} else if (command == List.SELECT_COMMAND) {
			try {
				User user = (User) this.getUserVector().elementAt(
						this.getUserList().getSelectedIndex());

				pendingUserVector.addElement(user);

				RecipientRecordStore.saveRecipients(pendingUserVector);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nameBasedMIDlet.getMessageSubjectView().showView();
			}

		} else if (command == this.getAddCommand()) {
			try {
				User user = (User) this.getUserVector().elementAt(
						this.getUserList().getSelectedIndex());

				pendingUserVector.addElement(user);

				RecipientRecordStore.saveRecipients(pendingUserVector);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					nameBasedMIDlet.getMessageOptionView().showView();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} else if (command == this.getAddAllCommand()) {
			try {
				RecipientRecordStore.saveRecipients(this.getUserVector());
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				try {
					nameBasedMIDlet.getMessageOptionView().showView();
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		}

	}

	public Vector getUserVector() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		if (userVector == null) {
			userVector = UserRecordStore.loadUsers();
		}
		return userVector;
	}

	public void setUserVector(Vector userVector) {
		this.userVector = userVector;
	}

	public Command getBackCommand() {
		if (backCommand == null) {
			backCommand = new Command(Text.BACK(), Command.BACK, 0);
		}
		return backCommand;
	}

	public void setBackCommand(Command backCommand) {
		this.backCommand = backCommand;
	}

	public Command getAddCommand() {
		if (addCommand == null) {
			addCommand = new Command(Text.ADD(), Command.SCREEN, 0);
		}

		return addCommand;
	}

	public void setAddCommand(Command addCommand) {
		this.addCommand = addCommand;
	}

	public Command getAddAllCommand() {
		if (addAllCommand == null) {
			addAllCommand = new Command(Text.ADD_ALL(), Command.SCREEN, 0);
		}
		return addAllCommand;
	}

	public void setAddAllCommand(Command addAllCommand) {
		this.addAllCommand = addAllCommand;
	}

	public List getUserList() {
		if (this.userList == null) {
			this.userList = new List(Text.SELECT_USER(), List.IMPLICIT);
			this.userList.setFitPolicy(List.TEXT_WRAP_ON);
			this.userList.addCommand(this.getBackCommand());
			this.userList.addCommand(this.getAddCommand());
			this.userList.addCommand(this.getAddAllCommand());
			this.userList.setCommandListener(this);
		}
		return userList;
	}

	public void setUserList(List userList) {
		this.userList = userList;
	}

	public Alert getExitAlert() {
		if (this.exitAlert == null) {
			exitAlert = Alerts.getConfirmAlert(Text.MESSAGE(),
					Text.EXIT_CONFIRM(), this);
		}
		return exitAlert;
	}

	public void setExitAlert(Alert exitAlert) {
		this.exitAlert = exitAlert;
	}

}
