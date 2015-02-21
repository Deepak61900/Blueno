package com.example.blue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	private BluetoothAdapter btAdapter;
	LinearLayout parent;
	//private TextView print;
	private Button turnOn;
	public boolean deviceFound = false;
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	private BluetoothDevice remoteDevice;
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	ConnectedThread connectedThread;
	
	float x,y;
	  float refY;
	  float refX;
	  boolean valid;
	  float mAngle;
	  MyView myView;
	  boolean small = true;
	
	private class ConnectThread extends Thread {
		
		private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	        try {
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { 
	        	
	        }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        btAdapter.cancelDiscovery();
	        try {
	            mmSocket.connect();
	        } catch (IOException connectException) {	
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	        connectedThread = new ConnectedThread(mmSocket);
			String s = "successfully connected";
			connectedThread.write(s.getBytes());
			loop();
	    }
	 
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

	private class ConnectedThread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	    
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	    
	BroadcastReceiver bluetoothState = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String stateExtra = BluetoothAdapter.EXTRA_STATE;
			String toast;
			int state = intent.getIntExtra(stateExtra, -1);
			if (state == BluetoothAdapter.STATE_TURNING_ON) {
				toast = "Bluetooth Turning On";
			} else if (state == BluetoothAdapter.STATE_ON) {
				toast = "Bluetooth On";
				SetupUI();
			} else if (state == BluetoothAdapter.STATE_OFF) {
				toast = "Bluetooth Off";
				SetupUI();
			} else
				toast = "Unknown Bluetooth State";
			Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
		}
	};

	BroadcastReceiver discoveryResult = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				//print.append("Finished\n");
			}
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				//print.append("\n  Device: " + device.getName() + ", " + device);
				btDeviceList.add(device);
				if (device.getName().equals("SLAVE_TMP") && !deviceFound) {
					deviceFound = true;
					btAdapter.cancelDiscovery();
					ConnectThread connect = new ConnectThread(device);
					connect.start();
				}
			}
			
			else {
			if (BluetoothDevice.ACTION_UUID.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
					for (int i = 0; i < uuidExtra.length; i++) {
						//print.append("\n  Device: " + device.getName() + ", "+ device + ", Service: "+ uuidExtra[i].toString());
					}
			}
			else
			{
				}
			}
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myView = new MyView(this);
		turnOn = new Button(this);
        setContentView(R.layout.activity_main);
        parent = (LinearLayout)findViewById(R.id.parent);
        //parent.addView(turnOn,0);
        parent.addView(myView,0);
		//print = (TextView) findViewById(R.id.print);
		//print.setBackgroundResource(R.color.black);
		valid = false;
        refX = 0;
        refY = 0;
        //myView.setOnTouchListener(this);
		SetupUI();
		findDevice();
//		turnOn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {	
//				findDevice();
//			}
//		});
	}
	
	public boolean onTouchEvent(MotionEvent ev){
        x = ev.getX();
        y = ev.getY();
        if(ev.getAction() == MotionEvent.ACTION_DOWN)
        {
        	refX = ev.getX();
    		refY = ev.getY();
        }
        if(Math.sqrt(Math.pow(x-refX,2)+Math.pow(y-refY,2)) < 30)
        	small = true;
        else
        	small = false;
        
        if(ev.getAction() == MotionEvent.ACTION_UP)
        {
        	small = true;
        }
        return true;
     }

	//@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		refX = event.getX();
//		refY = event.getY();
//		return false;
//	}

	public void findDevice() {
		String lastUsedRemoteDevice = getLastUsedRemoteBTDevice();
		if (lastUsedRemoteDevice != null) {
			Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
			for (BluetoothDevice pairedDevice : pairedDevices) {
				if (pairedDevice.getAddress().equals(lastUsedRemoteDevice)) {
					remoteDevice = pairedDevice;
					//print.append(remoteDevice.getName() + "\n");
				}
			}
		}
		if (remoteDevice == null) {
			Toast.makeText(MainActivity.this, "Discovering", Toast.LENGTH_SHORT)
					.show();
			if (btAdapter.startDiscovery()) {
				//print.append("Scanning...");
				IntentFilter filter = new IntentFilter(
						BluetoothDevice.ACTION_FOUND);
				filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
				filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				registerReceiver(discoveryResult, filter);
			}
		}
	}

	private String getLastUsedRemoteBTDevice() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String result = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);
		return result;
	}

	public void SetupUI()

	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter.isEnabled()) {
			//print.append(btAdapter.getName() + " = " + btAdapter.getAddress()
					//+ "\n");
		} else {
			String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
			String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
			startActivityForResult(new Intent(actionRequestEnable), 0);
			//print.append(btAdapter.getName() + " = " + btAdapter.getAddress()
				//	+ "\n");
			IntentFilter filter = new IntentFilter(actionStateChanged);
			registerReceiver(bluetoothState, filter);
		}
	}
	
	public void loop()
	{
		byte[] ch;
		int i;
		String total;
		while(true)
		{
			try
			{
				if(connectedThread.mmInStream.available() != 0)
				{
					ch = new byte[1];
					//connectedThread.mmInStream.read(ch);
					i = connectedThread.mmInStream.read();
					ch[0] = (byte)i;
					if(ch[0] == 'a')
					{
						mAngle = (float)Math.atan2((y-refY),(x-refX));
				        if(mAngle < 0)
					       {
					    	   mAngle += 2*Math.PI;
					       }
				        i = (int)(mAngle*100);
				        if(small)
				        	i += 1000;
						total = String.valueOf(i);
						connectedThread.write(total.getBytes());
						ch[0] = '\n';
						connectedThread.write(ch);
					}
				}
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	public class MyView extends View
    {
        public MyView(Context context) 
        {
             super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) 
        {
           super.onDraw(canvas);
           int radius;
           radius = 50;
           Paint paint = new Paint();
           paint.setStyle(Paint.Style.FILL);
           paint.setColor(Color.WHITE);
           canvas.drawPaint(paint);
           // Use Color.parseColor to define HTML colors
           paint.setColor(Color.parseColor("#CD5C5C"));
           canvas.drawCircle(refX, refY, radius,paint);
           paint.setColor(Color.parseColor("#444444"));
           canvas.drawCircle(x, y, radius/2,paint);
           invalidate();
       }
    }
	
}











//LinearLayout parent = (LinearLayout)findViewById(R.id.parent);
////creating instance of custom view
//DrawView drawView = new DrawView(this);
////adding custom view as the first child
//parent.addView(drawView,0);
////adding button dynamically
//Button btn = new Button(this);
//btn.setText("Ok");
//parent.addView(btn,1);
	