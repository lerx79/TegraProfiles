package com.itslightness.tegraprofiles;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	/* 
	 * @author ebildude123
	 * Coded for Asus Transformer Pad TF300T (Tegra 3)
	 * Used as Reference for Modes: https://github.com/omnirom/android_device_asus_tf300t/blob/android-4.4/ramdisk/init.cardhu.cpu.rc
	 */
	
	
	public static final String PERFORMANCE_MODE = "2";
	public static final String BALANCED_MODE = "1";
	public static final String POWER_SAVING_MODE = "0";
	
	TextView profileView;
	RadioGroup settingsGroup;
	
	RadioButton perfButton;
	RadioButton balButton;
	RadioButton pwrButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		profileView = (TextView) findViewById(R.id.textView2);
		settingsGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		perfButton = (RadioButton) findViewById(R.id.radio1);
		balButton = (RadioButton) findViewById(R.id.radio0);
		pwrButton = (RadioButton) findViewById(R.id.radio2);
		
		profileView.setText(getPerformanceProfile(true));
		updateRadioButtons();
		
		settingsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio0) {
					setPerformanceProfile(BALANCED_MODE);
				}
				else if (checkedId == R.id.radio1) {
					setPerformanceProfile(PERFORMANCE_MODE);
				}
				else if (checkedId == R.id.radio2) {
					setPerformanceProfile(POWER_SAVING_MODE);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public String getPerformanceProfile(boolean retStrFormat) {
		String getProfile = RunNormally("getprop sys.cpu.mode");
		if (retStrFormat) {
			if (getProfile.equals(PERFORMANCE_MODE)) {
				return "Performance";
			}
			else if (getProfile.equals(BALANCED_MODE)) {
				return "Balanced";
			}
			else if (getProfile.equals(POWER_SAVING_MODE)) {
				return "Power Saving";
			}
			else {
				return "Unknown";
			}
		}
		else {
			return getProfile;
		}
	}
	
	public boolean setPerformanceProfile(String pMode) {
		String buildCmd = "setprop sys.cpu.mode " + pMode;
		boolean setP = RunAsRoot(buildCmd);
		if (setP) {
			String getNewProfMode = getPerformanceProfile(true);
			if (!getPerformanceProfile(false).equals(pMode)) {
				Toast.makeText(getApplicationContext(), "Tegra Profiles: Root access was not granted.", Toast.LENGTH_LONG).show();
				finish();
				return false;
			}
			else {
				profileView.setText(getNewProfMode);
				Toast.makeText(getApplicationContext(), "Successfully changed profile to \"" + getNewProfMode + "\"", Toast.LENGTH_LONG).show();
				return true;
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "Tegra Profiles: Root access was not granted.", Toast.LENGTH_LONG).show();
			finish();
			return false;
		}
	}
	
	public void updateRadioButtons() {
		String profileRaw = getPerformanceProfile(false);
		if (profileRaw.equals(PERFORMANCE_MODE)) {
			perfButton.setChecked(true);
		}
		else if (profileRaw.equals(BALANCED_MODE)) {
			balButton.setChecked(true);
		}
		else if (profileRaw.equals(POWER_SAVING_MODE)) {
			pwrButton.setChecked(true);
		}
	}
	
	public boolean RunAsRoot(String command) {
        Process p;
		try {
			p = Runtime.getRuntime().exec("su");
	        DataOutputStream os = new DataOutputStream(p.getOutputStream());
	        os.writeBytes(command + "\n");
	        os.writeBytes("exit\n");  
	        os.flush();
	        p.waitFor();
	        return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String RunNormally(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
		    BufferedReader bufferedReader = new BufferedReader(
		    new InputStreamReader(process.getInputStream()));

		    StringBuilder log=new StringBuilder();
		    String line;
		    while ((line = bufferedReader.readLine()) != null) {
		    	log.append(line + "\n");
		    }
			return log.toString().trim();
		} catch (IOException e) {}
		return "";
	}

}
