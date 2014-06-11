package com.weather.util;

import javax.servlet.http.HttpServletRequest;

public class PageUtil {
	public static int SHOW_PAGE = 10;

	/**
	 * current page
	 */
	private int curPage;
	/**
	 * page size
	 */
	private int pageSize;
	/**
	 * total result count
	 */
	private int totalCount;
	/**
	 * total page count
	 */
	private int pageCount;

	/**
	 * 
	 */
	private int curFirstPage;

	/**
	 * 
	 */
	private int curLastPage;

	/**
	 * 
	 */
	private int start;
	/**
	 * 
	 */
	private int limit;

	/**
	 * 
	 */
	private int end;

	public PageUtil(int totalCount, int curPage) {
		this.curPage = curPage;
		this.pageSize = 25;
		this.totalCount = totalCount;
		this.pageCount = (int) Math.ceil((double) totalCount / pageSize);
		this.start = pageSize * (curPage - 1);
		this.limit = pageSize;
		if (totalCount - start > limit) {
			this.end = start + limit;
		} else {
			this.end = totalCount;
		}
		if (pageCount < SHOW_PAGE) {
			this.curFirstPage = 1;
			this.curLastPage = pageCount;
		} else if (pageCount - curPage < 5) {
			this.curFirstPage = pageCount - SHOW_PAGE + 1;
			this.curLastPage = pageCount;
		} else if ((curPage - SHOW_PAGE / 2) < 0) {
			this.curFirstPage = 1;
			this.curLastPage = SHOW_PAGE;
		} else {
			this.curFirstPage = curPage - SHOW_PAGE / 2 + 1;
			this.curLastPage = curPage + SHOW_PAGE / 2;
		}
	}

	public PageUtil(int totalCount, int curPage, int pageSize) {
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.pageCount = (int) Math.ceil((double) totalCount / pageSize);
		this.start = pageSize * (curPage - 1);
		this.limit = pageSize;
		if (totalCount - start > limit) {
			this.end = start + limit;
		} else {
			this.end = totalCount;
		}
		if (pageCount < SHOW_PAGE) {
			this.curFirstPage = 1;
			this.curLastPage = pageCount;
		} else if (pageCount - curPage < 5) {
			this.curFirstPage = pageCount - SHOW_PAGE + 1;
			this.curLastPage = pageCount;
		} else if ((curPage - SHOW_PAGE / 2) < 0) {
			this.curFirstPage = 1;
			this.curLastPage = SHOW_PAGE;
		} else {
			this.curFirstPage = curPage - SHOW_PAGE / 2 + 1;
			this.curLastPage = curPage + SHOW_PAGE / 2;
		}
	}

	public PageUtil(int totalCount, int curPage, int pageSize, int showPage) {
		this.curPage = curPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.pageCount = (int) Math.ceil((double) totalCount / pageSize);
		this.start = pageSize * (curPage - 1);
		this.limit = pageSize;
		if (totalCount - start > limit) {
			this.end = start + limit;
		} else {
			this.end = totalCount;
		}
		if (pageCount < showPage) {
			this.curFirstPage = 1;
			this.curLastPage = pageCount;
		} else if (pageCount - curPage < 5) {
			this.curFirstPage = pageCount - showPage + 1;
			this.curLastPage = pageCount;
		} else if ((curPage - showPage / 2) < 0) {
			this.curFirstPage = 1;
			this.curLastPage = showPage;
		} else {
			this.curFirstPage = curPage - showPage / 2 + 1;
			this.curLastPage = curPage + showPage / 2;
		}
	}

	public int getCurPage() {
		return curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getCurFirstPage() {
		return curFirstPage;
	}

	public int getCurLastPage() {
		return curLastPage;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int first() {
		return 1;
	}

	public int last() {
		return pageCount;
	}

	public int previous() {
		return (curPage - 1 < 1) ? 1 : curPage - 1;
	}

	public int next() {
		return (curPage + 1 > pageCount) ? pageCount : curPage + 1;
	}

	public boolean isFirst() {
		return (curPage == 1) ? true : false;
	}

	public boolean isLast() {
		if (pageCount == 0) {
			return true;
		}
		return (curPage == pageCount) ? true : false;
	}

	public String getToolBar(String queryUrl, HttpServletRequest request) {
		String temp = "";
		if (queryUrl.indexOf("?") == -1) {
			temp = "?";
		} else {
			temp = "&";
		}
		String str = "<p class=\"pageTurn\">";
		if (isFirst())
			str += "<span class=\"colorhuise\">"+LocalizationUtil.getClientString("FirstPage", request) + "</span> <span class=\"colorhuise\">" + LocalizationUtil.getClientString("PrevPage", request) + "</span>&nbsp;";
		else {
			str += "<a href='" + queryUrl + temp + "curPage=1'>" + LocalizationUtil.getClientString("FirstPage", request) + "</a>&nbsp;";
			str += "<a href='" + queryUrl + temp + "curPage=" + (curPage - 1) + "'>" + LocalizationUtil.getClientString("PrevPage", request) + "</a>&nbsp;";
		}
		for (int i = curFirstPage; i <= curLastPage; i++) {
			if (i == curPage) {
				str += i + "&nbsp;";
			} else {
				str += "<a href='" + queryUrl + temp + "curPage=" + i + "'>" + i + "</a>&nbsp;";
			}
		}

		if (isLast())
			str += "<span class=\"colorhuise\">" + LocalizationUtil.getClientString("NextPage", request) + "</span> <span class=\"colorhuise\">" + LocalizationUtil.getClientString("LastPage", request) + "</span>&nbsp;";
		else {
			str += "<a href='" + queryUrl + temp + "curPage=" + (curPage + 1) + "'>" + LocalizationUtil.getClientString("NextPage", request) + "</a>&nbsp;";
			str += "<a href='" + queryUrl + temp + "curPage=" + pageCount + "'>" + LocalizationUtil.getClientString("LastPage", request) + "</a>&nbsp;";
		}
		str += "&nbsp;" + LocalizationUtil.getClientString("Total", request) + "&nbsp;<b>" + pageCount+"</b>&nbsp;" + LocalizationUtil.getClientString("Pages", request) + "&nbsp;"
				+ LocalizationUtil.getClientString("Total", request) + "&nbsp;<b>" + totalCount + "</b>&nbsp;" + LocalizationUtil.getClientString("Records", request) + "&nbsp;</p>";
		return str;
	}
}
