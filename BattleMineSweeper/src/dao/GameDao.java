package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.Game;
import domain.IsFinishedEnum;
import domain.Player;
import domain.value.ADID;
import domain.value.DIFFICULTYID;
import domain.value.FINALWINPLAYERID;
import domain.value.GAMEID;
import domain.value.PLAYER1ID;
import domain.value.PLAYER2ID;
import domain.value.PLAYERID;
import domain.value.PLAYMODE;
import domain.value.TURNPLAYERID;
import domain.value.TURNSTARTTIME;
import service.bean.ConfigBean;

/**
 * データベースのGAMEテーブルを操作するDAOです。
 *
 * @author
 *
 */
public class GameDao extends Dao {

	/**
	 * コンストラクタ
	 *
	 * @param con
	 */
	public GameDao(Connection con) {
		super(con);
	}

	/**
	 * GAMEテーブルからデータを取得するメソッドです。
	 *
	 * @param gameID ゲームのID
	 * @param player1ID プレイヤー1のID
	 * @param player2ID プレイヤー2のID
	 * @param difficultyID 難易度のID
	 *
	 * @return 条件に合致するデータ
	 *
	 * @throws SQLException
	 */
	public Game getGame(int gameID) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Game gs = null;

		try{
			/* Statementの作成 */
			stmt = con.prepareStatement(
						  " select "
						+ "   *"
						+ " from "
						+ "   GAME "
						+ " where "
						+ "   GAMEID = ? ");

			stmt.setInt(1, gameID);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();
			/* 取得したデータをgsに格納します。 */
			while (rset.next()){
					gs = new Game();
					gs.setGAMEID(new GAMEID(rset.getInt(1)));
					gs.setPLAYER1ID(new PLAYER1ID(rset.getInt(2)) );
					gs.setPLAYER2ID(new PLAYER2ID( rset.getInt(3)));
					gs.setDIFFICULTYID(new DIFFICULTYID(rset.getInt(4)));
					gs.setTURNPLAYERID(new TURNPLAYERID(rset.getInt(5)));
					gs.setFINALWINPLAYERID(new FINALWINPLAYERID(rset.getInt(6)));
					gs.setADID(new ADID(rset.getInt(7)) );
					gs.setPLAYMODE( PLAYMODE.getPLAYMODEByValue(rset.getString(8)) );
					gs.setTURNSTARTTIME(new TURNSTARTTIME( rset.getString(9) ));
					gs.setISFINISHED(IsFinishedEnum.valueOf( rset.getString(10) ));
				}
		  }

		catch (SQLException e) {
			throw e;
		}
		finally{
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}

		return gs;
	}

	/**
	 * ゲームIDを取得するメソッドです。
	 *
	 * @return
	 */
	private int getGameId(){//GameIdを順番に返す
		/* Statementの作成 */
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int gameID = -1;

		try {
			stmt = con.prepareStatement(
					  "select "
					+ "SEQ_GAMEID.nextval "
					+ "from "
					+ "dual "
					);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			while(rset.next()){
				gameID = rset.getInt(1);
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return gameID;
	}

	/**
	 * 取得した設定を用いてゲームを生成するメソッドです。
	 *
	 * @param player プレイヤーのデータ
	 * @param difficultyID 難易度ID
	 * @param playMode 対戦プレイのフラグ
	 * @return ゲームID
	 * @throws SQLException
	 */
	public GAMEID createGame(Player player, DIFFICULTYID difficultyID, PLAYMODE playMode) throws SQLException{

		int gameID = getGameId();

		PreparedStatement stmt = null;

		try{
			/* Statementの作成 */
			stmt = con.prepareStatement(
					  "insert "
				    + "   into GAME "
					+ "("
					+ "    	 GAMEID "
					+ "    	,PLAYER1ID "
					+ "   	,DIFFICULTYID "
					+ "   	,PLAYMODE "
					+ "   	,ISFINISHED "
					+ ")"
					+ "values "
					+ "("
					+ " 	 ?"
					+ "		,?"
					+ "		,?"
					+ "		,?"
					+ "		,?"
					+ ")"
					);

			//SQL文に代入
			stmt.setInt(1, gameID);
			stmt.setInt(2, player.getPLAYERID().get());
			stmt.setInt(3, difficultyID.get());
			stmt.setString(4, playMode.get());
			stmt.setString(5, IsFinishedEnum.UNFINISHED.name());


			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + gameID);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				stmt = null;
			}
		}

		return new GAMEID(gameID);

	}

	/**
	 * 取得したデータからシングルプレイ用のゲームを生成するメソッドです。
	 *
	 * @param player プレイヤーのデータ
	 * @param difficultyID 難易度ID
	 * @param playMode シングルプレイのフラグ
	 * @return ゲームID
	 * @throws SQLException
	 */
	public GAMEID createSingleGame(Player player, DIFFICULTYID difficultyID, PLAYMODE playMode) throws SQLException{

		int gameID = getGameId();

		PreparedStatement stmt = null;

		try{
			/* Statementの作成 */
			stmt = con.prepareStatement(
					  "insert "
				    + "   into GAME "
					+ "("
					+ "    	 GAMEID "
					+ "    	,PLAYER1ID "
					+ "   	,DIFFICULTYID "
					+ "   	,TURNPLAYERID "
					+ "   	,PLAYMODE "
					+ "   	,ISFINISHED "
					+ ")"
					+ "values "
					+ "("
					+ " 	 ?"
					+ "		,?"
					+ "		,?"
					+ "		,?"
					+ "		,?"
					+ "		,?"
					+ ")"
					);

			//SQL文に代入
			stmt.setInt(1, gameID);
			stmt.setInt(2, player.getPLAYERID().get());
			stmt.setInt(3, difficultyID.get());
			stmt.setInt(4, player.getPLAYERID().get());
			stmt.setString(5, playMode.get());
			stmt.setString(6, IsFinishedEnum.UNFINISHED.name());


			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + gameID);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				stmt = null;
			}
		}

		return new GAMEID(gameID);

	}


	/*TURNPLAYER更新用のSQL文*/
	private static final String TURNPLAYER_CHANGE_SQL =
		      " UPDATE"
	    	+ "   GAME"
			+ " SET "
			+ "   TURNPLAYERID ="
			+ "     CASE TURNPLAYERID"
			+ "       WHEN PLAYER1ID THEN PLAYER2ID"
			+ "       WHEN PLAYER2ID THEN PLAYER1ID"
			+ "     END,"
			+ "   TURNSTARTTIME = TO_CHAR( SYSTIMESTAMP, 'YYYY/MM/DD HH24:MI:SS.FF3')"
			+ " WHERE"
			+ "   GAMEID = ?";

	/*TURNPLAYER確認用のSQL文*/
	private static final String TURNPLAYER_CHECK_SQL =
		      " SELECT"
	    	+ "   TURNPLAYERID"
			+ " FROM "
			+ "   GAME"
			+ " WHERE"
			+ "   GAMEID = ?";


	/**
	 * マスを掘れるプレイヤーを切り替えるメソッドです。
	 *
	 * @param gameID ゲームID
	 * @throws SQLException
	 */
	public void updateTurn(GAMEID gameID_p)throws SQLException{

		PreparedStatement stmt = null;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(TURNPLAYER_CHANGE_SQL);
			stmt.setInt(1,gameID_p.get());
			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("更新するゲームがありません. ID:" + gameID_p);
			}
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				}
				stmt = null;
			}
		}
	}

	/**
	 * 指定したプレイヤーのターンであるか否かを判定するメソッドです。
	 *
	 * @param gameID ゲームID
	 * @param playerID プレイヤーID
	 * @return プレイヤーIDのプレイヤーのターンであるか否か
	 * @throws SQLException
	 */
	public boolean isMyTurn(GAMEID gameID, PLAYERID playerID) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int turnPlayerID = -1;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(TURNPLAYER_CHECK_SQL);

			stmt.setInt(1,gameID.get());

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			if( rset.next() ){
				turnPlayerID = rset.getInt(1);
			}else{
				throw new SQLException("ゲームがありません. ID:" + gameID);
			}
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				}
				stmt = null;
			}
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				}
				rset = null;
			}
		}

		//ターンプレイヤーIDと自分のIDが同じならtrue
		if(turnPlayerID == playerID.get()){
			return true;
		}

		return false;
	}


	/*FINALWINPLAYERID更新用のSQL文*/
	private static final String FINALWINPLAYERID_UPDATE_SQL =
		      " UPDATE"
	    	+ "   GAME"
			+ " SET "
			+ "   FINALWINPLAYERID = ?,"
			+ "   ISFINISHED = ?"
			+ " WHERE"
			+ "   GAMEID = ?";


	/**
	 * 勝敗結果を反映するメソッドです。
	 *
	 * @param gameID ゲームID
	 * @param winPlayerID 勝ったプレイヤーのID
	 * @throws SQLException
	 */

	public void updateResult(GAMEID gameID, PLAYERID winPlayerID)throws SQLException{
		PreparedStatement stmt = null;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(FINALWINPLAYERID_UPDATE_SQL);

			stmt.setInt(1,winPlayerID.get());
			stmt.setString(2,IsFinishedEnum.FINISHED.name());
			stmt.setInt(3,gameID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();

			if (lineCount != 1) {
				throw new SQLException("更新するゲームがありません. ID:" + gameID);
			}
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				}
				stmt = null;
			}
		}
	}

	/*ターン開始からの時間取得用のSQL文*/
	private static final String TURNELAPSEDTIME_GET_SQL =
		      " SELECT"
	    	+ "   TO_CHAR(SYSTIMESTAMP - TO_TIMESTAMP(TURNSTARTTIME, 'YYYY/MM/DD HH24:MI:SS.FF3'), 'YYYY/MM/DD HH24:MI:SS.FF3')"
			+ " FROM "
			+ "   GAME"
			+ " WHERE"
			+ "   GAMEID = ?";

	/*ターン開始からの時間取得用のSQL文デバッグ用*/
	private static final String TURNELAPSEDTIME_GET_SQL_DBUG =
		      " SELECT"
	    	+ "   TO_TIMESTAMP(TURNSTARTTIME, 'YYYY/MM/DD HH24:MI:SS.FF3')"
			+ " FROM "
			+ "   GAME"
			+ " WHERE"
			+ "   GAMEID = ?";



	/**
	 * マスをクリックするまでに経過した時間を取得するメソッドです。
	 *
	 * @param gameID ゲームID
	 * @return クリックまでの経過時間
	 * @throws SQLException
	 */
	//マス選択の経過時間取得
	public String getElapsedTime(GAMEID gameID) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String elapsedTime = null;
		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(TURNELAPSEDTIME_GET_SQL);

			stmt.setInt(1,gameID.get());

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			if( rset.next() ){
				elapsedTime = rset.getString(1);
			}else{
				throw new SQLException("ゲームがありません. ID:" + gameID);
			}
		}catch(SQLException e){
			throw new SQLException(gameID.toString(),e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				}
				stmt = null;
			}
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				}
				rset = null;
			}
		}

		return elapsedTime;
	}



	/**
	 * ゲームIDからを反映します。
	 * @param gameList
	 * @throws SQLException
	 */
//	public void updateResult(GAMEID gameID_p, PLAYERID winPlayerID_p)throws SQLException{
//		PreparedStatement stmt = null;
//
//		try {
//			/* Statementの作成 */
//			stmt = con.prepareStatement(FINALWINPLAYERID_UPDATE_SQL);
//
//			stmt.setInt(1,winPlayerID_p.get());
//			stmt.setString(2,IsFinishedEnum.FINISHED.name());
//			stmt.setInt(3,gameID_p.get());
//
//			/* ｓｑｌ実行 */
//			int lineCount = stmt.executeUpdate();
//
//			if (lineCount != 1) {
//				throw new SQLException("更新するゲームがありません. ID:" + gameID_p);
//			}
//		} finally {
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//					throw e;
//				}
//				stmt = null;
//			}
//		}
//	}

	/**
	 * 指定したゲームから一人目のプレイヤーのIDを取得するメソッドです。
	 *
	 * @param gameID ゲームのID
	 * @return 指定したゲームにおける一人目のプレイヤーのID
	 * @throws SQLException
	 */
	public String getPlayer1Name(int gameID) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String player1Name = null;

		try{
			/* Statementの作成 */
			stmt = con.prepareStatement(
						  " select "
						+ "  USERNAME  "
						+ " from "
						+ "   GAME "
						+ "  JOIN  "
						+ " PLAYER "
						+ "   ON "
						+ " GAME.PLAYER1ID = PLAYER.PLAYERID "
						+ "  JOIN  "
						+ " USERS "
						+ "   ON "
						+ " PLAYER.USERID = USERS.USERID "
						+ " where "
						+ "   GAMEID = ? ");

			stmt.setInt(1, gameID);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			rset.next();
			//System.out.println(rset.getString(1));
			player1Name = rset.getString(1);
		}

		catch (SQLException e) {
			throw e;
		}
		finally{
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}

		return player1Name;
	}

	/**
	 * 指定したゲームから二人目のプレイヤーのIDを取得するメソッドです。
	 *
	 * @param gameID ゲームのID
	 * @return 指定したゲームにおける二人目のプレイヤーのID
	 * @throws SQLException
	 */
	public String getPlayer2Name(int gameID) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String player2Name = null;

		try{
			/* Statementの作成 */
			stmt = con.prepareStatement(
						  " select "
						+ "  USERNAME  "
						+ " from "
						+ "   GAME "
						+ "  JOIN  "
						+ " PLAYER "
						+ "   ON "
						+ " GAME.PLAYER2ID = PLAYER.PLAYERID "
						+ "  JOIN  "
						+ " USERS "
						+ "   ON "
						+ " PLAYER.USERID = USERS.USERID "
						+ " where "
						+ "   GAMEID = ? ");

			stmt.setInt(1, gameID);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			rset.next();
			//System.out.println(rset.getString(1));
			player2Name = rset.getString(1);
		}

		catch (SQLException e) {
			throw e;
		}
		finally{
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}

		return player2Name;
	}

	//マッチング用
	private final String MATCHING_GAME_SQL =
		      " UPDATE"
	    	+ "   GAME"
			+ " SET "
			+ "   PLAYER2ID = ?,"
			+ "   TURNPLAYERID = ?"
			+ " WHERE"
			+ "   PLAYER2ID IS NULL"
			+ " AND"
			+ "   DIFFICULTYID = ?"
			+ " AND"
			+ "   PLAYMODE = 2";

	//マッチングしたゲームのID取得用
	private final String GET_GAMEID_SQL =
			  " SELECT"
			+ "   GAMEID"
			+ " FROM "
			+ "   GAME"
			+ " WHERE"
			+ "   PLAYER2ID = ?";


	/**
	 * マッチングするゲームのIDを取得するメソッドです。
	 *
	 * @param player プレイヤーの情報
	 * @param cbean 難易度とキャラクター
	 * @return ゲームのID
	 * @throws SQLException
	 */
	public GAMEID getMatchGameID(Player player, ConfigBean cbean) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		GAMEID gameID = null;

		//マッチング
		try{
			stmt = con.prepareStatement( MATCHING_GAME_SQL );

			stmt.setInt(1, player.getPLAYERID().get());
			stmt.setInt(2, player.getPLAYERID().get());
			stmt.setInt(3, cbean.getDifficultyID().get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();

			if (lineCount == 0) {
				//マッチング待ちゲームが無いのでnullを返す。
				return null;
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally{
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}

		//マッチングしたゲームID取得
		try{
			stmt = con.prepareStatement( GET_GAMEID_SQL );

			stmt.setInt(1, player.getPLAYERID().get());

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			rset.next();
			gameID = new GAMEID(rset.getInt(1));
		}
		catch (SQLException e) {
			throw e;
		}
		finally{
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}

		return gameID;
	}

}
