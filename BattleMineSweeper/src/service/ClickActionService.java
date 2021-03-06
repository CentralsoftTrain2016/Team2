package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dao.GameDao;
import dao.ItemDao;
import dao.MassDao;
import dao.PlayerDao;
import dao.QuizDao;
import domain.ClickActionInterface;
import domain.Game;
import domain.Item;
import domain.Mass;
import domain.Quiz;
import domain.WinorLoseEnum;
import domain.value.ClickPlayerID;
import domain.value.ENDFLAG;
import domain.value.GAMEID;
import domain.value.ISFINISH;
import domain.value.ISOPEN;
import domain.value.MASSID;
import domain.value.MASSLIST;
import domain.value.PLAYERID;
import domain.value.PLAYMODE;
import service.bean.ClickResultBean;
import service.bean.WinLoseBean;


 /**
  * マスをクリックしたときの動作を行うサービスです。
  * @author
  *
  */
public class ClickActionService implements ClickActionInterface {

	/**
	 * コンストラクタ
	 */
	public ClickActionService() {
		super();

	}

	/**
	 * ClickActionInterfaceで宣言しているメソッドです。
	 * クリック時の動作を返すメソッドを定義します。
	 *
	 * @param massID マスID
	 * @param gameID ゲームID
	 * @param turnTotalTime クリックするまでの時間
	 * @param clickPlayerID クリックしたプレイヤーのID
	 * @param con コネクション
	 * @return クリックした時の結果
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public ClickResultBean clickAction(MASSID massID, GAMEID gameID, int turnTotalTime, ClickPlayerID clickPlayerID, Connection con)
			throws SQLException, ClassNotFoundException {
		ClickResultBean bean = new ClickResultBean();
		List<Mass> returnMassList = new ArrayList<Mass>();
		MassDao msdao = new MassDao(con);
		HashMap<Integer, Mass> allMassHash = msdao.getAllMassHash(gameID.get());
		GameDao gmdao = new GameDao(con);
		Game gm = gmdao.getGame(gameID.get());
		PLAYMODE playMode = gm.getPLAYMODE();

		Mass ms = allMassHash.get(massID.get());

		// turnPlayerじゃないプレイヤーがアクセスしたらそのままリターン
		if (gm.getTURNPLAYERID().get() != clickPlayerID.get()) {
			System.out.println("きみのばんじゃないだろ！");
			return bean;
		}

		if (ms.getISOPEN().get()) { // 既に開いていたマスの場合は空のリストを返却
			System.out.println("すでにあいてるよー");
			return bean;
		}
		// 地雷爆発処理
		if (ms.getISEXISTMINE().get()) {
			WinLoseBean winlose = playMode.explosionMine(ms.getGAMEID(), con);
			bean.setWinPlayerID(winlose.getWinPlayerID());
			bean.setLosePlayerID(winlose.getLosePlayerID());

			// 変更後
			msdao.updateAllISOPEN(ms.getGAMEID().get());
			returnMassList = msdao.getAllMass(ms.getGAMEID().get());

			bean.setMASSLIST(new MASSLIST(returnMassList));
			bean.setISFINISH(new ISFINISH(true));

			// 勝ち負けの更新
			if (playMode == PLAYMODE.BATTLE_MODE) {
				WinorLoseEnum.LOSE.updateResult(gameID, new PLAYERID(clickPlayerID.get()), con);
			} else {
				gmdao.updateResult(gameID,new PLAYERID(0));
			}
			return bean;

		}

		// クイズ取得
		int quizID = ms.getQUIZID().get();
		if (quizID != 0) {
			Quiz quiz = new QuizDao(con).getDictionaryItem(quizID);
			bean.setQuiz(quiz);
		}

		//プレイヤーDAO
		PlayerDao pdao = new PlayerDao(con);

		// アイテム取得
		int itemID = ms.getITEMID().get();
		if (itemID != 0) {
			Item item = new ItemDao(con).getItem(itemID);
			bean.setItem(item);
			pdao.updateItemId(clickPlayerID, item.getITEMID());
		}

		//相手プレイヤーID取得
		int player1ID = gm.getPLAYER1ID().get();
		int player2ID = gm.getPLAYER2ID().get();

		PLAYERID enemyID = null;

		if(player1ID == clickPlayerID.get()){
			enemyID = new PLAYERID(player2ID);
		}else{
			enemyID = new PLAYERID(player1ID);
		}

		//自分プレイヤー取得
		bean.setMyPlayer(pdao.getPlayer(clickPlayerID.get()));

		//相手プレイヤー取得
		bean.setEnemyPlayer(pdao.getPlayer(enemyID.get()));

		// 開いたマスをKekkaListに格納
		List<Mass> KekkaList = massOpen(massID.get(), allMassHash, returnMassList, 0);

		// DB更新マスオープン
		msdao.updateISOPEN(returnMassList);

		// 下に移動しました
		// bean.setMASSLIST( new MASSLIST(KekkaList) );

		// 地雷以外の全てのマスがオープンしたときの処理
		if (msdao.isOpenedAllMass(gameID)) {
			// if( true ){
			System.out.println("全部あいたよ！");
			WinLoseBean winlose = playMode.allOpen(gameID, con);
			bean.setWinPlayerID(winlose.getWinPlayerID());
			bean.setLosePlayerID(winlose.getLosePlayerID());

			// 追加
			msdao.updateAllISOPEN(ms.getGAMEID().get());
			returnMassList = msdao.getAllMass(ms.getGAMEID().get());
			for (Mass mass : returnMassList) {
				mass.setENDFLAG(new ENDFLAG(true));
			}
			bean.setMASSLIST(new MASSLIST(returnMassList));

			bean.setISFINISH(new ISFINISH(true));
			// 勝ち負けの更新
			if (playMode == PLAYMODE.BATTLE_MODE) {
				WinorLoseEnum.WIN.updateResult(gameID, new PLAYERID(clickPlayerID.get()), con);
			}else{
				//デバック用***********************************************************
				System.out.println("gameID:" + gameID.get());
				System.out.println("clickPlayerID:" + clickPlayerID.get());
				//***********************************************************
				gmdao.updateResult(gameID, clickPlayerID);
			}
			return bean;
		}

		// プレイモードに応じて次のターンの準備
		playMode.prepareNextTurn(gameID, con, clickPlayerID,turnTotalTime);

		bean.setMASSLIST(new MASSLIST(KekkaList));

		// ここからマスの状態によって、各イベントに分岐

		return bean;
	}
	// ClickActionメソッド

	// -------------------------------------------------------------------------------------
	// 爆弾が無いことが保障されるので好き勝手やろう
	// 開閉状態をupdateして、listに格納
	private List<Mass> massOpen(int mymassID, HashMap<Integer, Mass> allMassHash, List<Mass> returnMassList, int depth)
			throws SQLException, ClassNotFoundException {

		Mass ms = allMassHash.get(mymassID);

		// 再帰深さが1以上ならアイテム、クイズマスを開けない
		if (depth > 0) {
			if (ms.getQUIZID().get() != 0 || ms.getITEMID().get() != 0) {
				System.out.println("クイズアイテム");
				return returnMassList;
			}
		}

		// 再帰深さに1加算
		depth++;

		try {
			if (ms.getISOPEN().get()) {
				// System.out.println("あいてたー"+mymassID);
				return returnMassList;
			}
			ms.setISOPEN(new ISOPEN(true));

			returnMassList.add(ms); // 開けたマスを格納していく

			if (ms.getAROUNDMINES().get() == 0) { // 周囲に地雷が無いとき、周囲のマスも再帰的に開ける
				if (16 < mymassID) {
					if (mymassID % 16 != 1) {
						massOpen(mymassID - 17, allMassHash, returnMassList, depth);
					}
					massOpen(mymassID - 16, allMassHash, returnMassList, depth);
					if (mymassID % 16 != 0) {
						massOpen(mymassID - 15, allMassHash, returnMassList, depth);
					}
				}
				if (mymassID < 240) {
					if (mymassID % 16 != 1) {
						massOpen(mymassID + 15, allMassHash, returnMassList, depth);
					}
					massOpen(mymassID + 16, allMassHash, returnMassList, depth);
					if (mymassID % 16 != 0) {
						massOpen(mymassID + 17, allMassHash, returnMassList, depth);
					}
				}
				if (mymassID % 16 != 1) {
					massOpen(mymassID - 1, allMassHash, returnMassList, depth);
				}
				if (mymassID % 16 != 0) {
					massOpen(mymassID + 1, allMassHash, returnMassList, depth);
				}
			}
			// 周囲に地雷があるときはそのままリターン
			return returnMassList;
		} finally {
			// System.out.println("finallyを通りました。");
		}

	}// massOpenメソッド

}
