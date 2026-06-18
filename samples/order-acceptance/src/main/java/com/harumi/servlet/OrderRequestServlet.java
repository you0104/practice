package com.harumi.servlet;

import com.harumi.dao.CoffeeDao;
import com.harumi.dao.ItemDao;
import com.harumi.model.Item;
import com.harumi.model.Order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 注文受付サーブレット。
 *
 *  - コーヒーボタン押下(action=addItem) → 注文に加算し、同じ orderrequest.jsp へ forward（自己ループ）
 *  - 支払いへ進む押下(action=pay)        → payment.jsp（次のJSP）へ forward
 *
 * 注文中はセッション属性 "order" に貯め、DB登録は支払い確定時に行う（このサンプルでは payment.jsp 表示まで）。
 */
@WebServlet("/orderrequest")
public class OrderRequestServlet extends HttpServlet {

    private static final String VIEW_ORDER   = "/WEB-INF/orderrequest.jsp";
    private static final String VIEW_PAYMENT = "/WEB-INF/payment.jsp";

    private final ItemDao itemDao = new ItemDao();
    private final CoffeeDao coffeeDao = new CoffeeDao();

    /** メインメニューから遷移してきた最初の表示（GET）。 */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!ensureLoggedIn(req, res)) return;
        getOrCreateOrder(req);
        showOrderScreen(req, res);
    }

    /** ボタン押下（POST）。action でコーヒー加算／支払いを分岐。 */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (!ensureLoggedIn(req, res)) return;

        Order order = getOrCreateOrder(req);
        String action = req.getParameter("action");

        try {
            if ("addItem".equals(action)) {
                addCoffee(req, order);                 // 加算（残量チェック込み）
                showOrderScreen(req, res);             // ★同じ注文受付JSPへ戻る
                return;
            }
            if ("pay".equals(action)) {
                if (order.isEmpty()) {
                    req.setAttribute("error", "注文が入力されていません。");
                    showOrderScreen(req, res);
                    return;
                }
                req.getRequestDispatcher(VIEW_PAYMENT).forward(req, res); // ★次のJSPへ
                return;
            }
            // 不明なaction → 注文画面に戻す
            showOrderScreen(req, res);

        } catch (Exception e) {
            req.setAttribute("error", "処理中にエラーが発生しました: " + e.getMessage());
            showOrderScreen(req, res);
        }
    }

    // ---- 内部処理 -------------------------------------------------

    /** コーヒー1個を注文に加算。タンク残量が足りなければエラー属性をセットして加算しない。 */
    private void addCoffee(HttpServletRequest req, Order order) throws Exception {
        int itemNo = Integer.parseInt(req.getParameter("itemNo"));
        Item item = itemDao.findById(itemNo);
        if (item == null) {
            req.setAttribute("error", "選択された商品が見つかりません。");
            return;
        }
        int latest = coffeeDao.selectLatestCapacity();                  // DBの最新残量
        if (order.getTotalCoffee() + item.getCoffeeAmount() > latest) { // 残量超過は受付不可
            req.setAttribute("error", "タンク残量が不足しているため、この商品は注文できません。");
            return;
        }
        order.addItem(item);
    }

    /** 注文受付画面の描画。商品ボタン一覧と最新タンク残量を毎回読み直して渡す。 */
    private void showOrderScreen(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            req.setAttribute("items", itemDao.findAllOnSale());
            req.setAttribute("latestCapacity", coffeeDao.selectLatestCapacity());
        } catch (Exception e) {
            req.setAttribute("error", "商品・残量の取得に失敗しました: " + e.getMessage());
        }
        req.getRequestDispatcher(VIEW_ORDER).forward(req, res);
    }

    private Order getOrCreateOrder(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");
        if (order == null) {
            order = new Order();
            session.setAttribute("order", order);
        }
        return order;
    }

    /** 未ログインならログイン画面へリダイレクトして false を返す。 */
    private boolean ensureLoggedIn(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}
