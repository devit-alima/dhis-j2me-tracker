package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2014, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.model.Recipient;
import org.hisp.dhis.mobile.recordstore.FeedbackRecordStore;
import org.hisp.dhis.mobile.recordstore.RecipientRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class MessageView extends AbstractView implements ActionListener {
	private static final String CLASS_TAG = "MessageView";

	private NameBasedMIDlet nameBasedMIDlet;

	private TextArea messageTextArea;

	private Command backCommand;

	private Command sendCommand;

	private Vector recipientVector;

	private Message message;

	private Recipient recipient;

	private Form messageForm;

	public MessageView(DHISMIDlet dhisMIDlet) {
		super(dhisMIDlet);
		this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
	}

	public void prepareView() {

		System.gc();
		getMessageForm();
		messageForm.removeAll();
		messageForm.addComponent(this.getMessageTextArea());
		System.gc();

	}

	public void showView() {
		prepareView();
		this.getMessageForm().show();

	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getCommand().getCommandName().equals(Text.BACK())) {
			nameBasedMIDlet.getWaitingView().showView();
			nameBasedMIDlet.getMessageSubjectView().showView();
		} else if (ae.getCommand().getCommandName().equals(Text.SEND())) {

			try {
				ConnectionManager.setUrl(dhisMIDlet.getCurrentOrgUnit()
						.getSendMessageUrl());
				ConnectionManager.sendMessage(this.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		this.messageTextArea = null;
		this.backCommand = null;
		this.sendCommand = null;
		this.recipientVector = null;
		this.message = null;
		this.recipient = null;

	}

	public Form getMessageForm() {
		if (messageForm == null) {
			messageForm = new Form("Enter feedback message");
			messageForm.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
			messageForm.setScrollableY(true);
			messageForm.setTransitionOutAnimator(CommonTransitions.createSlide(
					CommonTransitions.SLIDE_HORIZONTAL, true, 200));
			messageForm.addCommand(this.getBackCommand());
			messageForm.addCommand(this.getSendCommand());

			messageForm.addCommandListener(this);

		}
		return messageForm;
	}

	public void setMessageForm(Form messageForm) {
		this.messageForm = messageForm;
	}

	public TextArea getMessageTextArea() {
		if (messageTextArea == null) {
			messageTextArea = new TextArea();

		}
		return messageTextArea;
	}

	public void setMessageTextArea(TextArea messageTextArea) {
		this.messageTextArea = messageTextArea;
	}

	public Command getBackCommand() {
		if (backCommand == null) {
			backCommand = new Command(Text.BACK());
		}
		return backCommand;
	}

	public void setBackCommand(Command backCommand) {
		this.backCommand = backCommand;
	}

	public Command getSendCommand() {
		if (sendCommand == null) {
			sendCommand = new Command(Text.SEND());
		}
		return sendCommand;
	}

	public void setSendCommand(Command sendCommand) {
		this.sendCommand = sendCommand;
	}

	public Vector getRecipientVector() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		if (recipientVector == null) {
			recipientVector = RecipientRecordStore.loadRecipients();
		}
		return recipientVector;
	}

	public void setRecipientVector(Vector recipientVector) {
		this.recipientVector = recipientVector;
	}

	public Message getMessage() throws RecordStoreNotOpenException,
			RecordStoreException, IOException {
		if (message == null) {
			message = new Message();
			message.setSubject(FeedbackRecordStore.load());
			message.setText(this.getMessageTextArea().getText());
			message.setRecipient(this.getRecipient());
		}
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Recipient getRecipient() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		if (recipient == null) {
			recipient = new Recipient();
			recipient.setUsers(RecipientRecordStore.loadRecipients());
		}
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

}
