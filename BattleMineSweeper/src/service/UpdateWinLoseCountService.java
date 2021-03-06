package service;

import java.sql.Connection;
import java.sql.SQLException;

import dao.PlayerDao;
import dao.UsersDao;
import domain.Player;
import domain.value.LosePlayerID;
import domain.value.WinPlayerID;
import service.bean.WinLoseBean;

public class UpdateWinLoseCountService extends Service {

	/**
	 * コンストラクタ
	 *
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public UpdateWinLoseCountService() throws SQLException, ClassNotFoundException {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * コンストラクタ
	 *
	 * @param connectService
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public UpdateWinLoseCountService(Service connectService) throws SQLException, ClassNotFoundException {
		super(connectService);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * コンストラクタ
	 *
	 * @param connection
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public UpdateWinLoseCountService(Connection connection) throws SQLException, ClassNotFoundException {
		super(connection);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	/**
	 * ユーザの勝利回数または敗北回数を更新します。
	 *
	 * @param player1ID プレイヤー1のID
	 * @param player2ID プレイヤー2のID
	 * @param turnPlayerID 現在にターンが回っているプレイヤーのID
	 * @return
	 * @throws SQLException
	 */
	public WinLoseBean updateWinLoseCount(int player1ID, int player2ID, int turnPlayerID) throws SQLException{
		WinLoseBean winlose = new WinLoseBean();

		//プレイヤーIDからユーザIDを取得
		PlayerDao pdao = new PlayerDao(con);
		Player player1 = pdao.getPlayer(player1ID);
		Player player2 = pdao.getPlayer(player2ID);

		//ユーザのスコアを更新
		UsersDao udao = new UsersDao(con);
		//Users u1 = udao.getUser(Player1.getPLAYERID().get);
		//Users u2 = udao.getUser(Player2.getPLAYERID().get);

		if(turnPlayerID == player1ID){  												//p1が敗北した場合

				//udao.updateResult(player2.getUSERID().get(), player1.getUSERID().get());

				//p2は図鑑がひとつ開放される（被りあり）
				//p1は広告が表示される
				winlose.setWinPlayerID(new WinPlayerID(player2ID));
				winlose.setLosePlayerID( new LosePlayerID(player1ID) );

		}else if(turnPlayerID == player2ID){												//p2が敗北した場合
				//udao.updateResult(player1.getUSERID().get(), player2.getUSERID().get());

				//p2は図鑑がひとつ開放される（被りあり）
				//p1は広告が表示される
				winlose.setWinPlayerID(new WinPlayerID(player1ID));
				winlose.setLosePlayerID( new LosePlayerID(player2ID) );
		}

		return winlose;
	}


}
