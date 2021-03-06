package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.CharacterSkillEnum;
import domain.Player;
import domain.value.CHARACTERID;
import domain.value.COOLTIME;
import domain.value.ClickPlayerID;
import domain.value.GAMEID;
import domain.value.ITEMID;
import domain.value.PLAYERID;
import domain.value.TIMELIMIT;
import domain.value.TOTALTIME;
import domain.value.USERID;

public class PlayerDao extends Dao{

	/**
	 * コンストラクタ
	 * @param con
	 */
	public PlayerDao(Connection con) {
		super(con);
	}

	/**
	 *
	 * @param playerID
	 * @return
	 * @throws SQLException
	 */
	public Player getPlayer(int playerID) throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rset = null;
		Player pm = null;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					      " select "
					    + "   *"
						+ " from "
						+ "   PLAYER "
						+ "where "
						+ "  PLAYERID =? "
						);

			//SQL文に代入
			stmt.setInt(1, playerID);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			/* 取得したデータを表示します。 */
			while (rset.next()) {
				pm = new Player();
				pm.setPLAYERID(new PLAYERID(rset.getInt(1)));
				pm.setUSERID(new USERID(rset.getInt(2)));
				pm.setCHARACTERID(new CHARACTERID(rset.getInt(3)));
				pm.setTOTALTIME(new TOTALTIME(rset.getInt(4)));
				pm.setITEMID(new ITEMID(rset.getInt(5)));
				pm.setCOOLTIME(new COOLTIME(rset.getInt(6)));
				pm.setTIMELIMIT(new TIMELIMIT(rset.getInt(7)));

				// System.out.println(rset.getString(1));
			}
		}

		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}

		return pm;
	}

	//シーケンス使って登録予定ID取得
	private int getNewPlayerID() throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int playerID = -1;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					"select seq_playerid.nextval from dual"
					);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();

			/* 取得したデータを表示します。 */
			while (rset.next()) {
				playerID = rset.getInt(1);
			}
		}

		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}

		return playerID;

	}

	//プレイヤーDB登録用SQL
	private final String INSERT_PLAYER_SQL =
		      " insert "
			+ "   into PLAYER"
			+ "("
			+ "    	PLAYERID "
			+ "   	,USERID "
			+ "   	,CHARACTERID "
			+ "   	,TOTALTIME "
			+ "   	,ITEMID "
			+ "   	,COOLTIME "
			+ "   	,TIMELIMIT "
			+ ")"
			+ "values "
			+ "("
			+ " 	?"
			+ "		,?"
			+ "		,?"
			+ "		,?"
			+ "		,?"
			+ "		,?"
			+ "		,?"
			+ ")";


	//プレイヤーDB登録
	public Player createPlayer(USERID userID, CHARACTERID characterID) throws SQLException {

		PreparedStatement stmt = null;
		Player player = new Player();

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(INSERT_PLAYER_SQL);

			//登録予定のplayerID取得
			int playerID = getNewPlayerID();

			//SQL文に代入
			stmt.setInt(1, playerID);
			stmt.setInt(2, userID.get());
			stmt.setInt(3, characterID.get());
			stmt.setInt(4, 0);
			stmt.setInt(5, 0);
			stmt.setInt(6, CharacterSkillEnum.getSkillEnumByValue(characterID.get()).getCoolTime().get());
			stmt.setInt(7, 10);

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();

			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}

			player.setPLAYERID(new PLAYERID(playerID));
			player.setUSERID(new USERID(userID.get()));
			player.setCHARACTERID(new CHARACTERID(characterID.get()));
			player.setTOTALTIME(new TOTALTIME(0));
			player.setITEMID(new ITEMID(0));
			player.setCOOLTIME(new COOLTIME(CharacterSkillEnum.getSkillEnumByValue(characterID.get()).getCoolTime().get()));
			player.setTIMELIMIT(new TIMELIMIT(10));
		}

		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}

		return player;
	}

	//プレイヤーDB登録
	public Player createSinglePlayer(USERID userID) throws SQLException {

		PreparedStatement stmt = null;
		Player player = new Player();

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(INSERT_PLAYER_SQL);

			//登録予定のplayerID取得
			int playerID = getNewPlayerID();

			//SQL文に代入
			stmt.setInt(1, playerID);
			stmt.setInt(2, userID.get());
			stmt.setInt(3, 0);
			stmt.setInt(4, 0);
			stmt.setInt(5, 0);
			stmt.setInt(6, 0);
			stmt.setInt(7, 10);

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();

			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}

			player.setPLAYERID(new PLAYERID(playerID));
			player.setUSERID(new USERID(userID.get()));
			player.setCHARACTERID(new CHARACTERID(0));
			player.setTOTALTIME(new TOTALTIME(0));
			player.setITEMID(new ITEMID(0));
			player.setCOOLTIME(new COOLTIME(0));
			player.setTIMELIMIT(new TIMELIMIT(10));
		}

		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}

		return player;
	}

	/**アイテムを取得する(ItemIDをセット)
	 * @param PLAYERID
	 * @return void
	 */
	public void setItem(PLAYERID playerID,int itemID)
			throws SQLException,ClassNotFoundException{
		PreparedStatement stmt = null;
		ResultSet rset = null;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					      " update "
					    + "   PLAYER "
						+ " set "
						+ "   ITEMID = ?  "
						+ "where "
						+ "  PLAYERID =? "
						);

			//SQL文に代入
			stmt.setInt(1, itemID);
			stmt.setInt(2, playerID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}
		}
		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}
	}

	/**アイテムを消費する(ItemIDに０をセット)
	 *
	 * @param PLAYERID
	 * @return void
	 */
	public void consumeItem(PLAYERID playerID)
			throws SQLException,ClassNotFoundException{
		PreparedStatement stmt = null;
		ResultSet rset = null;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					      " update "
					    + "   PLAYER "
						+ " set "
						+ "   ITEMID = 0  "
						+ "where "
						+ "  PLAYERID =? "
						);

			//SQL文に代入
			stmt.setInt(1, playerID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}
		}
		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}


	}

	/**coolTimeに固定値をセットする
	 *
	 * @param playerID
	 * @param coolTime
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void setCoolTime(ClickPlayerID playerID, COOLTIME coolTime)
			throws SQLException,ClassNotFoundException{
		PreparedStatement stmt = null;
		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					" update "
							+ "   PLAYER "
							+ " set "
							+ "   COOLTIME = ?  "
							+ "where "
							+ "  PLAYERID =? "
					);

			//SQL文に代入
			stmt.setInt(1, coolTime.get());
			stmt.setInt(2, playerID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}

		}

		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}

	}

	/**coolTimeを今の値から減少させる
	 *
	 * @param playerID
	 * @param reduseNumber
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void decreaseCoolTime(PLAYERID playerID, int reduseNumber)
			throws SQLException,ClassNotFoundException{
		PreparedStatement stmt = null;
		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					" update "
							+ "   PLAYER "
							+ " set "
							+ "   COOLTIME = COOLTIME - ?  "
							+ "where "
							+ "  PLAYERID =? "
					);

			//SQL文に代入
			stmt.setInt(1, reduseNumber);
			stmt.setInt(2, playerID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}

		}

		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}
	}
	/**coolTimeを今の値から減少させる
	 *
	 * @param gameID
	 * @param reduseNumber
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void decreaseCoolTime(GAMEID gameID, int reduseNumber)
			throws SQLException,ClassNotFoundException{
		PreparedStatement stmt = null;
		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					" update "
							+ "   PLAYER "
							+ " set "
							+ "   COOLTIME = COOLTIME - ?  "
							+ " where "
							+ "  PLAYERID in (  "
							+ "  ( select PLAYER1ID  "
							+ "  from GAME  "
							+ "  where GAMEID = ? ),"
							+ "  (select PLAYER2ID "
							+ "   from GAME "
							+ "   where GAMEID = ? "
							+ "  ) )" );

			//SQL文に代入
			stmt.setInt(1, reduseNumber);
			stmt.setInt(2, gameID.get());
			stmt.setInt(3, gameID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount == 0) {
				throw new SQLException("登録できません. ID:" + gameID);
			}

		}

		catch (SQLException e) {
			throw e;
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}

	}

	// アイテムのアップデート
	public void updateItemId(ClickPlayerID playerID, ITEMID itemid)
			throws SQLException{
		PreparedStatement stmt = null;
		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					" update "
							+ "   PLAYER "
							+ " set "
							+ "   ITEMID = ?  "
							+ "where "
							+ "  PLAYERID =? "
					);

			//SQL文に代入
			stmt.setInt(1, itemid.get());
			stmt.setInt(2, playerID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}


		}
		finally {

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}

	}

	//プレイヤーのトータル経過時間を加算
	public void totalTime(int turnTotalTime, PLAYERID playerID){
		PreparedStatement stmt = null;

		System.out.println(turnTotalTime);
		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					" update "
							+ "   PLAYER "
							+ " set "
							+ "   TOTALTIME = TOTALTIME + ?  "
							+ "where "
							+ "  PLAYERID = ? "
					);

			//SQL文に代入
			stmt.setInt(1, turnTotalTime);
			stmt.setInt(2, playerID.get());

			/* ｓｑｌ実行 */
			int lineCount = stmt.executeUpdate();
			if (lineCount != 1) {
				throw new SQLException("登録できません. ID:" + playerID);
			}

		}

		catch (SQLException e) {
			try {
				throw e;
			} catch (SQLException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}
		catch ( Exception e){
			throw e;
		}
		finally {

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				stmt = null;
			}
		}
	}

}
