package com.bbg.dc;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bbg.dc.DCGame;

public class DesktopLauncher {
	public static void main(String[] arg) {
		

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");		
		config.vSyncEnabled = false;
		//if(opt.framelimit) {
			config.foregroundFPS = 60;
			config.backgroundFPS = 60;		
		//} else {
			//config.foregroundFPS = 0;
			//config.backgroundFPS = 0;	
		//}
		new LwjglApplication(new DCGame(), config);
	}
}