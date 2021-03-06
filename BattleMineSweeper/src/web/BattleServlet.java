package web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.Player;
import domain.value.GAMEID;
import service.FieldService;
import service.MatchingDecisionService;
import service.PlayerCreateService;
import service.bean.ConfigBean;
import service.bean.UserBean;

/**
 * Servlet implementation class BattleServlet
 */
@WebServlet("/BattleServlet")
public class BattleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BattleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FieldService service = null;



		/*try{
			service = new FieldService();
			Game game = new Game();
			//service.createGame(game);
			int gameid = game.getGAMEID().get();//ゲームID
			int difficultyid = game.getDIFFICULTYID().get();//難易度ID
			service.fieldPlacement(gameid, difficultyid);
		}catch(Throwable e){
			service.rollebackEnd();
			throw e;
		}finally{
			service.end();
		}*/

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//【FieldServiceで処理】
		//FieldService fs = new FieldService();

		// 今は固定値を入れている。
		// ゲームIDと難易度IDを取得して入れてください
	    //fs.fieldPlacement(2, 1);

		UserBean userB = (UserBean) request.getSession().getAttribute("UserBean");
		ConfigBean config = (ConfigBean) request.getSession().getAttribute("config");
		PlayerCreateService PCS = null;
		MatchingDecisionService mds = null;
		Player player = null;
		try {
			//プレイヤーをDB登録
			PCS = new PlayerCreateService();
			// UserBeanとConfigBeanをセットする。
			player = PCS.createPlayer(userB, config);
			// 登録したプレイヤーをセッションに登録する
			request.getSession().setAttribute("player",player);
			request.getSession().setAttribute("PLAYERID",player.getPLAYERID().get());

			//マッチング
			mds = new MatchingDecisionService(PCS);
			GAMEID gameID = mds.matchingDecision(player, config);
			request.getSession().setAttribute("GAMEID", gameID.get());
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			PCS.end();
			if(mds != null)mds.end();
		}

		System.out.println(userB.getUserName().get()+":"+request.getSession());

		RequestDispatcher disp = request.getRequestDispatcher("/battleMode.jsp");
		disp.forward(request, response);

		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
