package qzhwebserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

import javax.lang.model.element.Element;

/*
 * the class handle communication between clien and localhost
 * @author:Qiu Zenghui
 * @date: 12/23/2016
 * @version:1.0
 * **/
public class HandlerClient {

	private static String htmlPath;  //�������������ļ���ȡ�ֱ��Ӧ���������ڸ����ļ�������ͼѰ��
	private static String imgPath;
	private static String txtPath;
	private static String icoPath;
	private static String legalUsr = "3140100806"; //my id
	private static String legalPass = "2316";  //password
	
	
	private boolean PackOrNot = false;  //�費��Ҫ����htmlͷ����β���ı�ǩ
	private String userName;  //��¼��¼��ʱ����û���������
	private String password;
	private OutputStream ouStream = null;
	private BufferedReader fReader;
	private BufferedInputStream fStream;
	private StringBuffer resbuff = new StringBuffer();
	private StringBuffer contextBuffer = new StringBuffer();
	private Socket mySocket;
	private boolean isConnected = false;
	StringBuffer stringBuffer = new StringBuffer();
	byte[] Sendbuffer = new byte[4096];
	char [] recvBuffer = new char[4096];
	int ret=0;
	BufferedReader Mread = null;
	Writer mwWriter;
	private String rootPath;   //����·��
	private int CurrentFileLength = -1;
	private String absolutPath = null;
	private Gui mGui;
	public HandlerClient(Socket sock,String rootPath,Gui ui) {
		
		// TODO Auto-generated constructor stub
		this.rootPath = rootPath;
		this.mySocket = sock;
		this.mGui = ui;
		htmlPath = rootPath+"/html";
		imgPath = rootPath + "/img";
		txtPath = rootPath +"/txt";
		icoPath = rootPath +"/ico";
		if(sock !=null)
		{
			try {
				ouStream = sock.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Warning: Getting outputstream failed!!");
				e.printStackTrace();
			}
		}
		isConnected = true;
		
	}
	public int GetFileLength(String filename) {
		
		if(filename.indexOf(".html")!=-1)
		{
			absolutPath = htmlPath + filename;
		}
		else if(filename.indexOf(".jpg")!=-1)
		{
			absolutPath = imgPath + filename;
		}
		else if(filename.indexOf(".txt")!=-1)
		{
			absolutPath = txtPath + filename;
		}
		else if(filename.indexOf(".ico")!=-1)
		{
			absolutPath = icoPath + filename;
		}
		else{
			return -1; //�������������ļ�ֱ�ӷ���-1
		}
		System.out.println(absolutPath);
		File temp = new File(absolutPath);
		if(!temp.exists())
			return -1;
		else{
			return (int) temp.length();
		}
	}
	private boolean CheckfileExits(String path)
	{
		String absolutPath = rootPath +path;
		File temp = new File(absolutPath);
		if(temp.exists())
			return true;
		return false;
	}
	/**
	 * function:check whether uer name and pass id legal
	 * @param info: the line contain both " login = " and "pass="
	 * @author qzh
	 * @date:12/23/2016
	 * @version:1.0s
	 * */
	private boolean checkUserPass(String Info)
	{
		
		System.out.println(Info);
		int index1 = Info.indexOf('='); 
		int index2 = Info.indexOf('&');
		int index3 = Info.indexOf(' ', index2);
		String loginUsr = Info.substring(index1+1,index2);
//		System.out.println("User "+loginUsr);
		if(resbuff.length()>0)
		{
			resbuff.delete(0, resbuff.length());
		}
		resbuff.append(GetDate());
		resbuff.append("\nLogin attempt from ");
		resbuff.append(mySocket.getInetAddress().toString());
		resbuff.append("with User id=");
		resbuff.append(loginUsr);
		if(mGui!=null)
			mGui.appendText(recvBuffer.toString());
		
		if(!loginUsr.equals(legalUsr))
		{
			resbuff.append(" failed\n");
			if(mGui!=null)
				mGui.appendText(resbuff.toString());
			resbuff.delete(0, resbuff.length());
			return false;
		}
		index2 = Info.indexOf('=', index1+1);
		String loginPass = Info.substring(index2+1,index3);
		resbuff.append(" with pass=");
		resbuff.append(loginPass);
		
		if(!loginPass.equals(legalPass))
		{
			resbuff.append(" failed\n");
			if(mGui!=null)
				mGui.appendText(resbuff.toString());
			resbuff.delete(0, resbuff.length());
			return false;
		}
		resbuff.append(" success\n");
		if(mGui!=null)
			mGui.appendText(resbuff.toString());
		resbuff.delete(0, resbuff.length());
		return true;
	}
	public void sendImage() throws FileNotFoundException  //��ͼƬ�����ӷ��͹�ȥ
	{

		if(resbuff.length()>0)
		{
			resbuff.delete(0, resbuff.length());
		}
		resbuff.append(GetDate());
		resbuff.append("\nSending file " + absolutPath+" to ");
		resbuff.append(mySocket.getInetAddress().toString());
		resbuff.append("\n");
		if(mGui!=null)
			mGui.appendText(resbuff.toString());
		resbuff.delete(0, resbuff.length());
		BufferedInputStream fStream = null;
	//	mGui.appendText(msg);
		fStream = new BufferedInputStream(new FileInputStream(absolutPath));
		int ret;
		try {
			while((ret=fStream.read(Sendbuffer))!=-1)
				if(ouStream!=null)
				{
					ouStream.write(Sendbuffer, 0, ret);
					ouStream.flush();
				}
			
			if(mwWriter!=null)
			{
				mwWriter.write("\r\n\r\n");
				mwWriter.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		finally{
			try {
				fStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error happen when closing image file");
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 
	 * functon:send a file requeted by client
	 * 
	 * */
	public void SendFile() {
		
		String message = "";
		BufferedReader mfReader = null;
		try {
			mfReader = new BufferedReader(new InputStreamReader(new FileInputStream(absolutPath)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error happen when opening file");
			e1.printStackTrace();
		} 
		if(resbuff.length()>0)
		{
			resbuff.delete(0, resbuff.length());
		}
		resbuff.append(GetDate());
		resbuff.append("\nSending file " + absolutPath+" to ");
		resbuff.append("Sending file " + absolutPath+" to ");
		resbuff.append(mySocket.getInetAddress().toString());
		resbuff.append("\n");
		System.out.println(resbuff.toString());
		if(mGui!=null)
			mGui.appendText(resbuff.toString());
		resbuff.delete(0, resbuff.length());
		StringBuffer MycontextBuffer = new StringBuffer();
		try {
			while((message = mfReader.readLine()) != null)
			{
				MycontextBuffer.append(message+"\r\n");
			}
			if(PackOrNot)
			{
				System.out.println(MycontextBuffer.toString());
				if(mwWriter != null){
					mwWriter.write("<html><body>");
					mwWriter.write(MycontextBuffer.toString());
					mwWriter.write("</body></html>");
					mwWriter.write("\r\n\r\n");
					mwWriter.flush();
				}	
			}
			else {
				MycontextBuffer.append("\r\n");
				System.out.println(MycontextBuffer.toString());
				if(mwWriter != null){
					mwWriter.write(MycontextBuffer.toString());
					mwWriter.write("\r\n\r\n");
					mwWriter.flush();
				}
			}
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error happen when sending files");
			}
		finally {
			try {
				mfReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//new HandlerClient(null, "e:/Document/MywebServer").SendFile("/html/noimage.html");
	}
	
	
	private String GetDate()
	{
		Date now=new Date();
		return now.toString();
	}
	
	public void Run() {
		try {
			Mread = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			mwWriter = new OutputStreamWriter(mySocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("could not get information from server");
		}
		while(isConnected)
		{
			try {
				ret = Mread.read(recvBuffer);
				if(ret == -1)  //Զ�̹ر�������
				{
					System.out.println("Client has close the connection");
					break;
				}
				String str;
				str = new String(recvBuffer, 0, ret);
				System.out.println(str);
				if(str.indexOf("\r\n\r\n")!=-1)  //�������������Ļس����з�����ظ�
				{
					try {
						response(str);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						ResponseError(e.getMessage()); //�ظ�����
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block

				if(e.getMessage().indexOf("Connection reset")!=-1);
				{	
				//	isConnected=false;
					if(mySocket!=null)
					{
						try {
							mySocket.close();
							isConnected = false;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				e.printStackTrace();
			}
		}
		
	}
	
	// HTTP_NOT_ACCEPTABLE HTTP_BAD_REQUEST  Code.HTTP_NOT_FOUND
	//������Щ�����뷢�ʹ�����Ϣ
	public void ResponseError(String errorMsg)
	{
		int errorCode = Integer.valueOf(errorMsg);
		System.out.println(errorMsg);
		if(resbuff.length()>0)
		{
			resbuff.delete(0, resbuff.length());
		}
		// 					mwWriter.write("<html><body>");
		if(errorCode == Code.HTTP_NOT_FOUND)  //�û��������������
		{
			resbuff.append("HTTP/1.1 404 File Not Found\r\n");
			resbuff.append("Content-Type: text/html\r\n");
			resbuff.append("Content-Length: 53\r\n\r\n");
			if(mwWriter!=null)
			{
				try {
					mwWriter.write(resbuff.toString());
					mwWriter.flush();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Error happen when sending error message");
				}
			}
			try {
				mwWriter.write("<html><body><h2>404 File Not Found</h2></html></body>");
				mwWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(errorCode == Code.HTTP_NOT_ACCEPTABLE)
		{
			resbuff.append("HTTP/1.1 406 Not Acceptable\r\n");
			resbuff.append("Content-Type: text/html\r\n");
			resbuff.append("Content-Length: 66\r\n\r\n");
			if(mwWriter!=null)
			{
				try {
					mwWriter.write(resbuff.toString());
					mwWriter.flush();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Error happen when sending error message");
				}
			}
			try {
				mwWriter.write("<html><body><h2>406 User name or password wrong</h2></body></html>");
				mwWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			resbuff.append("HTTP/1.1 500 Internel Error\r\n");
			resbuff.append("Content-Type: text/html\r\n");
			resbuff.append("Content-Length: 53\r\n\r\n");
			if(mwWriter!=null)
			{
				try {
					mwWriter.write(resbuff.toString());
					mwWriter.flush();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Error happen when sending error message");
				}
			}
			try {
				mwWriter.write("<html><body><h2>500 Internel Error</h2></body></html>");
				mwWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ������Ӧ����
	 * @author qzh
	 * @date:12-23/2016
	 * @version: 1.0
	 * */
	
	public void response(String request) throws Exception
	{
		
		int index1,index2,index3;
		String filepath,filename;
		index1 = request.indexOf("GET");
		index2 = request.indexOf("?");
		int indexSpace1;
		int indexSpace2 = -1;
		System.out.println(request);
		if(index1 != -1)  //get ����
		{
			
			indexSpace1 = request.indexOf(" ");
			if(index2!=-1)  //?�ĳ��ֱ�ʾ�������һ����¼����
			{
				String confirm = request.substring(indexSpace1+1, index2);
				if(confirm.indexOf("dopost")!=-1) //�ҵ��û���������
				{
					if(!checkUserPass(request.substring(0, request.indexOf("\n"))))
					{
						int ErrorCode = Code.HTTP_NOT_ACCEPTABLE; //�����Ҳ���
						throw new Exception(new String("")+ErrorCode);
					}
					//���͵�¼�ɹ����ļ���info.txt
				}
				if(resbuff.length()>0)
				{
					resbuff.delete(0, resbuff.length());
				}
				CurrentFileLength = GetFileLength("/info.txt");
				//CurrentFileLength += 6;
				System.out.print("ahha "+CurrentFileLength);
				resbuff.append(HeadComponent.HeadVersion+ Code.HTTP_OK +" OK\r\n");
				resbuff.append(HeadComponent.ContentType+"text/html"+"\r\n");
				resbuff.append(HeadComponent.ContentLength +CurrentFileLength+"\r\n");
				resbuff.append("Date: "+GetDate()+"\r\n\r\n");
				if(mwWriter !=null)
				{
					mwWriter.write(resbuff.toString());
			//		mwWriter.write("\r\n");
					mwWriter.flush();
				}
				System.out.println(resbuff.toString());		
		//		absolutPath = "e:/Document/MywebServer/txt/info.txt";
				absolutPath = rootPath + "/txt/info.txt";
		//		PackOrNot = true;
				SendFile();
		//		PackOrNot = false;
				return ;
			}		
			if(indexSpace1!=-1)
			{
				indexSpace2 = request.indexOf(" ", indexSpace1+1);  //�Ӻ�ʼ�ĵ�һ���ո��
			}
			if(indexSpace1==-1||indexSpace2==-1)
			{
				int ErrorCode = Code.HTTP_NOT_FOUND;
				throw new Exception(new String("")+ErrorCode);
			}
			filepath = request.substring(indexSpace1+1, indexSpace2);  //�ļ�·��
			//�ҵ����һ��/���ֵ�λ��
			index3 = filepath.lastIndexOf('/');
			filename = filepath.substring(index3, filepath.length());
			System.out.println(filename);
			CurrentFileLength = GetFileLength(filename);  //�����ļ����͵���ͬ�ļ�����Ѱ���ļ�
			if(CurrentFileLength < 0)
			{
				int ErrorCode = Code.HTTP_NOT_FOUND; //�����Ҳ���
				throw new Exception(new String("")+ErrorCode);
			}
			else if(absolutPath.indexOf(".html")!=-1){
				System.out.println("Found html file");
				if(resbuff.length()>0)
				{
					resbuff.delete(0, resbuff.length());
				}
				resbuff.append(HeadComponent.HeadVersion+ Code.HTTP_OK +" OK\r\n");
				resbuff.append(HeadComponent.ContentType+"text/html"+"\r\n");
				resbuff.append(HeadComponent.ContentLength +CurrentFileLength+"\r\n");
				resbuff.append("Date: "+GetDate()+"\r\n");
				if(mwWriter !=null)
				{
					mwWriter.write(resbuff.toString());
					mwWriter.write("\r\n");
				}
				System.out.println(resbuff.toString());
				mwWriter.flush();
				SendFile();
			}
			else if(absolutPath.indexOf(".txt")!=-1)
			{
				System.out.println("Found txt file");
				if(resbuff.length()>0)
				{
					resbuff.delete(0, resbuff.length());
				}
				//�����ļ���ͷ����β���ĳ���
				CurrentFileLength += 26;  // <html><body></body></html>�����ⲿ�ֵĳ���
				resbuff.append(HeadComponent.HeadVersion+ Code.HTTP_OK +" OK\r\n");
				resbuff.append(HeadComponent.ContentType+"text/html"+"\r\n");
				resbuff.append(HeadComponent.ContentLength +CurrentFileLength+"\r\n");
				resbuff.append("Date: "+GetDate()+"\r\n");
				if(mwWriter !=null)
				{
					mwWriter.write(resbuff.toString());
					mwWriter.write("\r\n");
				}
				System.out.println(resbuff.toString());
				mwWriter.flush();
				PackOrNot = true;  //���߷��ļ����������html�ļ����ײ���β��
				SendFile();
				PackOrNot = false;
			}
			else  //����ͼƬ������
			{
				if(resbuff.length()>0)
				{
					resbuff.delete(0, resbuff.length());
				}
				resbuff.append(HeadComponent.HeadVersion+ Code.HTTP_OK +" OK\r\n");
				if(filepath.indexOf(".jpg")!=-1)
					resbuff.append(HeadComponent.ContentType+"image/jpg"+"\r\n");
				else if(filepath.indexOf(".ico")!=-1)
					resbuff.append(HeadComponent.ContentType+"image/ico"+"\r\n");
				resbuff.append(HeadComponent.ContentLength +CurrentFileLength+"\r\n");
				resbuff.append("Date: "+GetDate()+"\r\n");
				if(mwWriter !=null)
				{
					try {
						mwWriter.write(resbuff.toString());
						mwWriter.write("\r\n");
						mwWriter.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(resbuff.toString());
				}
				try {
					sendImage();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
		}
		
	}
}

