package com.ibm.eastb.harumi.common;

import java.lang.reflect.Field;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * サーバー起動時に定数クラス({@link WebConst})の各定数をEL用に設定するクラス。<br>
 * ServletContextListenerはServletContextが変化したときに行う処理を記述できるインターフェイス。<br>
 * contextInitializedはサーバー起動時にServletContextが生成された際に実行される処理で、
 * このメソッド内でcontext属性を設定し、JSPからELで <code>${定数名}</code> としてアクセスできるようにする。
 *
 * @author harumi development team
 * @version 1.00
 */
@WebListener
public class BindConstContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// WebApp内で単一のインスタンスかつELの属性探索対象を取得
		ServletContext context = sce.getServletContext();

		// リフレクションを使って、定数クラスのフィールド情報を取得
		Field[] fields = WebConst.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				// フィールド名と設定値を取得する
				String fieldName = field.getName();
				Object value = field.get(WebConst.class);

				// ${定数名}でアクセスできるようにcontextに設定する
				context.setAttribute(fieldName, value);

			} catch (IllegalArgumentException | IllegalAccessException e) {
				// 定数値の取得に失敗した場合は何もしない
			}
		}
	}
}
