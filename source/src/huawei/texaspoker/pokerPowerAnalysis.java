package huawei.texaspoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ������ǰ�����빫���Ƶ���ϵ����� ��������ǿ��ֵ0-n 0��ʾ����
 */
public class pokerPowerAnalysis {
	private List<Card> holeCards=new ArrayList<Card>();
	private List<Card> sharedCards=new ArrayList<Card>();
	private List<Card> allCards=new ArrayList<Card>(); // ȫ����
	//��ǰ����������
	public int[] currentMaxHand=new int[5];
	public int[] longestStraight=new int[4];

	/**
	 * ���캯�� �������ƺ͹�����
	 * 
	 * @param holeCards
	 *            ����
	 * @param sharedCards
	 *            ������
	 */
	public pokerPowerAnalysis(List<Card> holeCards, List<Card> sharedCards) {
		this.holeCards = holeCards;
		this.sharedCards = sharedCards;
		/*allCards.addAll(this.holeCards);
		allCards.addAll(this.sharedCards);*/
	}

	/**
	 * ����ִ�����ƹ�������ϵ��������� ������ǿ����ֵ
	 * 
	 * @return
	 */
	public int pokerPowerRankValue() {
		allCards.addAll(holeCards);
		allCards.addAll(sharedCards);
		Map<Integer, Integer> numberOfCards = getNumberOfCards(allCards);// ��ȡ�������еĲ�ͬ��������
		Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);// ��ȡ�������еĻ�ɫ��
		if(getFlushStraight(numberOfCards, suitOfCards)>0){
			return getFlushStraight(numberOfCards, suitOfCards);
		}
		if(getFourOfaKing(numberOfCards)>0){
			return getFourOfaKing(numberOfCards);
		}
		if(getFullHouse(numberOfCards)>0){
			return getFullHouse(numberOfCards);
		}
		if(getFlush(suitOfCards)>0){
			return getFlush(suitOfCards);
		}
		if(getStraight(numberOfCards)>0){
			return getStraight(numberOfCards);
		}
		if(getSet(numberOfCards)>0){
			return getSet(numberOfCards);
		}
		if(getTwoPairs(numberOfCards)>0){
			return getTwoPairs(numberOfCards);
		}
		if(getSinglePair(numberOfCards)>0){
			return getSinglePair(numberOfCards);
		}
		return getHighCard(numberOfCards);
	}

	/**
	 * ͬ��˳
	 * @param suitOfCards 80-89
	 * @return
	 */
	public int getFlushStraight(Map<Integer, Integer> numberOfCards,Map<Integer, Integer> suitOfCards ) {
		int colorOfFlush=-1;//��¼ͬ���Ļ�ɫ
		for(Integer color:suitOfCards.keySet()){
			if(suitOfCards.get(color)>=5){
				colorOfFlush=color;
				break;
			}
		}
		Map<Integer,Integer> flushMap=new HashMap<Integer,Integer>();
		if(colorOfFlush>=0){
			//int[] flushComnbs=new int[7];//�洢��ǰ�����ϵ�ͬ����
			int index=0;
			for(Card card:allCards){
				if(card.suit==colorOfFlush){
					flushMap.put(card.number, 1); 		
				}
			}
			if(getStraight(flushMap)>0){
				return getStraight(flushMap)+40;
			}
		}		
		return 0;	
	}
	
	/**
	 * �ж��Ƿ����ը��  70-79
	 * @return
	 */
	public int getFourOfaKing(Map<Integer, Integer> numberOfCards) {
		int fourNumber=0;
		boolean Flag_hasFourOfaKing=false;
		int hignNumber=0;	
		for(Integer number:numberOfCards.keySet()){
			if(numberOfCards.get(number)==4){
				fourNumber=number;
				Flag_hasFourOfaKing=true;
			}else{
				if(number>hignNumber)hignNumber=number;
			}
		}
		if(Flag_hasFourOfaKing){
			int[] fourOfaKingComnbs=new int[5];
			fourOfaKingComnbs[4]=fourNumber;
			fourOfaKingComnbs[3]=fourNumber;
			fourOfaKingComnbs[2]=fourNumber;
			fourOfaKingComnbs[1]=fourNumber;
			fourOfaKingComnbs[0]=hignNumber;
			currentMaxHand=fourOfaKingComnbs;
		/**
		 * �������� 
		 * 1.������һ�Ż����� forNumber�� ����λ��Ϊ9��	 ������ forNumber��  �����ж����� ����λ��Ϊ9��
		 * 2.���� ����λ��Ϊ0��
		 */
			if(holeCards.get(0).number==fourNumber||holeCards.get(1).number==fourNumber||hignNumber==14||(fourNumber==14&&hignNumber==13)){
				return 79;
			}else{
				return 70;
			}
		}
		return 0;
		
	}
	/**
	 * �ж��Ƿ���ں�« 60-69
	 * @return
	 */
	public int getFullHouse(Map<Integer, Integer> numberOfCards) {
		boolean Flag_tri=false;
		boolean Flag_dou=false;
		int triNumber=0;
		int douNumber=0;
		int highCardNumber=0;
		for(Integer number:numberOfCards.keySet()){		
			if(numberOfCards.get(number)==3){
			//	if(number==1)number=14;
				if(!Flag_tri){
					triNumber=number;
					Flag_tri=true;
				}
				else {
					if(number>triNumber){
						int tmp=triNumber;
						triNumber=number;
						if(tmp>douNumber)douNumber=tmp;//�������Ķ��Ӻ�2��
					}
					Flag_dou=true;
				}
			}else{
				if(numberOfCards.get(number)==2){
				//	if(number==1)number=14;
					Flag_dou=true;
					if(number>douNumber)douNumber=number;
				}else{
					if(number>highCardNumber){
						highCardNumber=number;
					}
				}
			}
		}
		/**
		 * ���ں�« ��洢��ǰ�����Ϻ�«��
		 * 1.��������3��  ��ǿ��  �����ϲ��ǹ����ԡ���λ��Ϊ 9��
		 * 2.��������3��   ���ֶ��Ƕ��� ����λ��Ϊ6��  ��������4�� �� ���ƶ��Ӵ��ڸ��� ����λ��Ϊ 6�� ���� ��Ϊ��ǿ�� ����λ��Ϊ 3��	
		 * 3.��������5�� ��ǰ����Ϊ���� ��Ϊ0
		 */
		if(Flag_dou&&Flag_tri){
			int[] fullHouseComnb=new int[5];//�洢��ǰ�������
			for(int i=0;i<3;i++){
				fullHouseComnb[i]=triNumber;
			}
			for(int i=3;i<5;i++){
				fullHouseComnb[i]=douNumber;
			}
			currentMaxHand=fullHouseComnb;//�洢����ǰ�������
			if(findCardsInFullHouse(sharedCards, fullHouseComnb)==3&&holeCards.get(0).number!=holeCards.get(1).number){
				return 69;
			}else{				
				if((findCardsInFullHouse(sharedCards, fullHouseComnb)==3)&&holeCards.get(0).number>highCardNumber||findCardsInFullHouse(sharedCards, fullHouseComnb)==4){
					if((holeCards.get(0).number==triNumber||holeCards.get(1).number==triNumber)&&triNumber>douNumber||douNumber>highCardNumber){
						return 66;
					}else{
						return 63;
					}
				}else{
					return 60;
				}
			}
			
		}				
		return 0;
	}
	/**
	 * �ж��Ƿ����˳�� 40-49
	 * @return
	 */
	public  int getStraight(Map<Integer, Integer> numberOfCards) {
		if (numberOfCards.size() <= 4)
			return 0;
		int[] aAsNormal = new int[numberOfCards.size()];
		int index = 0;
		boolean hasAcard = false;
		for (Integer number : numberOfCards.keySet()) {
			if (number == 14)
				hasAcard = true;
			aAsNormal[index] = number;
			index++;
		}
		Arrays.sort(aAsNormal);
		int preNumber = aAsNormal[0];
		int count = 0;
		int Flag_end = 0;//��˳�ӵı�־ �洢˳�ӵ����λ��
		for (int i = 0; i < index; i++) {
			if (aAsNormal[i] - preNumber == 1)
				count++;
			else {
				if (count >= 4) {
					Flag_end = i;
				}
				count = 0;
			}
			if (count >= 4) {
				Flag_end = i;
			}
			preNumber = aAsNormal[i];
		}
		if (Flag_end != 0) {
			int[] straightComnbs = new int[5];// �洢˳����
			for (int i = 0; i < 5; i++) {
				straightComnbs[i] = aAsNormal[Flag_end - 4+i];
			}
			currentMaxHand=straightComnbs;//�洢����ǰ�������
			/**
			 * ����˳�ӵ����� 1.����3��+����2�� ����ǿ�ƣ� ����λ��Ϊ 9�� 2.����4��+����1�� a.��˳/��˳
			 * ��ǿ�ƣ�����λ��Ϊ6�� b.��˳����ǿ�ƣ�����λ��Ϊ3�� 3.����5�� ���ơ�������Ϊ0��
			 */
			if (findCardsInStraight(sharedCards, straightComnbs) == 3) {// ����3��
																		// +����2��
																		// ���� 49
				return 49;
			} else {
				if (findCardsInStraight(sharedCards, straightComnbs) == 4) {
					if (holeCards.get(0).number == straightComnbs[4]
							|| holeCards.get(1).number == straightComnbs[4]
							|| (holeCards.get(0).number != straightComnbs[0] || holeCards
									.get(1).number != straightComnbs[0])) {
						return 46;// ��˳����˳ ���� 46
					} else {
						return 43;// ��˳ ���� 43
					}
				} else {
					return 40;
				}
			}
		}
		// ��A�����
		if (hasAcard) {
			int[] aAs1 = new int[7];
			index = 0;
			for (Integer number : numberOfCards.keySet()) {
				if (number == 14)
					number = 1;
				aAs1[index] = number;
				index++;
			}
			Arrays.sort(aAs1);
			preNumber = aAs1[0];
			count = 0;
			Flag_end=0;
			for (int i = 1; i < index; i++) {
				if (aAs1[i] - preNumber == 1)
					count++;
				else {
					if (count >= 4)Flag_end=i;						
					count = 0;
				}		
				if (count >= 4)Flag_end=i;
				preNumber = aAs1[i];
				
			}
			if (Flag_end != 0) {
				int[] straightComnbs = new int[5];// �洢˳����
				for (int i = 0; i < 5; i++) {
					straightComnbs[i] = aAs1[Flag_end - 4+i];
				}
				currentMaxHand=straightComnbs;//�洢����ǰ�������
				/**
				 * ����˳�ӵ����� 1.����3��+����2�� ����ǿ�ƣ� ����λ��Ϊ 9�� 2.����4��+����1�� a.��˳/��˳
				 * ��ǿ�ƣ�����λ��Ϊ6�� b.��˳����ǿ�ƣ�����λ��Ϊ3�� 3.����5�� ���ơ� ������Ϊ0��
				 */
				
				if (findCardsInStraight(sharedCards, straightComnbs) == 3) {// ����3��
																			// +����2��
																			// ���� 49
					return 49;
				} else {
					if (findCardsInStraight(sharedCards, straightComnbs) == 4) {
						if (holeCards.get(0).number == straightComnbs[4]
								|| holeCards.get(1).number == straightComnbs[4]
								|| (holeCards.get(0).number != straightComnbs[0] || holeCards
										.get(1).number != straightComnbs[0])) {
							return 46;// ��˳����˳ ���� 46
						} else {
							return 43;// ��˳ ���� 43
						}
					} else {
						return 40;
					}
				}
			}
		}
		return 0;
	}
	/**
	 * �ж��Ƿ����ͬ���� 50-59
	 * @return
	 */
		public  int getFlush(Map<Integer, Integer> suitOfCards ){
			int colorOfFlush=-1;//��¼ͬ���Ļ�ɫ
			for(Integer color:suitOfCards.keySet()){
				if(suitOfCards.get(color)>=5){
					colorOfFlush=color;
					break;
				}
			}
	/**
	 * ����ͬ�� ��洢��ǰ�����ϵ�ͬ����
	 * 1.��������3��ͬɫ  ��ǿ�� ����λ��Ϊ 9��
	 * 2.��������4��ͬɫ �� ���ƵĻ�ɫΪ��ǰ�ܻ�ȡ��ͬɫ�Ƶ�ǰ2λ �� ��Ϊǿ��	����λ��Ϊ 6�� ���� ��Ϊ����	����λ��Ϊ 3��	
	 * 3.��������5�� ��ǰ����Ϊ���� ��Ϊ0
	 */
			if(colorOfFlush>=0){
				int[] flushComnbs=new int[5];//�洢��ǰ�����ϵ�ͬ����
				int index=0;
				for(Card card:allCards){
					if(card.suit==colorOfFlush){
						if(index<5){
							flushComnbs[index]=card.number;
							index++;
						}else{
							Arrays.sort(flushComnbs);
							int number=card.number;
							//if(card.number==1)number=14; 
							if(number>flushComnbs[0]){
								flushComnbs[0]=number;
							}
						}
					}
				}
				Arrays.sort(flushComnbs);
				currentMaxHand=flushComnbs;//�洢����ǰ�������
				if(findCardsInFlush(sharedCards, flushComnbs, colorOfFlush)==3){
					return 59;
				}else{				
					if(findCardsInFlush(sharedCards, flushComnbs, colorOfFlush)==4){
						int holeNumber=0;
						if(holeCards.get(0).suit==colorOfFlush){
							holeNumber=holeCards.get(0).number;
						}
						//if(holeNumber==1)holeNumber=14;
						if(holeCards.get(1).suit==colorOfFlush&&holeCards.get(1).number>holeNumber){
							holeNumber=holeCards.get(1).getNumber();
						}
						//if(holeNumber==1)holeNumber=14;
						int count_biggerThanHole=0;//�����ȵ�ǰ����ǿ��������
					    for(int i=holeNumber;i<15;i++){
					    	boolean Flag_count=true;//��ǰֵ�Ƿ����
					    	for(int j=0;j<5;j++){
					    		if(flushComnbs[j]==i){
					    			Flag_count=false;
					    			break;
					    		}
					    	}
					    	if(Flag_count)count_biggerThanHole++;
					    }
					    if(count_biggerThanHole<=2)return 56;
					    else return 53;
					}else{
						return 50;
					}
				}		
			}		
			return 0;	
		}
		/**
		 * �ж��Ƿ�������� 30-39
		 * @return
		 */
		public  int getSet(Map<Integer, Integer> numberOfCards){
			//TODO
			int triNumber=0;
			int index=0;
			boolean Flag_hasSet=false;
			int[] highCardComnbs=new int[2];
			
			for(Integer number:numberOfCards.keySet()){
				if(numberOfCards.get(number)==3){
					triNumber=number;
					Flag_hasSet=true;
				}else{
					if(index<2){
						highCardComnbs[index]=number;
					}else{
						Arrays.sort(highCardComnbs);
						if(number>highCardComnbs[0]){
							highCardComnbs[0]=number;
						}
					}
					index++;
				}
			}
			if(Flag_hasSet){
				Arrays.sort(highCardComnbs);
				int[] setComnbs=new int[5];
				setComnbs[4]=triNumber;
				setComnbs[3]=triNumber;
				setComnbs[2]=triNumber;
				setComnbs[1]=highCardComnbs[1];
				setComnbs[0]=highCardComnbs[0];
				currentMaxHand=setComnbs;//�洢����ǰ�������
			/**
			 * set��������
			 * 1.��3   �������Ŷ���triNumber ����λ��Ϊ9��
			 * 2.��3   ����ֻ��һ���� triNumber ����λ��Ϊ6��
			 * 3.��3   ��������3�� �����Ǹ���A  ����λ��Ϊ3��
			 * 4.��high  ����λ��Ϊ1��
			 */
				Map<Integer, Integer> numbersOfSharedCrads=getNumberOfCards(sharedCards);
				if(numbersOfSharedCrads.get(triNumber)==1){
					return 39;
				}else{
					if(numbersOfSharedCrads.get(triNumber)==2){
						return 36;
					}else{
						if(Math.max(holeCards.get(0).number, holeCards.get(0).number)==14||(triNumber==14&&Math.max(holeCards.get(0).number, holeCards.get(0).number)==13)){
							return 33;
						}else{
							return 30;
						}
					}
				}
			}
			
			return 0;
		}
		
		/**
		 * �ж��Ƿ�������� 20-29
		 * @return
		 */
		public  int getTwoPairs(Map<Integer, Integer> numberOfCards){
			int firstPairNumber=0;//�ϴ�Ķ���
			int secondPairNumber=0;//��С�Ķ���
			int highCardNumber=0;//������ĸ���
			int count_numberOfPairs=0;
			for(Integer number:numberOfCards.keySet()){
				if(numberOfCards.get(number)==2){
					//if(number==1)number=14;
					if(count_numberOfPairs<2){
						if(count_numberOfPairs==0)secondPairNumber=number;
						else{
							int tmp=secondPairNumber;
							firstPairNumber=Math.max(tmp, number);
							secondPairNumber=Math.min(tmp, number);
						}
					}else{
						int tmp1=secondPairNumber;
						int tmp2=firstPairNumber;
						firstPairNumber=Math.max(tmp2, Math.max(tmp2, number));
						if(firstPairNumber==tmp1){
							secondPairNumber=Math.max(tmp1, number);
						}else{
							secondPairNumber=tmp2;
						}
					}
					count_numberOfPairs++;
				}else{
					//if(number==1)number=14;
					if(number>highCardNumber)highCardNumber=number;
				}
			}
			if(count_numberOfPairs>=2){
				int[] twoPairsComnbs=new int[5];
				twoPairsComnbs[3]=twoPairsComnbs[4]=firstPairNumber;
				twoPairsComnbs[1]=twoPairsComnbs[2]=secondPairNumber;
				twoPairsComnbs[0]=highCardNumber;
				currentMaxHand=twoPairsComnbs;//�洢����ǰ�������
				/**
				 * ���������㷨
				 * 1.�������޶��� �Լ��������빫������ɶ��� ����λ��Ϊ9��
				 * 2.�Լ������Ƕ����Ҹö��ӱȸ��ƴ󡾸�λ��Ϊ6�� �����Ƕ��ӵ��ȸ���С��������û���� ������A����λ��Ϊ3��
				 * 3.���Ӻ͸��ƶ��ǹ����棨��high��
				 */
				int numberOfAllCardsSameSuit=0;//ͬ��ɫ
				int colorOfsuit=-1;
				Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);
				for(Integer number:suitOfCards.keySet()){
					if(suitOfCards.get(number)==4){
						numberOfAllCardsSameSuit=4;
						colorOfsuit=number;
						break;
					}
				}
				//����
				int maxSharedCardsNumber=0;//������ĸ���
				for(Card card:sharedCards){
					if(card.number>maxSharedCardsNumber)maxSharedCardsNumber=card.number;
				}
				if((holeCards.get(0).number==firstPairNumber&&holeCards.get(1).number==secondPairNumber)||(holeCards.get(1).number==firstPairNumber&&holeCards.get(0).number==secondPairNumber)||(numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)){
					return 29;
				}else{
					if((holeCards.get(0).number==holeCards.get(1).number&&firstPairNumber>=maxSharedCardsNumber)||((holeCards.get(0).number==firstPairNumber||holeCards.get(1).number==firstPairNumber)&&firstPairNumber>=maxSharedCardsNumber)){
						return 26;
					}else{
						if(holeCards.get(0).number==secondPairNumber||holeCards.get(1).number==secondPairNumber||holeCards.get(0).number==firstPairNumber||holeCards.get(1).number==firstPairNumber){
							return 23;
						}else{
							return 20;
						}
					}
					/*if(holeCards.get(0).number==holeCards.get(1).number&&(holeCards.get(0).number==firstPairNumber||holeCards.get(0).number==secondPairNumber)){
						if(holeCards.get(0).number>maxSharedCardsNumber) return 26;
						else return 23;
					}else{
						if(findCardsInTwoPairs(sharedCards, twoPairsComnbs)!=5&&highCardNumber==14){
							return 23;
						}else{
							return 20;
						}
					}*/
				}	
			}	
			return 0;		
		}
		/**
		 * �ж��Ƿ���ڵ��� 10-19
		 * @return
		 */
	public  int getSinglePair(Map<Integer, Integer> numberOfCards){
		int douNumber=0;
		int[] highCardComnbs=new int[3];
		int index=0;
		boolean Flag_singlePair=false;;
		for(Integer number:numberOfCards.keySet()){
			//if(number==1)number=14;
			if(numberOfCards.get(number)==2){				
				douNumber=number;
				Flag_singlePair=true;
			}else{
				if(index<3){
					highCardComnbs[index]=number;
					
				}else{
					Arrays.sort(highCardComnbs);
					if(number>highCardComnbs[0]){
						highCardComnbs[0]=number;
					}
				}
				index++;
			}		
		}
		if(Flag_singlePair){
			Arrays.sort(highCardComnbs);
			int[] singlePairComnbs=new int[5];
			singlePairComnbs[4]=douNumber;
			singlePairComnbs[3]=douNumber;
			singlePairComnbs[2]=highCardComnbs[2];
			singlePairComnbs[1]=highCardComnbs[1];
			singlePairComnbs[0]=highCardComnbs[0];
			currentMaxHand=singlePairComnbs;//�洢����ǰ�������
			int numberOfAllCardsSameSuit=0;//ͬ��ɫ
			int colorOfsuit=-1;
			Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);
			for(Integer number:suitOfCards.keySet()){
				if(suitOfCards.get(number)==4){
					numberOfAllCardsSameSuit=4;
					colorOfsuit=number;
					break;
				}
			}
			/**
			 * ������������
			 * 1.����  �Լ������Ƕ��� ���Ҵ������ĸ��� ����λ��Ϊ9��
			 * 2.����  �Լ�������һ�Ŷ��� �ж��ƴ��ڸ���   ����λ��Ϊ6��
			 * 3.�ж�/�׶�  ������С�ڸ���  ����λ��Ϊ3��
			 * 4.�����ǹ�����   ����λ��Ϊ0��
			 */
			//��˳ 
			Map<Integer, Integer> numberOfSharedCards = getNumberOfCards(sharedCards);
			int count=0;
			if(getStraightNumber(numberOfCards)==44){
				for(int i=0;i<4;i++){
					if(holeCards.get(0).number==longestStraight[i]||holeCards.get(1).number==longestStraight[i]){
						count++;
					}
				}
			}
			int maxSharedCardsNumber=0;
			for(Card card:sharedCards){
				if(card.number>maxSharedCardsNumber)maxSharedCardsNumber=card.number;
			}
			Map<Integer, Integer> numbersOfSharedCrads=getNumberOfCards(sharedCards);
			if((!numbersOfSharedCrads.containsKey(douNumber))&&douNumber>=maxSharedCardsNumber||(numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)||((holeCards.get(0).number==douNumber||holeCards.get(1).number==douNumber)&&count==2&&sharedCards.size()<5)){
				return 19;
			}else{
				if(douNumber==maxSharedCardsNumber&&numbersOfSharedCrads.get(douNumber)==1){
					return 16;
				}else{
					if(!numberOfSharedCards.containsKey(douNumber)||(numberOfSharedCards.containsKey(douNumber)&&numbersOfSharedCrads.get(douNumber)!=2)){
						return 13;
					}else{
						return 10;
					}
				}
			}
		}			
		return 0;	
	}
		/**
		 * �ж��Ƿ���ڸ���
		 * ����������
		 * @param numberOfCards
		 * @return
		 */
	public  int getHighCard(Map<Integer, Integer> numberOfCards){
		
		int[] highCardComnbs=new int[5];
		int index=0;
		for(Integer number:numberOfCards.keySet()){
			if(index<5){
				highCardComnbs[index]=number;
			}else{
				Arrays.sort(highCardComnbs);
				if(number>highCardComnbs[0]){
					highCardComnbs[0]=number;
				}
			}
			index++;
		}
		Arrays.sort(highCardComnbs);
		currentMaxHand=highCardComnbs;//�洢����ǰ�������
		
		int numberOfAllCardsSameSuit=0;//ͬ��ɫ
		int colorOfsuit=-1;
		Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);
		for(Integer number:suitOfCards.keySet()){
			if(suitOfCards.get(number)==4){
				numberOfAllCardsSameSuit=4;
				colorOfsuit=number;
				break;
			}
		}
		//��˳ 
		Map<Integer, Integer> numberOfSharedCards = getNumberOfCards(allCards);
		int count=0;
		if(getStraightNumber(numberOfSharedCards)==44){
			for(int i=0;i<4;i++){
				if(holeCards.get(0).number==longestStraight[i]||holeCards.get(1).number==longestStraight[i]){
					count++;
				}
			}
		}
		/*System.out.println(getStraightNumber(numberOfSharedCards));
		System.out.println(count);
		System.out.println(count==2&&sharedCards.size()<5);*/
		
		if((numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)&&(count==2&&sharedCards.size()<5)){
			return 9;
		}else{
			if((numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)||(count==2&&sharedCards.size()<5)){
				return 6;
			}
		}
		
		return 0;			
	}	
	//��ȡ��ǰ������� ��Ϊ�Ƚ�ʹ��
	public  int[]  getCurrentMaxHand() {
		return currentMaxHand;
	}
	//Ѱ�����������
	public int getStraightNumber(Map<Integer, Integer> numberOfCards) {
		if (numberOfCards.size() < 4)
			return 0;
		int[] aAsNormal = new int[numberOfCards.size()];
		int index = 0;
		boolean hasAcard = false;
		for (Integer number : numberOfCards.keySet()) {
			if (number == 14)
				hasAcard = true;
			aAsNormal[index] = number;
			index++;
		}
		Arrays.sort(aAsNormal);
		int preNumber = aAsNormal[0];
		int count = 0;
		int Flag_end = 0;// ��˳�ӵı�־ �洢˳�ӵ����λ��
		for (int i = 1; i < index; i++) {
			if (aAsNormal[i] - preNumber == 1)
				count++;
			else {
				if (count >= 3) {
					Flag_end = i;
				}
				count = 0;
			}
			if (count >= 3) {
				Flag_end = i;
			}
			preNumber = aAsNormal[i];
		}
		if (Flag_end != 0) {
			int[] straightComnbs = new int[4];// �洢˳����
			for (int i = 0; i < 4; i++) {
				straightComnbs[i] = aAsNormal[Flag_end - 3+i];
			}
			longestStraight=straightComnbs;
			return 44;
		}
		// ��A�����
		if (hasAcard) {
			int[] aAs1 = new int[7];
			index = 0;
			for (Integer number : numberOfCards.keySet()) {
				if (number == 14)
					number = 1;
				aAs1[index] = number;
				index++;
			}
			Arrays.sort(aAs1);
			preNumber = aAs1[0];
			count = 0;
			Flag_end = 0;
			for (int i = 0; i < index; i++) {
				if (aAs1[i] - preNumber == 1)
					count++;
				else {
					if (count >= 3)
						Flag_end = i;
					count = 0;
				}
				if (count >= 3)
					Flag_end = i;
				preNumber = aAs1[i];

			}
			if (Flag_end != 0) {
				int[] straightComnbs = new int[4];// �洢˳����
				for (int i = 0; i < 4; i++) {
					straightComnbs[i] = aAsNormal[Flag_end - 3+i];
				}
				longestStraight=straightComnbs;
				return 44;
			}
		}
		return 0;
	}
	public  int findCardsInTwoPairs(List<Card> sample, int[] twoPairsComnbs) {
			int count=0;
			for(Card card:sample){
				for(int i=0;i<5;i++){
					int number=card.number;
					//if(number==1)number=14;
					if(number==twoPairsComnbs[i]){
						count++;
						break;
					}									
				}
			}
			return count;		
		}
		
	public int findCardsInFullHouse(List<Card> sample, int[] fullHouse) {
		int count=0;
		for(Card card:sample){
			for(int i=0;i<5;i++){
				int number=card.number;
				//if(number==1)number=14;
				if(card.number==fullHouse[i]){
					count++;
					break;
				}									
			}
		}
		return count;
	}

	public int findCardsInFlush(List<Card> sample, int[] flush, int colorOfFlush) {
		int count = 0;
		for (Card card : sharedCards) {
			if (card.suit == colorOfFlush) {
				int number = card.number;
				/*if (number == 1)
					card.number = 14;*/
				for (int i = 0; i < 5; i++) {
					if (number == flush[i]) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public int findCardsInStraight(List<Card> sample, int[] straight) {
		int count = 0;
		Map<Integer, Integer> map = getNumberOfCards(sample);
		for (Integer number : map.keySet()) {
			for (int i = 0; i < straight.length; i++) {
				if (number == straight[i])
					count++;
			}
		}
		return count;
	}

	public Map<Integer, Integer> getNumberOfCards(List<Card> cards) {
		Map<Integer, Integer> res = new HashMap<Integer, Integer>();
		for (Card card : cards) {
			if (res.containsKey(card.number)) {
				res.put(card.number, res.get(card.number) + 1);
			} else
				res.put(card.number, 1);
		}
		return res;
	}

	public Map<Integer, Integer> getSuitOfCards(List<Card> cards) {
		Map<Integer, Integer> res = new HashMap<Integer, Integer>();
		for (Card card : cards) {
			if (res.containsKey(card.suit)) {
				res.put(card.suit, res.get(card.suit) + 1);
			} else
				res.put(card.suit, 1);
		}
		return res;
	}
}
