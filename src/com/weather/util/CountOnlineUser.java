
package com.weather.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CountOnlineUser implements HttpSessionListener {
	private static long count = 0;
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	private static final Lock readLock = lock.readLock();
	private static final Lock writeLock = lock.writeLock();
	public static final Log log = LogFactory.getLog(CountOnlineUser.class);

	public void sessionCreated(HttpSessionEvent e) {
		writeLock.lock();
		try {
			count++;
			log.debug("Session create: sessionId=" + e.getSession().getId() + ", the session number is increased to " + count);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			writeLock.unlock();
		}
	}

	public void sessionDestroyed(HttpSessionEvent e) {
		writeLock.lock();
		try {
			if (count > 0) {
				count--;
				log.debug("Session destroy: sessionId=" + e.getSession().getId() + ", the session number is decreased to " + count);
			}
			// String userName =
			// OnlineStatusCache.getInstance().getUsernameBySessionId(e.getSession().getId());
			// if (userName != null && !userName.equalsIgnoreCase("guest")) {
			// OnlineStatusCache.getInstance().removeUserSessionIdObject(e.getSession().getId());
			// ClearUserStatus.clear(userName);
			// // 更新退出用户的在线时间,改为passport应用更新用户的在线时间 modified by @kbian
			// // ClearUserStatus.updateAccountOnlineTime(userName);
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			writeLock.unlock();
		}
	}

	public static long getSessionCount() {
		readLock.lock();
		try {
			return count;
		} finally {
			readLock.unlock();
		}
	}

	public static long getOnlineUserCount() {
		readLock.lock();
		try {
			// TODO 添加在线人数
			return count;
		} finally {
			readLock.unlock();
		}
	}
}
