package huawei.texaspoker;

import java.util.HashMap;
import java.util.Map;

public class MessageHead {
	 public final String SEAT="seat"; //������Ϣ
	 public final String GAME_OVER="game-over"; //��Ϸ������Ϣ
	 public final String BINLD="blind"; //äע��Ϣ
	 public final String HOLD="hold"; //������Ϣ
	 public final String INQUIRE="inquire"; //ѯ����Ϣ
	 public final String FLOP="flop"; //������Ϣ
	 public final String TURN="turn"; //ת����Ϣ
	 public final String RIVER="river"; //������Ϣ
	 public final String SHUTDOWN="shutdown"; //̯����Ϣ
	 public final String POT_WIN="pot-win"; //���ط���
	 public final Map<String, Integer> map=new HashMap<String, Integer>();
	 {
		 map.put(SEAT, 1);
		 map.put(BINLD, 2);
		 map.put(HOLD, 3);
		 map.put(INQUIRE, 4);
		 map.put(FLOP, 5);
		 map.put(TURN, 6);
		 map.put(RIVER, 7);
		 map.put(SHUTDOWN, 8);
		 map.put(POT_WIN, 9);
		 map.put(GAME_OVER, 10);
	 }
}
