package qzhwebserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.UID;
import java.util.ArrayList;

public class Server {

	private ArrayList<Socket> ClientArray = new ArrayList<>(); //泛型构造
	private static int PortMain = 11806;
	private static int PortStandBy =22806;
	private static String rootpath;  //设置服务器的根目录
	private static int MaxConnect = 100; //设置最大的并发连接数目为100
	public static ArrayList<String> FileList = new ArrayList<>();
	private Gui mGui;
	private StringBuffer tmBuffer = new StringBuffer();
	private static ServerSocket myServerSocket = null;
	
	
	private static void FileTranverse()
	{
		File tmp = new File(rootpath);
		if(tmp.isDirectory())
		{
			String [] filelist = tmp.list();
			for(String temp:filelist)
			{
				FileList.add(temp);
			}
		}
	}
	public void setUi(Gui ui)
	{
		this.mGui = ui;
	}
	
	public Server(int port) throws IOException {
		// TODO Auto-generated constructor stub
		myServerSocket = new ServerSocket(port,MaxConnect);	
	}
	/**
	 * Function: after bind the local port begin to wait for remote connection
	 * 
	 */
	public void start()
	{
		
		if(myServerSocket == null||myServerSocket.isClosed())
		{
			return;
		}
		while(true)  //dead loop 
		{
			try {
		//		System.out.println("Waitng for connectting");

				Socket socket = myServerSocket.accept();
				ClientArray.add(socket);
				if(tmBuffer.length()>0)
				{
					tmBuffer.delete(0, tmBuffer.length());
				}
				tmBuffer.append("New Connect Comes with ip: ");
				tmBuffer.append(socket.getInetAddress().toString());
				tmBuffer.append("\n");
				if(tmBuffer.length()>0){
					mGui.appendText(tmBuffer.toString());
					
					//System.out.println(tmBuffer.toString());
				}
				mGui.addVisit();
				new Thread(() -> {
					new HandlerClient(socket,rootpath,mGui).Run();
				}).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error happen when waiting for client");
				try {
					myServerSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ClearSocket();
				break;
			}
		}
	}
	/**
	 * function:when exceptio happen, use this function to clear the running sockte, free resource
	 *@author qzh
	 *@date:12/23/2016
	 *@version:1.0
	 * */
	public void ClearSocket() {
		
		for(Socket socket:ClientArray)
		{
			if(socket!=null&&!socket.isClosed())
			{
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	public void CloseServer() {
		if(myServerSocket!=null)
		{
			try {
				
				myServerSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error Happen when closing socket");
				e.printStackTrace();
			}
		}
	}
	public void SetRoot(String path) {
		this.rootpath = path;
	}
	public static void main(String[] args) {
		try {
			Server temp = new Server(806);
			temp.SetRoot("e:/Document/MywebServer");
			temp.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Initialize Server failed");
			e.printStackTrace();
		}
	}
}
