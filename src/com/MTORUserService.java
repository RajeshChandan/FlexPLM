package com.meritor.odata.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.core.meta.common.RemoteWorker;
import com.ptc.core.meta.common.RemoteWorkerHandler;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;

public class MTORUserService {

	protected static final Logger logger = LogR.getLogger(MTORUserService.class.getName());
	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("Meritor@123");
		try {
			SessionHelper.manager.getPrincipal();
			RemoteWorkerHandler.handleRemoteWorker(new mtorRemoteWorker(), args);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public String getActiveUsers() {
		String json = "";

		try {
			MTORUser user;
			ArrayList<MTORUser> list = new ArrayList<>();

			QuerySpec qs = new QuerySpec(WTUser.class);

			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			logger.debug("qr count>>>" + qr.size());

			while (qr.hasMoreElements()) {

				WTUser wtUser = (WTUser) qr.nextElement();

				if(Objects.nonNull(wtUser.getDn()) && wtUser.getDn().length() > 5) {

					user = new MTORUser();

					user.setEmail(wtUser.getEMail());
					user.setFullname(wtUser.getFullName());
					user.setUserName(wtUser.getName());

					ReferenceFactory referencefactory = new ReferenceFactory();
					String oid = referencefactory.getReferenceString(wtUser);
					user.setUserId(oid);

					list.add(user);
				}

			}
			logger.debug("list count>>>" + list.size());
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.error(json);
		return json;
	}

	public String getUsers(String name) {
		String json = "";

		try {
			MTORUser user;
			Map<String, MTORUser> usrMap = new HashMap<>();
			ArrayList<MTORUser> list = new ArrayList<>();

			QuerySpec qs = new QuerySpec(WTUser.class);

			qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.LIKE, "%" + name + "%"), new int[]{0});

			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

			logger.debug("qr count>>>" + qr.size());

			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();

				WTUser wtUser = (WTUser) obj;

				user = new MTORUser();

				user.setEmail(wtUser.getEMail());
				user.setFullname(wtUser.getFullName());
				user.setUserName(wtUser.getName());

				usrMap.put(wtUser.getName(), user);

				ReferenceFactory referencefactory = new ReferenceFactory();
				String oid = referencefactory.getReferenceString(wtUser);
				user.setUserId(oid);

				usrMap.put(wtUser.getName(), user);

			}
			list.addAll(usrMap.values());

			logger.debug("usrMap count>>>" + usrMap.size());
			logger.debug("list count>>>" + list.size());

			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.error(json);
		return json;
	}

}
class MTORUser {
	private String userId;
	private String userName;
	private String fullname;
	private String email;
	private Timestamp lastLoggedin;
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId
	 *                   the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName
	 *                     the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}
	/**
	 * @param fullname
	 *                     the fullname to set
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email
	 *                  the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the lastLoggedin
	 */
	public Timestamp getLastLoggedin() {
		return lastLoggedin;
	}
	/**
	 * @param lastLoggedin
	 *                         the lastLoggedin to set
	 */
	public void setLastLoggedin(Timestamp lastLoggedin) {
		this.lastLoggedin = lastLoggedin;
	}
	public MTORUser() {
		super();
	}
	public MTORUser(String userId, String userName, String fullname, String email) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.fullname = fullname;
		this.email = email;
	}
	@Override
	public String toString() {
		return "MTORUser [userId=" + userId + ", userName=" + userName + ", fullname=" + fullname + ", email=" + email + "]";
	}

}
class mtorRemoteWorker extends RemoteWorker {

	@Override
	public Object doWork(Object arg0) throws Exception {
		String s = new MTORUserService().getActiveUsers();
		return s;
	}

}
class UserGroupsRoles {
	private String userId;
	private String fullName;
	private String email;
	private String memberGroup;
	private String contextName;
	private String roles;

	public UserGroupsRoles() {
	}

	public UserGroupsRoles(String userId, String fullName, String email, String memberGroup, String contextName, String roles) {
		this.setUserId(userId);
		this.setFullName(fullName);
		this.setEmail(email);
		this.setMemberGroup(memberGroup);
		this.setContextName(contextName);
		this.setRoles(roles);
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMemberGroup() {
		return this.memberGroup;
	}

	public void setMemberGroup(String memberGroup) {
		this.memberGroup = memberGroup;
	}

	public String getContextName() {
		return this.contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getRoles() {
		return this.roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
}