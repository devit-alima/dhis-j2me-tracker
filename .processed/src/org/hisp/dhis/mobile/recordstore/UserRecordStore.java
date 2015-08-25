package org.hisp.dhis.mobile.recordstore;

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

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.User;
import org.hisp.dhis.mobile.recordstore.filter.UserFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class UserRecordStore {
	public static final String CLASS_TAG = "UserRecordStore";

	public static final String USER_DB = "USER";

	public UserRecordStore() {

	}

	public static boolean saveUsers(Vector userVector)
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException, IOException {

		for (int i = 0; i < userVector.size(); i++) {
			User user = (User) userVector.elementAt(i);
			saveUser(user);
		}
		return true;

	}

	private static void saveUser(User user) throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		try {
			RecordStore recordStore = RecordStore
					.openRecordStore(USER_DB, true);
			UserFilter userFilter = new UserFilter(user);

			RecordEnumeration recordEnum = recordStore.enumerateRecords(
					userFilter, null, false);
			byte[] bytes = SerializationUtil.serialize(user);
			if (recordEnum.numRecords() > 0) {
				int id = recordEnum.nextRecordId();
				recordStore.setRecord(id, bytes, 0, bytes.length);
			} else {
				recordStore.addRecord(bytes, 0, bytes.length);
			}
			recordEnum.destroy();
			recordStore.closeRecordStore();

		} finally {
			System.gc();
		}

	}

	public static Vector loadUsers() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		Vector userVector = null;
		try {
			RecordStore recordStore = RecordStore
					.openRecordStore(USER_DB, true);
			RecordEnumeration recordEnum = recordStore.enumerateRecords(null,
					null, false);
			userVector = new Vector();
			while (recordEnum.hasNextElement()) {
				User user = new User();
				SerializationUtil.deSerialize(user, recordEnum.nextRecord());
				userVector.addElement(user);
			}
			recordEnum.destroy();
			recordStore.closeRecordStore();
		} finally {
			System.gc();
		}
		return userVector;
	}

	public static void deleteRecordStore() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException {
		RecordStore rs = RecordStore.openRecordStore(USER_DB, true);
		RecordEnumeration re = null;
		try {
			re = rs.enumerateRecords(null, null, true);
		} catch (RecordStoreNotOpenException ex) {
			LogMan.log("RMS," + CLASS_TAG, ex);
			ex.printStackTrace();
		}

		int rid = 0;

		try {
			while (re.hasNextElement()) {
				rid = re.nextRecordId();
				try {
					rs.deleteRecord(rid);
				} catch (RecordStoreNotOpenException ex) {
					LogMan.log("RMS," + CLASS_TAG, ex);
					ex.printStackTrace();
				} catch (InvalidRecordIDException ex) {
					LogMan.log("RMS," + CLASS_TAG, ex);
					ex.printStackTrace();
				} catch (RecordStoreException ex) {
					LogMan.log("RMS," + CLASS_TAG, ex);
					ex.printStackTrace();
				}
			}
		} catch (InvalidRecordIDException ex) {
			LogMan.log("RMS," + CLASS_TAG, ex);
			ex.printStackTrace();
		}
	}

}
