package huawei.texaspoker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Game {
	private String MsgHead;
	Socket player;
	PrintWriter player2server;
	BufferedReader reader;
	MessageHead MsgHeadHanlder;
	Desk desk;
	
    int mypid;
    String serverip,myip;
    int serverport,myport;
    
    int myorder;
	private boolean isDiscard;//��¼�Լ��Ƿ�����
	private List<Card> holdCards;//�Լ�����
	private int mymoney=10000,myjetton=2000;//�Լ��ĳ���ͽ��
	Map<Integer, Opponent> Pid_Opponent;//����pid����һ������ʵ����
	public Game(){
		isDiscard=false;
		holdCards=new ArrayList<Card>(2);
		Pid_Opponent=new HashMap<Integer, Opponent>();
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub	
		//��ʼ��
		Game dsnju=new Game();
		dsnju.serverip=args[0];
		dsnju.serverport=new Integer(args[1]);
		dsnju.myip=args[2];
		dsnju.myport=new Integer(args[3]);
		dsnju.mypid=new Integer(args[4]);
		/*dsnju.serverip="127.0.0.1";
		dsnju.serverport=8888;
		dsnju.myip="127.0.0.1";
		dsnju.myport=8889;
		dsnju.mypid=666;*/
		dsnju.initialize();
		//����ע����Ϣ
		dsnju.player2server.println("reg:"+dsnju.mypid+" DSNJU");
		dsnju.player2server.flush();
		//�ƾּ���
		int count=0;
		while(count<2&&dsnju.mymoney>0){//����һ�����涨����������������
			do{
				dsnju.getAllMsg(dsnju.reader);
			}while(!dsnju.MsgHead.equals("pot-win"));
			//��һЩ�������
			dsnju.holdCards.clear();//����Լ��������б�
			//���ÿ�����ֶ���Ķ���Map
			for(Entry<Integer, Opponent> entry:dsnju.Pid_Opponent.entrySet()){
				entry.getValue().action.clear();
			}
			//System.out.println("��"+(count+1)+"�ֽ���");
			count++;
		}
		if(dsnju.reader.readLine().equals("game-over")){
			dsnju.reader.close();
			dsnju.player2server.close();
			dsnju.player.close();
			//System.out.println("������������");
		}
	}
	private void initialize() throws UnknownHostException, IOException{
		player = new Socket();
		player.bind(new InetSocketAddress(myip, myport));//�󶨿ͻ��˵�ָ��IP�Ͷ˿ں�
		player.connect(new InetSocketAddress(serverip, serverport));//����server
		/*player=new Socket(serverip, serverport);*/
		player2server=new PrintWriter(player.getOutputStream());
		reader=new BufferedReader(new InputStreamReader(player.getInputStream()));
		MsgHeadHanlder=new MessageHead();
		//System.out.println("�ѳ�ʼ��");
	}
	/**
	 * ��ȡ��Ϣ�� ����MsgHead��ֵ
	 * @param reader
	 * @param msghead
	 * @return
	 * @throws IOException
	 */
	public  void getAllMsg(BufferedReader reader) throws IOException{
		//���ܽ�MsgHead��Ϊ��������ʹ��ָ���µĶ�����Ϊ���󴫲�Ҳ�Ǵ��ı��ݣ�ָ���¶����Ͳ���ԭ��������
		//msghead=new String(result.substring(0, head.indexOf("/")));

		String head=reader.readLine();//��ȡ��Ϣͷ
		StringBuffer result=new StringBuffer(head);	
		head=result.substring(0, head.indexOf("/"));//ȥ����/��
		/*result.insert(0, "/");
		head=result.substring(0, head.length());//��headǰ����/��ȥ��β����/����/���пո��ô˷���*/
		
		this.setMsgHead(head);
		this.HandleMsg(MsgHead);
	}
	public void setMsgHead(String msghead){
		this.MsgHead=new String(msghead);
	}
	public void HandleMsg(String msghead) throws IOException{
		if(msghead==null)
			return;
		int label=MsgHeadHanlder.map.get(msghead);
		String head="/"+msghead;
		switch (label) {
		case 1:
			//���ô���������Ϣ����
			//System.out.println("��������������Ϣ����");
			HanldeSeat(reader, head);
			break;
		case 2:
			//���ô���äע��Ϣ����
			//System.out.println("��������äע��Ϣ����");
			HandleBlind(reader, head);
			break;
		case 3:
			//���ô���������Ϣ����
			//System.out.println("��������������Ϣ����");
			HanldeHoldCards(reader, head);
			break;
		case 4:
			//���ô���ѯ����Ϣ����
			//System.out.println("��������ѯ����Ϣ����");
			HanldeInquire(reader, head);
			break;
		case 5:
			//���ô�������Ϣ����
			//System.out.println("������������Ϣ����");
			HandleFlop(reader, head);
			break;
		case 6:
			//���ô���ת����Ϣ����
			//System.out.println("��������ת����Ϣ����");
			HandleTurn(reader, head);
			break;
		case 7:
			//���ô��������Ϣ����
			//System.out.println("�������������Ϣ����");
			HandleRiver(reader, head);
			break;
		case 8:
			//���ô���̯����Ϣ����
			//System.out.println("��������̯����Ϣ����");
			HandleShutdown(reader, head);
			break;
		case 9:
			//���ô�������Ϣ����
			//System.out.println("������������Ϣ����");
			HandlePot_Win(reader, head);
			break;
		case 10:
			//���ô��������Ϣ����
			break;
		default:
			break;
		}
	}
		
	/**
	 * ����������Ϣ����¼��Сä��
	 * ���ݴ������Ϣ����ȡ��Ϣ
	 * �������ֶ������Map
	 * ��¼ִ��˳��
	 * @throws IOException 
	 */
	public void HanldeSeat(BufferedReader reader,String head) throws IOException{
		int smallblind=0,bigblind=0,button=0;//��¼pid
		int money,jetton,pid;//��¼ÿһλ��ҵĳ���ͽ����		
		desk=new Desk(smallblind, bigblind, button);//��ʼ��desk
		desk.playercount=0;//��¼������Ϣ�ĵڼ���
		StringBuffer msgbody=new StringBuffer();
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);//������һ�е���Ϣ
			String splittemp[]=temp.split(" ");//���Կո�Ϊ�ָ����ֳ�����
			if(splittemp[0].contains("button")){
				button=getSeat(splittemp[0]);
				jetton=new Integer(splittemp[1]).intValue();//��ø����jetton
				money=new Integer(splittemp[2]).intValue();
				desk.setButton(button);
				desk.Order_Pid.put(desk.playercount, button);//����˳��Map
				//�������Map������key���ظ��Ķ��ֶ���ÿ�ζ����½���
				this.Pid_Opponent.put(button, new Opponent(button, jetton, money));
			}
			else if(splittemp[0].equals("small")){
				smallblind=getSeat(splittemp[1]);
				desk.setSmallBlind(smallblind);
				jetton=new Integer(splittemp[2]).intValue();//��ø����jetton
				money=new Integer(splittemp[3]).intValue();
				desk.Order_Pid.put(desk.playercount, smallblind);//����˳��Map
				this.Pid_Opponent.put(smallblind, new Opponent(smallblind, jetton, money));//�������Map
			}
			else if(splittemp[0].equals("big")){
				bigblind=getSeat(splittemp[1]);
				desk.setBigBlind(bigblind);
				jetton=new Integer(splittemp[2]).intValue();//��ø����jetton
				money=new Integer(splittemp[3]).intValue();
				desk.Order_Pid.put(desk.playercount, bigblind);//����˳��Map
				this.Pid_Opponent.put(bigblind, new Opponent(bigblind, jetton, money));//�������Map
			}
			else{
				pid=new Integer(splittemp[0]).intValue();
				jetton=new Integer(splittemp[1]).intValue();//��ø����jetton
				money=new Integer(splittemp[2]).intValue();
				desk.Order_Pid.put(desk.playercount, pid);//����˳��Map
				if(pid!=mypid)
					this.Pid_Opponent.put(pid, new Opponent(pid, jetton, money));//�������Map
				else{
					this.myjetton=jetton;
					this.mymoney=money;
					this.myorder=desk.playercount;
				}
			}		
			desk.playercount++;
			desk.setcardStatus(0);//�����ƾ�״̬
		}
		if(mypid==button)//button�ǵ�0�е�
			this.myorder=desk.playercount;
		
		/*System.out.println("button:"+desk.getButton()+",smallblind:"+desk.getSmallBlind()+",bigblind:"+desk.getBigBlind());
		System.out.println("��ǰ�Լ��ĳ��롢�����Σ�"+myjetton+","+mymoney+","+myorder);
		System.out.println("��ǰ��������:"+desk.playercount);
		System.out.println("�Ѽ�����ж�˳��Map��");
		for(Map.Entry<Integer, Integer> entry:desk.Order_Pid.entrySet()){    
		     System.out.print(entry.getKey()+"-->"+entry.getValue()+" ");    
		}
		System.out.println("\n�Ѽ���Ķ���Map��");
		for(Map.Entry<Integer, Opponent> entry:Pid_Opponent.entrySet()){    
		     System.out.println(entry.getKey()+"-->"+entry.getValue().getPID()+" "+entry.getValue().getJetton()+" "+entry.getValue().getMoney());    
		}*/
	}
	private int getSeat(String Blindmsg){
		int index=Blindmsg.indexOf(":");
		String str=Blindmsg.substring(index+1,Blindmsg.length());
		int result=new Integer(str).intValue();
		return result;
	}
	/**
	 * äע��Ϣ������¼��äע���
	 * @param reader
	 * @param head
	 * @throws IOException
	 */
	public void HandleBlind(BufferedReader reader,String head) throws IOException{
		String temp="";
		StringBuffer msgbody=new StringBuffer();
		int linecount=0;
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);	
			if(linecount==1){
				String splittemp[]=temp.split(" ");
				int bb=new Integer(splittemp[1]).intValue();
				desk.setBB(bb);//��¼��äע���
			}
			++linecount;
		}
		
		//System.out.println("��ä��"+desk.getBB());
	}
	/**
	 * ������������
	 * @param reader
	 * @param head
	 * @throws IOException
	 */
	private void HanldeHoldCards(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";		
		while(!(temp=reader.readLine()).equals(head)){
			String splittemp[]=temp.split(" ");	
			Integer i=Card.NumeralSuit.get(splittemp[0]);
			int suitnumber=i.intValue();
			Card c=new Card(suitnumber, card2number(splittemp[1]));
			holdCards.add(c);
		}
		
		/*System.out.println("�Լ������ƣ�");
		for(Card c:holdCards){
			System.out.println(c.getSuit()+" "+c.getNumber());
		}*/
	}
	/**
	 * �Է�����ѯ����Ϣ��¼���ֵĶ��������������Լ��Ķ���
	 * @param reader
	 * @param head
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	private void HanldeInquire(BufferedReader reader, String head) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		String temp="";
		int bet=0;
		StringBuffer curRoundAction=new StringBuffer();
		while(!(temp=reader.readLine()).equals(head)){
			String splittemp[]=temp.split(" ");
			int action_index=splittemp.length-1;
			if(!splittemp[0].equals("total")){
				int pid=new Integer(splittemp[0]);
				int bettemp=new Integer(splittemp[action_index-1]);
				bet=(bettemp>bet?bettemp:bet);
				String action=splittemp[action_index];//��ȡ����
				curRoundAction.append(action);//����֪��������buffer
				if(!action.equals("blind")){//��äע��Ϣ������					
					Opponent opp=Pid_Opponent.get(pid);//��ȡ��Ӧ���ֶ���
					if(action.equals("raise"))
						opp.raise_money=new Integer(splittemp[action_index-1]);//��¼���ּ�ע���
					if(!opp.action.containsKey(desk.getcardStatus())){//δ�Ը�״̬��¼
						List<String> action_list=new ArrayList<String>();
						action_list.add(action);
						opp.action.put(desk.getcardStatus(), action_list);//���������붯��Map
					}
					else{
						List<String> action_list=opp.action.get(desk.getcardStatus());//��ȡ��Ӧ�б�
						action_list.add(action);//���б������
					}
				}
			}
			else{
				desk.totalpot=new Integer(splittemp[action_index]);
			}
		}
		/*for(Map.Entry<Integer, Opponent> entry:Pid_Opponent.entrySet()){//��ȡÿ�����ֶ���  
			System.out.println(entry.getKey()+":");
			for(Map.Entry<Integer, List<String>> e: entry.getValue().action.entrySet()){//ÿ������Ķ���
				System.out.println(e.getKey()+"-->"+e.getValue());
			}
		}
		System.out.println("��ǰ��Ͷע��"+desk.totalpot);
		System.out.println("ǰ��������bet��"+bet);
		System.out.println("ǰ������ж���Ϣ��"+curRoundAction.toString());
		System.out.println("��sever�����Լ���action����");*/
		if(desk.getcardStatus()==0){
			preFlopAction pre=new preFlopAction(holdCards, myorder, bet, desk.getBB(), 
					desk.totalpot, desk.playercount, myjetton);
			player2server.println(pre.preFlopDecision());
		}
		else {
			actionDecision mActionDecision=new actionDecision(holdCards, desk.sharedCards, bet, 
					desk.getBB(), getOpponentAction(curRoundAction.toString()), desk.totalpot, myjetton);
			player2server.println(mActionDecision.actionSendToServer());
		}
		player2server.flush();
	}
	private String getOpponentAction(String curRoundAction ){
		if(curRoundAction.contains("all_in"))//����all_in
			return "all_in";
		else if(curRoundAction.contains("raise"))//����raise
			return "raise";
		else if(curRoundAction.contains("call"))//
			return "call";
		else if(curRoundAction.contains("check"))//
			return "check";
		else
			return "fold";
	}
	/**
	 * ��¼������
	 * @param reader2
	 * @param head
	 * @throws IOException
	 */
	private void HandleFlop(BufferedReader reader2, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			String[] splittemp=temp.split(" ");
			desk.sharedCards.add(new Card(Card.NumeralSuit.get(splittemp[0]), card2number(splittemp[1])));
		}
		desk.setcardStatus(1);//�ƾ�״̬Ϊflop
		/*System.out.println("���й����ƣ�");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	
	/**
	 * ��¼Turn��
	 * @param reader2
	 * @param head
	 * @throws IOException
	 */
	private void HandleTurn(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			String[] splittemp=temp.split(" ");
			desk.sharedCards.add(new Card(Card.NumeralSuit.get(splittemp[0]), card2number(splittemp[1])));
		}
		/*System.out.println("���й����ƣ�");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	
	private void HandleRiver(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			String[] splittemp=temp.split(" ");
			desk.sharedCards.add(new Card(Card.NumeralSuit.get(splittemp[0]), card2number(splittemp[1])));
		}
		/*System.out.println("���й����ƣ�");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	/**
	 * ̯��
	 * @param reader
	 * @param head
	 * @throws IOException
	 */
	private void HandleShutdown(BufferedReader reader, String head) throws IOException {
		// TODO Auto-generated method stub
		String temp="";
		StringBuffer msgbody=new StringBuffer();
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);	
		}
		//System.out.println("shutdown��Ϣ��"+msgbody);
	}
	
	public void HandlePot_Win(BufferedReader reader,String head) throws IOException{
		String temp="";
		StringBuffer msgbody=new StringBuffer();
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);	
		}
		//System.out.println("pot-win��Ϣ��"+msgbody);
	}
	
	private int card2number(String str){
		int num;
		try{
			num=new Integer(str).intValue();
		}
		catch (IllegalArgumentException e){
			if(str.equals("A"))
				num=14;
			else if(str.equals("J"))
				num=11;
			else if(str.equals("Q"))
				num=12;
			else 
				num=13;
		}
		return num;
	}
}
