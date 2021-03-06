package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.Difficulty;
import domain.value.DIFFICULTYID;
import domain.value.DIFFICULTYNAME;
import domain.value.MINES;

/**
 * データベースのDIFFICULTYテーブルを操作するDAOです。
 *
 * @author junichi.furukawa
 *
 */
public class DifficultyDao extends Dao {

	/**
	 * コンストラクタ
	 *
	 * @param con
	 */
	public DifficultyDao(Connection con) {
		super(con);
	}

	/**
	 * 指定した難易度のデータを取得するメソッドです。
	 *
	 * @param difficultyID
	 * @return
	 * @throws SQLException
	 */
	public Difficulty getDifficulty(int difficultyID) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Difficulty ds = null;

		try {
			/* Statementの作成 */
			stmt = con.prepareStatement(
					" select " + "   *" + " from " + "   DIFFICULTY " + " where " + "   DIFFICULTYID = ?");

			stmt.setInt(1, difficultyID);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();
			/* 取得したデータをdisに格納します。 */
			while (rset.next()) {
				ds = new Difficulty();
				ds.setDIFFICULTYID(new DIFFICULTYID(rset.getInt(1)));
				ds.setDIFFICULTYNAME(new DIFFICULTYNAME(rset.getString(2)));
				ds.setMINES(new MINES(rset.getInt(3)));
			}
		}catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rset != null) {
				rset.close();
				rset = null;
			}
		}

		return ds;
	}

}
