package huawei.texaspoker;

import java.util.List;
import java.util.Map;


public class pokerStrategy {

/**
 * ���ߺ����Ķ���
 */
	private int Betdecision_Raise=0;
	private int Betdecision_Call=1;
	private int Betdecision_Fold=2;
    
	private int[][] betterSeatHoleCards={};//λ�ú�ʱ��������������
	private int[][] worseSeatHoleCards={};//λ�ò�̫��ʱ��������������
	
	private int currentHand;//��ǰ������
	
	private Map<Integer,historyPlayer> historyMap;//��ʷ������Ϣ ���յ� ��ѯ����Ϣ�� ��̯����Ϣ����ʱ��������
	
    private List<Card> holeCards;//ÿ��ȡ�õ����ƣ��Լ��������ƣ�  ���յ�  ��������Ϣ��  ��ʱ��ִ�� ���
    private List<Card> sharedCards;//������  ���յ� ��������Ϣ����ת����Ϣ����������Ϣ�� ��ʱ�������� 
    
    private int currentSeat;//��ǰλ����Ϣ ��һ���ƽ����ж� �Ժ��1mod(8);1-8 1����λ�����

    

/**
 *  ����ǰ��������ǿ�Ƚ��о����ж� 
 */
	public int preFlopDesicion(){
	// TODO Auto-generated method stub
		if(currentSeat<=2) return betterSeatHoleCards[holeCards.get(0).number][holeCards.get(1).number];
		else return worseSeatHoleCards[holeCards.get(0).number][holeCards.get(1).number];
	}
/**
 *  ���ĺ��������ǿ�Ƚ��о����ж� 
 */
	public int afterFlop(){
	// TODO Auto-generated method stub
		
		return 0;
	}
	
}
