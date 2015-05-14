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
	private boolean isDiscard;//记录自己是否弃牌
	private List<Card> holdCards;//自己手牌
	private int mymoney=10000,myjetton=2000;//自己的筹码和金额
	Map<Integer, Opponent> Pid_Opponent;//根据pid储存一个对手实例。
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
		//初始化
		Game dsnju=new Game();
		dsnju.serverip=args[0];
		dsnju.serverport=new Integer(args[1]);
		dsnju.myip=args[2];
		dsnju.myport=new Integer(args[3]);
		dsnju.mypid=new Integer(args[4]);
		/*dsnju.serverip="127.0.0.1";
		dsnju.serverport=6000;
		dsnju.myip="127.0.0.1";
		dsnju.myport=8889;
		dsnju.mypid=666;*/
		dsnju.initialize();
		//发送注册信息
		dsnju.player2server.println("reg: "+dsnju.mypid+" DSNJU ");
		dsnju.player2server.flush();
		//牌局计数
		int count=0;
		start:while(true){//持续进行，直到收到game-over消息
			do{
				dsnju.getAllMsg(dsnju.reader);
				
			}while(!dsnju.MsgHead.equals("pot-win")&&!dsnju.MsgHead.equals("game-over "));
			if(dsnju.MsgHead.equals("game-over "))
				break start;
			//对一些变量清空
			dsnju.holdCards.clear();//清空自己的手牌列表
			//清空每个对手对象的动作Map
			for(Entry<Integer, Opponent> entry:dsnju.Pid_Opponent.entrySet()){
				entry.getValue().action.clear();
			}
			//System.out.println("第"+(count+1)+"局结束");
			count++;
			if(!dsnju.player.isConnected())
				dsnju.player.connect(new InetSocketAddress(dsnju.serverip, dsnju.serverport));
		}
		dsnju.reader.close();
		dsnju.player2server.close();
		dsnju.player.close();
	}
	private void initialize() throws UnknownHostException, IOException{
		player = new Socket();
		player.bind(new InetSocketAddress(myip, myport));//绑定客户端到指定IP和端口号
		if(!player.isConnected())
			player.connect(new InetSocketAddress(serverip, serverport));//连接server
		
		/*player=new Socket(serverip, serverport);*/
		player2server=new PrintWriter(player.getOutputStream());
		reader=new BufferedReader(new InputStreamReader(player.getInputStream()));
		MsgHeadHanlder=new MessageHead();
		//System.out.println("已初始化");
	}
	/**
	 * 获取消息体 并对MsgHead赋值
	 * @param reader
	 * @param msghead
	 * @return
	 * @throws IOException
	 */
	public  void getAllMsg(BufferedReader reader) throws IOException{
		//不能将MsgHead作为参数带入使其指向新的对象。因为对象传参也是传的备份，指向新对象后就不是原来的引用
		//msghead=new String(result.substring(0, head.indexOf("/")));

		String head=reader.readLine();//获取消息头
		if(head.equals("game-over ")){//game-over消息不带/
			this.setMsgHead(head);
			return;
		}
		StringBuffer result=new StringBuffer(head);	
		head=result.substring(0, head.indexOf("/"));//去除“/”
		/*result.insert(0, "/");
		head=result.substring(0, head.length());//在head前插入/，去掉尾部的/，若/后有空格用此方法*/
		
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
		String head="/"+msghead+" ";
	switch (label) {
		case 1:
			//调用处理座次信息方法
			//System.out.println("……处理座次信息……");
			//HanldeSeat(reader, head);
			HandlePot_Win(reader, head);
			break;
		case 2:
			//调用处理盲注信息方法
			//System.out.println("……处理盲注信息……");
			//HandleBlind(reader, head);
			HandlePot_Win(reader, head);
			break;
		case 3:
			//调用处理手牌信息方法
			//System.out.println("……处理手牌信息……");
			//HanldeHoldCards(reader, head);
			HandlePot_Win(reader, head);
			break;
		case 4:
			//调用处理询问信息方法
			//System.out.println("……处理询问信息……");
			//HanldeInquire(reader, head);
			HanldeInquireTest(reader, head);
			break;
		case 5:
			//调用处理公牌信息方法
			//System.out.println("……处理公牌信息……");
			//HandleFlop(reader, head);
			HandlePot_Win(reader, head);
			break;
		case 6:
			//调用处理转牌信息方法
			//System.out.println("……处理转牌信息……");
			//HandleTurn(reader, head);
			HandlePot_Win(reader, head);
			break;
		case 7:
			//调用处理河牌信息方法
			//System.out.println("……处理河牌信息……");
			//HandleRiver(reader, head);
			HandlePot_Win(reader, head);
			break;
		case 8:
			//调用处理摊牌信息方法
			//System.out.println("……处理摊牌信息……");
			HandleShutdown(reader, head);			
			break;
		case 9:
			//调用处理奖池信息方法
			//System.out.println("……处理奖池信息……");
			HandlePot_Win(reader, head);
			break;
		case 10:
			//调用处理结束信息方法
			break;
		default:
			break;
		}
	}
		
	/**
	 * 处理座次信息，记录大小盲等
	 * 根据传入的消息体提取信息
	 * 创建对手对象存入Map
	 * 记录执行顺序
	 * @throws IOException 
	 */
	public void HanldeSeat(BufferedReader reader,String head) throws IOException{
		int smallblind=0,bigblind=0,button=0;//记录pid
		int money,jetton,pid;//记录每一位玩家的筹码和金币数		
		desk=new Desk(smallblind, bigblind, button);//初始化desk
		desk.playercount=0;//记录座次消息的第几行
		StringBuffer msgbody=new StringBuffer();
		String temp="";
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);//接收这一行的消息
			String splittemp[]=temp.split(" ");//并以空格为分隔符分成数组
			if(splittemp[0].contains("button")){
				button=getSeat(splittemp[0]);
				jetton=new Integer(splittemp[1]).intValue();//获得该玩家jetton
				money=new Integer(splittemp[2]).intValue();
				desk.setButton(button);
				desk.Order_Pid.put(desk.playercount, button);//加入顺序Map
				//加入对手Map，由于key是重复的对手对象每次都是新建的
				this.Pid_Opponent.put(button, new Opponent(button, jetton, money));
			}
			else if(splittemp[0].equals("small")){
				smallblind=getSeat(splittemp[1]);
				desk.setSmallBlind(smallblind);
				jetton=new Integer(splittemp[2]).intValue();//获得该玩家jetton
				money=new Integer(splittemp[3]).intValue();
				desk.Order_Pid.put(desk.playercount, smallblind);//加入顺序Map
				this.Pid_Opponent.put(smallblind, new Opponent(smallblind, jetton, money));//加入对手Map
			}
			else if(splittemp[0].equals("big")){
				bigblind=getSeat(splittemp[1]);
				desk.setBigBlind(bigblind);
				jetton=new Integer(splittemp[2]).intValue();//获得该玩家jetton
				money=new Integer(splittemp[3]).intValue();
				desk.Order_Pid.put(desk.playercount, bigblind);//加入顺序Map
				this.Pid_Opponent.put(bigblind, new Opponent(bigblind, jetton, money));//加入对手Map
			}
			else{
				pid=new Integer(splittemp[0]).intValue();
				jetton=new Integer(splittemp[1]).intValue();//获得该玩家jetton
				money=new Integer(splittemp[2]).intValue();
				desk.Order_Pid.put(desk.playercount, pid);//加入顺序Map
				if(pid!=mypid)
					this.Pid_Opponent.put(pid, new Opponent(pid, jetton, money));//加入对手Map
				else{
					this.myjetton=jetton;
					this.mymoney=money;
					this.myorder=desk.playercount;
				}
			}		
			desk.playercount++;
			desk.setcardStatus(0);//设置牌局状态
		}
		if(mypid==button)//button是第0行的
			this.myorder=desk.playercount;
		
		/*System.out.println("button:"+desk.getButton()+",smallblind:"+desk.getSmallBlind()+",bigblind:"+desk.getBigBlind());
		System.out.println("当前自己的筹码、金额及座次："+myjetton+","+mymoney+","+myorder);
		System.out.println("当前牌手人数:"+desk.playercount);
		System.out.println("已加入的行动顺序Map：");
		for(Map.Entry<Integer, Integer> entry:desk.Order_Pid.entrySet()){    
		     System.out.print(entry.getKey()+"-->"+entry.getValue()+" ");    
		}
		System.out.println("\n已加入的对手Map：");
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
	 * 盲注消息处理：记录大盲注金额
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
				desk.setBB(bb);//记录大盲注金额
			}
			++linecount;
		}
		
		//System.out.println("大盲金额："+desk.getBB());
	}
	/**
	 * 接收两张手牌
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
		
		/*System.out.println("自己的手牌：");
		for(Card c:holdCards){
			System.out.println(c.getSuit()+" "+c.getNumber());
		}*/
	}
	/**
	 * 对发来的询问消息记录对手的动作，并且做出自己的动作
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
				String action=splittemp[action_index];//获取动作
				curRoundAction.append(action);//将已知动作加入buffer
				if(!action.equals("blind")){//对盲注信息不处理					
					Opponent opp=Pid_Opponent.get(pid);//获取相应对手对象
					if(action.equals("raise"))
						opp.raise_money=new Integer(splittemp[action_index-1]);//记录对手加注金额
					if(!opp.action.containsKey(desk.getcardStatus())){//未对该状态记录
						List<String> action_list=new ArrayList<String>();
						action_list.add(action);
						opp.action.put(desk.getcardStatus(), action_list);//将动作加入动作Map
					}
					else{
						List<String> action_list=opp.action.get(desk.getcardStatus());//获取对应列表
						action_list.add(action);//在列表中添加
					}
				}
			}
			else{
				desk.totalpot=new Integer(splittemp[action_index]);
			}
		}
		/*for(Map.Entry<Integer, Opponent> entry:Pid_Opponent.entrySet()){//获取每个对手对象  
			System.out.println(entry.getKey()+":");
			for(Map.Entry<Integer, List<String>> e: entry.getValue().action.entrySet()){//每个对象的动作
				System.out.println(e.getKey()+"-->"+e.getValue());
			}
		}
		System.out.println("当前总投注："+desk.totalpot);
		System.out.println("前面玩家最大bet："+bet);
		System.out.println("前面玩家行动消息："+curRoundAction.toString());
		System.out.println("向sever发送自己的action……");*/
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
	private void HanldeInquireTest(BufferedReader reader, String head) throws IOException{
		String temp="";
		StringBuffer msgbody=new StringBuffer();
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);	
		}
		player2server.println("call");
		player2server.flush();
	}
	private String getOpponentAction(String curRoundAction ){
		if(curRoundAction.contains("all_in"))//包含all_in
			return "all_in";
		else if(curRoundAction.contains("raise"))//包含raise
			return "raise";
		else if(curRoundAction.contains("call"))//
			return "call";
		else if(curRoundAction.contains("check"))//
			return "check";
		else
			return "fold";
	}
	/**
	 * 记录公共牌
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
		desk.setcardStatus(1);//牌局状态为flop
		/*System.out.println("现有公共牌：");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	
	/**
	 * 记录Turn牌
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
		/*System.out.println("现有公共牌：");
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
		/*System.out.println("现有公共牌：");
		for(Card c:desk.sharedCards)
			System.out.println(c.getSuit()+","+c.getNumber());*/
	}
	/**
	 * 摊牌
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
		//System.out.println("shutdown消息体"+msgbody);
	}
	
	public void HandlePot_Win(BufferedReader reader,String head) throws IOException{
		String temp="";
		StringBuffer msgbody=new StringBuffer();
		while(!(temp=reader.readLine()).equals(head)){
			msgbody.append(temp);	
		}
		//System.out.println("pot-win消息体"+msgbody);
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
