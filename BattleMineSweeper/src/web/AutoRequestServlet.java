package web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import domain.value.GAMEID;
import domain.value.PLAYERID;
import net.arnx.jsonic.JSON;
import service.AutoRequestMiniService;

/**
 * Servlet implementation class AutoRequestServlet
 */
@WebServlet("/AutoRequestServlet")
public class AutoRequestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AutoRequestServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String isMyTurnJson = (String) request.getParameter("isMyTurn");
		boolean isMyTurn = (boolean)JSON.decode(isMyTurnJson);

		HttpSession session = request.getSession();
		PLAYERID playerID = new PLAYERID((int)session.getAttribute("PLAYERID"));
		GAMEID gameID = new GAMEID((int)session.getAttribute("GAMEID"));

		AutoRequestMiniService arms = null;

		try{
			arms = new AutoRequestMiniService();

			//JSON変換
			String autoRequestBeanJson = JSON.encode(arms.run(isMyTurn, playerID, gameID));

			System.out.println(autoRequestBeanJson);
			PrintWriter out = response.getWriter();
			out.println(autoRequestBeanJson);

			return;
		} finally{
			if(arms != null) arms.end();
		}
	}

}
