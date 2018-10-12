package com.service;

import java.io.IOException;

public class StartServer {
	public static void main(String[] args) {
		new Thread() {
			@Override
			public void run() {
				try {
					LoginService.openLoginServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					RegistUserService.openRegistService();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					DownloadService.openDownloadService();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					UploadService.openUploadService();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		System.out.println("服务器启动成功");
	}
}
