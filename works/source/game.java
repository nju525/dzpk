import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;


public class game {
	public static int[] twonum=new int[2];
	public static String player=" YoungKing ";
	public static Socket socket;
	public static PrintWriter printWriter;
	public static BufferedReader bufferedReader;
	public static String head;
	public static int Blind;
	public static int id;
	
	public static void main(String[] args) throws IOException {
		
		id=Integer.parseInt(args[4]);	
		try {
			int a=0;
			while(a==0){
				try {
					SocketAddress serveraddress = new InetSocketAddress(
							args[0], Integer.parseInt(args[1]));
					SocketAddress hostaddress = new InetSocketAddress(args[2],
							Integer.parseInt(args[3]));
					socket = new Socket();
					socket.setReuseAddress(true);
					socket.bind(hostaddress);
					socket.connect(serveraddress);
					a=1;
				} catch (IOException e) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}				
					continue;			
				}
			}
			printWriter=new PrintWriter(socket.getOutputStream());
			bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			inquire(printWriter, "reg: "+id+player);
			
			while(true){
				do{
					mainThread(bufferedReader);
					
				}while(!head.equals("pot-win")&&!head.equals("game-over "));
				if(head.equals("game-over "))
					break ;
			}
		} catch (IOException e) {
			socket.close();
			bufferedReader.close();
		}
	
	}
	private static void mainThread(BufferedReader bufferedReader) {
		try {
			String tmp=bufferedReader.readLine();			
			if(tmp.equals("game-over ")){
				head=tmp;
				return;
			}	
			tmp=tmp.substring(0, tmp.indexOf("/"));
			head=new String(tmp);
			tmp="/"+tmp+" ";
			for(String context=bufferedReader.readLine();!context.equals(tmp);context=bufferedReader.readLine()){
				 if(head.equals("hold")){
					String temp=context,msg="";
					do{
						msg+=temp;
					}
					while(!(temp=bufferedReader.readLine()).equals(tmp));
					String[] words=msg.split(" ");
					int number=0;
					if(words[2].length()==1){
						number=new Integer(words[2]).intValue();
					}else{
						if(words[2].equals("10"))
							number=10;
						else if(words[2].equals("J"))
							number=11;
						else if(words[2].equals("Q"))
							number=12;
						else if(words[2].equals("K"))
							number=13;
						else if(words[2].equals("A"))
							number=14;
					}
					twonum[0]=number;
					if(words[2].length()==1){
						number=new Integer(words[2]).intValue();
					}else{
						if(words[2].equals("10"))
							number=10;
						else if(words[2].equals("J"))
							number=11;
						else if(words[2].equals("Q"))
							number=12;
						else if(words[2].equals("K"))
							number=13;
						else if(words[2].equals("A"))
							number=14;
					}
					twonum[1]=number;
					break;
				}
				else if(head.equals("inquire")){
					while(!(bufferedReader.readLine()).equals(tmp));
					inquire(printWriter, starts(Blind));	
					break;			
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void inquire(PrintWriter printWriter,String context){
		printWriter.println(context);
		printWriter.flush();
	}
	public static String starts(int Blind){		
		if(twonum[0]==twonum[1]&&twonum[0]>=12||(Math.max(twonum[0], twonum[1])==14)&&(Math.min(twonum[0], twonum[1])>=11)){
			return "raise "+10*Blind;
		}else{
			return "check";
		}
	}
	public String publicPokers(int BB){
		return "raise "+5*BB;
	}
}
