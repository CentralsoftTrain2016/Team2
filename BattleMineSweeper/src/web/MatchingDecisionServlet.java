package web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.value.DIFFICULTYID;
import domain.value.PLAYMODE;
import service.FieldService;

/**
 * Servlet implementation class MatchingDecisionServlet
 */
@WebServlet("/MatchingDecisionServlet")
public class MatchingDecisionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MatchingDecisionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String player = request.getParameter("player");

		//文字化けのおまじない
		player =new String(player.getBytes("ISO8859-1"),"UTF-8");

//		System.out.println(player);

		// 【MatchingServiceで処理】

		if (player.equals("Aさん")) {
			FieldService fs = new FieldService();
			DIFFICULTYID difficultyID = new DIFFICULTYID((int)request.getSession().getAttribute("difficultyID"));
			// 今は固定値を入れている。
			// ゲームIDと難易度IDを取得して入れてください
		    fs.fieldPlacement(1, difficultyID.get(), PLAYMODE.BATTLE_MODE);

			request.getSession().setAttribute("PLAYERID", 1);

		} else if (player.equals("Bさん")) {
			request.getSession().setAttribute("PLAYERID", 2);


		}

		request.getSession().setAttribute("GAMEID", 1);

		RequestDispatcher disp = request.getRequestDispatcher("BattleServlet");
		disp.forward(request, response);
	}

}
