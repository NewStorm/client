package de.gymolching.fsb.gui;

import java.io.*;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import de.gymolching.fsb.client.api.*;
import de.gymolching.fsb.client.implementation.*;

public class Controller// implements Runnable
{
	public static void main(String[] args)
	{
		Controller controller;
		try
		{
			controller = new Controller();
			controller.startGUI();
		}
		catch (InterruptedException | IOException e)
		{
			e.printStackTrace();
		}
	}

	private Display display;
	private Shell shell;

	private FSBClient client;
	private Text text;
	private Button btnSendData;
	private Scale scale;
	private Button button;

	private Random random;
	private Thread demoThread;
	private boolean isDemoThreadCanceled;

	// public void run()
	// {
	// Ask for client IP
	// boolean connected = false;
	// do
	// {
	// try
	// {
	// String serverIP = JOptionPane.showInputDialog("Server ip:", "127.0.0.1");
	// if (serverIP == null)
	// {
	// System.err.println("Canceling connection attempt, exiting...");
	// System.exit(1);
	// }
	// String serverPort = JOptionPane.showInputDialog("Server port:", "666");
	// if (serverPort == null)
	// {
	// System.err.println("Canceling connection attempt, exiting...");
	// System.exit(1);
	// }
	//
	// this.client.connect(serverIP, new Integer(serverPort));
	// connected = true;
	// }
	// catch (Exception e)
	// {
	// System.err.println("Could not connect, pls retry!");
	// }
	// } while (!connected);

	// }

	public Controller() throws InterruptedException, IOException
	{
		this.client = new FSBClient();
		this.client.connect("192.168.1.168", 666);
		this.random = new Random();

		// Thread thread = new Thread(this);
		// thread.start();
		// thread.join();

		this.display = new Display();
		this.shell = new Shell(display);
		shell.setSize(238, 450);
		shell.setLayout(new GridLayout(2, false));

		scale = new Scale(shell, SWT.HORIZONTAL);
		scale.setMinimum(0);
		scale.setMaximum(255);
		scale.setIncrement(1);
		scale.setPageIncrement(5);
		scale.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				text.setText("" + scale.getSelection());
			}
		});
		scale.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		new Label(shell, SWT.NONE);

		text = new Text(shell, SWT.BORDER | SWT.CENTER);
		text.setText("0");
		GridData gd_text = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 182;
		text.setLayoutData(gd_text);
		new Label(shell, SWT.NONE);

		button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				sendData();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				sendData();
			}

			public void sendData()
			{
				try
				{
					client.sendNewPosition(new FSBPosition(text.getText() + ":" + text.getText()
							+ ":" + text.getText() + ":" + text.getText() + ":" + text.getText()
							+ ":" + text.getText() + ":"));
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		button.setText("Send Data");

		btnSendData = new Button(shell, SWT.NONE);
		btnSendData.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				if (demoThread == null)
				{
					isDemoThreadCanceled = false;
					btnSendData.setEnabled(true);
					demoThread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							while (!isDemoThreadCanceled)
							{
								int position = random.nextInt(255);
								try
								{
									client.sendNewPosition(new FSBPosition(position + ":"
											+ position + ":" + position + ":" + position + ":"
											+ position + ":" + position));
								}
								catch (IOException e)
								{
									e.printStackTrace();
									System.exit(1);
								}

								try
								{
									Thread.sleep(100);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
							}
						}
					});
					demoThread.start();
				}
				else
				{
					isDemoThreadCanceled = true;
					try
					{
						demoThread.join();
						demoThread = null;
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		btnSendData.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSendData.setText("Demo Mode");
		;
	}

	public void startGUI()
	{
		this.shell.pack();
		this.shell.open();

		while (!this.shell.isDisposed())
		{
			if (!this.display.readAndDispatch())
			{
				this.display.sleep();
			}
		}

		this.display.dispose();

		try
		{
			this.client.disconnect();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
