package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dao.DifficultyDao;
import dao.MassDao;
import dao.QuizDao;
import domain.Difficulty;
import domain.Mass;
import domain.value.AROUNDMINES;
import domain.value.GAMEID;
import domain.value.ISEXISTMINE;
import domain.value.ISOPEN;
import domain.value.ITEMID;
import domain.value.MASSID;
import domain.value.PLAYMODE;
import domain.value.QUIZID;

public class FieldService extends Service{

	public FieldService() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}



	public FieldService(Connection connection) throws SQLException, ClassNotFoundException {
		super(connection);
		// TODO 自動生成されたコンストラクター・スタブ
	}



	public FieldService(Service connectService) {
		super(connectService);
		// TODO 自動生成されたコンストラクター・スタブ
	}



	// ゲームのマス生成
	public void fieldPlacement(int gameid, int difficultyid, PLAYMODE playMode ) {
		final int HEIGHT = 16;  // 縦の数
		final int SIDE = 16;    // 横の数
		final int MASSFIRSTNUM = 1; // マス番号の初め
		final int MASSLASTNUM = HEIGHT*SIDE;  // マス番号の終わり
		final int QUIZQUANTITY = 3;  // クイズ個数
		final int ITEMQUANTITY = 3;  // アイテム個数
		List<Mass>masslist = new ArrayList<Mass>();

		try{
		// 初期状態の mass を生成
		for (int i = 1 ; i <= MASSLASTNUM ; i++){
			Mass mass = new Mass();
			mass.setMASSID(new MASSID(i));
			mass.setGAMEID(new GAMEID(gameid));
			mass.setISEXISTMINE(new ISEXISTMINE(false));
			mass.setAROUNDMINES(new AROUNDMINES(0));
			mass.setISOPEN(new ISOPEN(false));
			mass.setITEMID(new ITEMID(0));
			mass.setQUIZID(new QUIZID(0));

			// リストに格納
			masslist.add(mass);
		}

		// マス番号の数がランダムに入っているリストを取得。
		List<Integer>randomlist = randomList(MASSFIRSTNUM,MASSLASTNUM);


		// 爆弾があるマスIDをリストで生成する。
		List<Integer>minelist = getMineList(randomlist,difficultyid);

		// 地雷の有無をセットする。
		for (int j = 0 ; j < minelist.size() ; j++){
			masslist.get(minelist.get(j)-1).setISEXISTMINE(new ISEXISTMINE(true));
		}


		// 周りの地雷の数を数えてセットする。

		int minesum =0;    // 爆弾の数
		int xPoint = 0;    // x座標
		int yPoint = 0 ;   // y座標

		for(int i = MASSFIRSTNUM ; i <= MASSLASTNUM ; i++){
			minesum = 0;   // 初期は0にセット
			xPoint = (i-1)%SIDE;    // x座標のセット
			yPoint = (i-1)/HEIGHT;  // y座標のセット

			// 確認用
//			System.out.println(i);
//			System.out.println(xPoint);
//			System.out.println(yPoint);


			// 自身のマスを含む周囲9マスの地雷の確認
			for(int x = 0 ; x < 3  ; x++){
				if((xPoint-1+x)<0 ||(xPoint-1+x)>=SIDE )continue;
				for(int y = 0 ; y < 3 ; y++){
					if((yPoint-1+y)<0 || (yPoint-1+y)>=HEIGHT)continue;
					// 地雷かどうかは確認
					if(masslist.get((yPoint -1 + y)*SIDE + (xPoint-1 + x)).getISEXISTMINE().get()){
						// 地雷の数を1追加する
						minesum++;
					}
				}
			}

			// 自身のマスを含む周囲9マスの地雷の数をセット
			masslist.get(i-1).setAROUNDMINES(new AROUNDMINES(minesum));



			// 確認用
			if(masslist.get(i-1).getISEXISTMINE().get()){
				System.out.print("*");
			}
			else if(masslist.get(i-1).getAROUNDMINES().get()==0){
				System.out.print(" ");
			}
			else{
				System.out.print(masslist.get(i-1).getAROUNDMINES().get());
			}
			if(i%SIDE==0){
				System.out.print("\n");
			}
		}


		// クイズとアイテムをセット
		if(playMode == PLAYMODE.BATTLE_MODE){
			//クイズをセット

			// クイズがあるマスIDをリストで生成する。
			List<Integer>quizlist = getQuizList(randomlist,minelist.size(),QUIZQUANTITY);
			// クイズの番号をランダムにリストに生成する。
			List<Integer>quiznumlist = getQuiznumList();
			// クイズの有無をセットする。
			for (int k=0 ; k < quizlist.size() ; k++){
				masslist.get(quizlist.get(k)-1).setQUIZID(new QUIZID(quiznumlist.get(k)));
			}

			// アイテムをセット

			// アイテムがあるマスIDをリストで生成する。
			List<Integer>itemlist = getItemList(randomlist,minelist.size(),QUIZQUANTITY,ITEMQUANTITY);
			// アイテムの有無をセットする。
			for (int l=0 ; l < itemlist.size() ; l++){
				masslist.get(itemlist.get(l)-1).setITEMID(new ITEMID(l+1));
			}
		}

		//Massをデータベースに保存

		MassDao massdao =  new MassDao(con);
		massdao.createMass(masslist);
		}  catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
	}


	// マス番号の数がランダムに入っているリストを取得。
	public List<Integer> randomList(int massfirst,int massend) {
		List<Integer>randomfieldlist = new ArrayList<Integer>(); // マスIDのリスト
		// フィールドマス番号
		for(int i = massfirst ; i <= massend ; i++){
			randomfieldlist.add(i);
		}

		// ランダムに順番入れ替え
		Collections.shuffle(randomfieldlist);

		return randomfieldlist;
	}



	// 爆弾があるマスIDをリストで生成
	public List<Integer> getMineList(List<Integer>randomlist , int difficultyid) {
		int minequantity = 0; //爆弾数
		Difficulty df = new Difficulty(); //難易度データ
		List<Integer>randomminelist=new ArrayList<Integer>(); //爆弾があるマスIDのリスト

		try{
			//con = Dao.getConnection();
			DifficultyDao difficultydao =  new DifficultyDao(con);
			df = difficultydao.getDifficulty(difficultyid);
			// 爆弾数の取得
			minequantity=df.getMINES().get();

			// 地雷数に応じてマス番号をリストに保存
			for(int j = 0 ; j < minequantity ; j++){
				randomminelist.add(randomlist.get(j));
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}

		return randomminelist;
	}



	// クイズがあるマスIDをリストで生成
	public List<Integer> getQuizList(List<Integer>randomlist,int minequantity,int quizquantity) {
		List<Integer>randomquizlist=new ArrayList<Integer>(); //クイズがあるマスIDのリスト

		// クイズ数に応じてマス番号をリストに保存
		for(int j = minequantity+1 ; j < minequantity+quizquantity+1 ; j++){
			randomquizlist.add(randomlist.get(j));
		}

		return randomquizlist;
	}


	// シャッフルしたクイズIDをリストで生成
	private List<Integer> getQuiznumList() {
		List<Integer>randomquiznumlist=new ArrayList<Integer>(); //シャッフルしたクイズIDのリスト
		int quizquantitysum = 0;

		try{
			//con = Dao.getConnection();
			QuizDao qdao =  new QuizDao(con);
			//クイズIDリスト取得
			randomquiznumlist = qdao.getQuizQuantity();
			Collections.shuffle(randomquiznumlist);

			// クイズ数に応じてリストに保存
			for(int j = 1 ; j < quizquantitysum+1 ; j++){
				randomquiznumlist.add(j);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}

		return randomquiznumlist;
	}


	// アイテムがあるマスIDをリストで生成
	public List<Integer> getItemList(List<Integer>randomlist,int minequantity,int quizquantity,int itemquantity) {
		List<Integer>randomitemlist=new ArrayList<Integer>(); //アイテムがあるマスIDのリスト

		// アイテム数に応じてマス番号をリストに保存
		for(int j = minequantity+quizquantity+1 ; j < minequantity+quizquantity+itemquantity+1 ; j++){
			randomitemlist.add(randomlist.get(j));
		}

		return randomitemlist;
	}


//	public static void main(String[] args) throws ClassNotFoundException, SQLException{
//		FieldService fs = new FieldService();
//		fs.fieldPlacement(1, 1);
//		List<Integer>minelist = new ArrayList<Integer>();
//		minelist = fs.minePlacement(1, 256);
//		System.out.println(minelist);
//	}
}
