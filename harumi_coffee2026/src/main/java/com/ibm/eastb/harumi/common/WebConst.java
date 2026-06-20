package com.ibm.eastb.harumi.common;

/**
 * Web層で使用する定数を一元管理するクラス。<br>
 * サーブレットのURL・JSPのファイル名・リクエストパラメータ名・属性名・
 * 固定エラーメッセージなどを集約し、文字列リテラルの散在を防ぐ。<br>
 * 本クラスの各定数は {@link BindConstContextListener} によってEL用の
 * ServletContext属性として公開され、JSPから <code>${定数名}</code> で参照できる。
 *
 * @author harumi development team
 * @version 1.00
 */
public class WebConst {

	// ===== サーブレットURL（コンテキストルートからの相対パス：contextPathは付けない）=====
	public static final String URL_LOGIN_SERVLET = "login";
	public static final String URL_LOGOUT_SERVLET = "logout";
	public static final String URL_MENU_SERVLET = "menu";
	public static final String URL_ORDER_SERVLET = "order";
	public static final String URL_PAYMENT_SERVLET = "payment";
	public static final String URL_RECEIPT_SERVLET = "receipt";
	public static final String URL_COFFEE_SERVLET = "coffee";
	public static final String URL_CLOSING_SERVLET = "closing";

	// ===== JSP（表示モードフォルダ配下のファイル名／トップのindex）=====
	public static final String URL_INDEX_JSP = "index.jsp";
	public static final String URL_LOGIN_JSP = "login.jsp";
	public static final String URL_MENU_JSP = "menu.jsp";
	public static final String URL_ORDER_JSP = "order.jsp";
	public static final String URL_PAYMENT_JSP = "payment.jsp";
	public static final String URL_RECEIPT_JSP = "receipt.jsp";
	public static final String URL_COFFEE_JSP = "coffee.jsp";
	public static final String URL_CLOSING_JSP = "closing.jsp";

	// ===== フォームのname属性（リクエストパラメータ名）=====
	public static final String PARAM_EMPNO = "empno";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_MODE = "mode";
	public static final String PARAM_PAYMENT = "payment";
	public static final String PARAM_QTY_PREFIX = "qty_";
	public static final String PARAM_OPERATION = "operation";
	public static final String PARAM_AMOUNT = "amount";

	// ===== コーヒー管理の操作種別（operationパラメータの値）=====
	public static final String OP_ADD = "add";
	public static final String OP_REDUCE = "reduce";

	// ===== 表示モード（viewModeの値）=====
	public static final String VIEW_EL = "el";
	public static final String VIEW_NOEL = "noel";

	// ===== request, session属性名 =====
	public static final String ATTR_USER = "user";
	public static final String ATTR_ORDER = "order";
	public static final String ATTR_COFFEE = "coffee";
	public static final String ATTR_VIEW_MODE = "viewMode";
	public static final String ATTR_ERROR = "errorMsg";
	public static final String ATTR_INFO = "infoMsg";
	public static final String ATTR_EMPNO = "empno";
	public static final String ATTR_ITEM_SALES = "itemSales";
	public static final String ATTR_TODAY_TOTAL = "todayTotal";
	public static final String ATTR_USED_COFFEE = "usedCoffee";
	public static final String ATTR_CLOSED = "closed";
	public static final String ATTR_CHANGE_UNITS = "changeUnits";

	// ===== 固定エラー／情報メッセージ（動的な値を含まないもの）=====
	public static final String ERR_LOGIN_FAILED = "従業員番号またはパスワードが正しくありません。";
	public static final String ERR_NO_ITEM_SELECTED = "商品が1点も選択されていません。個数を入力してください。";
	public static final String ERR_AMOUNT_REQUIRED = "1以上の数量を入力してください。";
	public static final String ERR_COFFEE_SHORT_CONFIRM = "コーヒー残量が不足しているため、注文を確定できません。";
}
