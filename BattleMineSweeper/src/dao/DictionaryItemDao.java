package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.DictionaryItem;
import domain.value.INFORMATIONDESCRIPTION;
import domain.value.INFORMATIONID;
import domain.value.INFORMATIONNUMBER;

/**
 * データベースのDICTIONARYITEMテーブルを操作するDAOです。
 *
 * @author
 *
 */
public class DictionaryItemDao extends Dao{

	/**
	 * コンストラクタ
	 * @param con
	 */
	public DictionaryItemDao(Connection con) {
		super(con);
	}

	/**
	 * DICTIONARYITEMテーブルからデータを取得します。
	 *
	 * @param informationID 地雷の知識のID
	 * @return 条件に合致するデータ
	 * @throws SQLException
	 */
	public DictionaryItem getDictionaryItem(int informationID) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		DictionaryItem dis = null;

		try{
			/* Statementの作成 */
			stmt = con.prepareStatement(
						  " select "
						+ "   *"
						+ " from "
						+ "   DICTIONARYITEM "
						+ " where "
						+ "   INFORMATIONID = ?");

			stmt.setInt(1, informationID);

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();
			/* 取得したデータをdisに格納します。 */
			while (rset.next()){
					dis = new DictionaryItem();
					dis.setINFORMATIONID(new INFORMATIONID(rset.getInt(1)));
					dis.setINFORMATIONNUMBER(new INFORMATIONNUMBER(rset.getInt(2)));
					dis.setDESCRIPTION( new INFORMATIONDESCRIPTION(rset.getString(3)));
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

		return dis;
	}

	/**
	 * 図鑑の数を数える
	 *
	 * @return 図鑑の数
	 * @throws SQLException
	 */
	public int dictionaryCount() throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int dictionaryTotal = 0;


		try{
			/* Statementの作成 */
			stmt = con.prepareStatement(
						  " select "
						+ " count(informationid)"
						+ " from "
						+ " DICTIONARYITEM ");

			/* ｓｑｌ実行 */
			rset = stmt.executeQuery();
			/* 取得したデータをdisに格納します。 */
			while (rset.next()){
				dictionaryTotal = rset.getInt(1);

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


		return dictionaryTotal;
	}
}
