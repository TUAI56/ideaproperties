package com.bonc.framework.common.util.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

public class FTPUtil {

	/** 

	 * Description: 向FTP服务器上传文件 

	 * @param host FTP服务器hostname 

	 * @param port FTP服务器端口 

	 * @param username FTP登录账号 

	 * @param password FTP登录密码 

	 * @param filePath FTP服务器文件存放路径。文件的路径为filePath

	 * @param filename 上传到FTP服务器上的文件名 

	 * @param input 输入流 

	 * @return 成功返回true，否则返回false 

	 */  

	public static boolean uploadFile(String host, int port, String username, String password,

			String filePath, String filename, InputStream input) {

		System.out.println(host+"=="+port+"=="+username+"=="+password+"=="+filePath+"=="+filename);
		boolean result = false;

		FTPClient ftp = new FTPClient();

		try {

			int reply;

			ftp.connect(host, port);// 连接FTP服务器

			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器

			ftp.login(username, password);// 登录

			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {

				ftp.disconnect();

				return result;

			}

			//切换到上传目录

			if (!ftp.changeWorkingDirectory(filePath)) {

				//如果目录不存在创建目录

				System.out.println("上传目录不存在");

			}

			//设置上传文件的类型为二进制类型

			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			//上传文件

			if (!ftp.storeFile(filename, input)) {
				System.out.println("file upload faild"+'\n');
				return result;

			}else {
				System.out.println("file upload success"+'\n');
			}

			input.close();

			ftp.logout();

			result = true;

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (ftp.isConnected()) {

				try {

					ftp.disconnect();

				} catch (IOException ioe) {

				}

			}

		}

		return result;

	}

	

	/** 

	 * Description: 从FTP服务器下载文件 

	 * @param host FTP服务器hostname 

	 * @param port FTP服务器端口 

	 * @param username FTP登录账号 

	 * @param password FTP登录密码 

	 * @param remotePath FTP服务器上的相对路径 

	 * @param fileName 要下载的文件名 

	 * @param localPath 下载后保存到本地的路径 

	 * @return 

	 */  

	public static boolean downloadFile(String host, int port, String username, String password, String remotePath,

			String fileName, String localPath) {

		boolean result = false;

		FTPClient ftp = new FTPClient();

		try {

			int reply;

			ftp.connect(host, port);

			// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器

			ftp.login(username, password);// 登录

			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {

				ftp.disconnect();

				return result;

			}
			ftp.setControlEncoding("UTF-8");
			ftp.setFileType(ftp.BINARY_FILE_TYPE);
			ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录

			FTPFile[] fs = ftp.listFiles();

			for (FTPFile ff : fs) {

				if (ff.getName().equals(fileName)) {

					File localFile = new File(localPath + "/" + ff.getName());

 

					OutputStream is = new FileOutputStream(localFile);

					ftp.retrieveFile(ff.getName(), is);

					is.close();

				}

			}

 

			ftp.logout();

			result = true;

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (ftp.isConnected()) {

				try {

					ftp.disconnect();

				} catch (IOException ioe) {

				}

			}

		}

		return result;

	}

 

	//ftp上传文件测试main函数

	public static void main(String[] args) {

		try {  
			//ftp://133.96.10.66/temp

	        FileInputStream in=new FileInputStream(new File("F:\\downloads\\低产能分析__2019_04_16_16_43_41.xlsx"));  
	        System.out.println(uploadFile("133.96.10.66", 21, "zzqs", "Passw0rd!@#", "/hbman", "低产能分析__2019_04_16_16_43_41.xlsx", in)); 
//	        boolean flag = uploadFile("133.96.10.66", 21, "zzqs", "Passw0rd!@#", "", "3.xls", in); 
//	        boolean flag = downloadFile("133.96.10.66", 21, "zzqs", "Passw0rd!@#","/temp","4c546109a42642a6ba8e6ae8780b6622.xls","E:\\");
//ftp://133.96.10.66/temp/0b8faa4823ff40eba3d41f10f834b58a.xls
//	        String str="ftp://133.96.10.66/temp/0b8faa4823ff40eba3d41f10f834b58a.xls";
//	        String[]  strs=str.split("/");
//	        for(int i=0,len=strs.length;i<len;i++){
//	            System.out.println(strs[i].toString());
//	        }

//	        System.out.println(flag);  

	    } catch (FileNotFoundException e) {  

	        e.printStackTrace();  

	    }  

	}
}
