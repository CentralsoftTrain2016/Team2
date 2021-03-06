package service;

import java.sql.Connection;
import java.sql.SQLException;

import dao.CharacterDao;
import domain.Character;
import service.bean.ConfigBean;

/**
 * 指定したキャラクターの情報をデータベースから取得するサービスです。
 *
 * @author junichi.furukawa
 *
 */
public class CharacterService extends Service {

	/**
	 * コンストラクタ
	 */
	public CharacterService() {
		super();
	}

	/**
	 * コンストラクタ
	 *
	 * @param connection
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public CharacterService(Connection connection) throws SQLException, ClassNotFoundException {
		super(connection);
	}

	/**
	 * コンストラクタ
	 *
	 * @param connectService
	 */
	public CharacterService(Service connectService) {
		super(connectService);
	}

	/**
	 * データベースからキャラクターの情報を取得するメソッドです。
	 *
	 * @param config_p
	 * @return
	 * @throws SQLException
	 */
	public Character getCharacterInfomation(ConfigBean config_p) throws SQLException{
		ConfigBean config = config_p;
		CharacterDao charD = new CharacterDao(con);
		Character selectChar = charD.getCharacterInfo(config.getCharacterID().get());
		return selectChar;
	}
}