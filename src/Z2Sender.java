import java.net.*;
import java.util.*;

class Z2Sender
{
	static final int datagramSize=50;
	static final int sleepTime=500;
	static final int maxPacket=50;
	InetAddress localHost;
	int destinationPort;
	DatagramSocket socket;
	SenderThread sender;
	ReceiverThread receiver;
	public static List <Memory > memory = new ArrayList <Memory >();

	Thread retransmissionControl = new Thread()
	{
		public void run()
		{
			try 
			{
				while (true)
				{
					for (int j=0; j<memory.size(); j++) 
					{
						if (memory.get(j).t-System.currentTimeMillis() < 4000) 
						{
							Z2Packet retransmitowany = new Z2Packet(1);
							retransmitowany.data = memory.get(j).pa.data;
							retransmitowany.setIntAt(memory.get(j).nr, 0);
							DatagramPacket datagramRetransmitowany = new DatagramPacket(retransmitowany.data , retransmitowany.data.length ,localHost , destinationPort);
							socket.send(datagramRetransmitowany);
							memory.get(j).t = System.currentTimeMillis();
							System.out.println("Retransmisja: "+ memory.get(j).nr);
						}
					}
					sleep(500);
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		};
	};

	public Z2Sender(int myPort, int destPort)
			throws Exception
			{
		localHost=InetAddress.getByName("127.0.0.1");
		destinationPort=destPort;
		socket=new DatagramSocket(myPort);
		sender=new SenderThread();
		receiver=new ReceiverThread();
			}

	class SenderThread extends Thread
	{
		public void run()
		{
			int i, x;
			try
			{
				for(i=0; (x=System.in.read()) >= 0 ; i++)
				{
					Z2Packet p=new Z2Packet(4+1);
					p.setIntAt(i,0);
					p.data[4]= (byte) x;
					DatagramPacket packet = 
							new DatagramPacket(p.data, p.data.length, 
									localHost, destinationPort);
					socket.send(packet);
					sleep(sleepTime);
					Memory wyslany = new Memory(i,System.currentTimeMillis(),p);
					memory.add(wyslany);
				}
			}
			catch(Exception e)
			{
				System.out.println("Z2Sender.SenderThread.run: "+e);
			}
		}

	}



	class ReceiverThread extends Thread
	{
		public void run()
		{
			try
			{
				while(true)
				{
					byte[] data=new byte[datagramSize];
					DatagramPacket packet = new DatagramPacket(data, datagramSize);
					socket.receive(packet);
					Z2Packet p=new Z2Packet(packet.getData());
					for (int i=0; i<memory.size(); i++) //usuwanie z pamieci tych, ktore zostaly potwierdzone
					{
						if ( p.getIntAt(0)<=memory.get(i).nr)
						{
							memory.remove(i);
						}
					}
					System.out.println("S:"+p.getIntAt(0)+": "+(char) p.data[4]);
				}
			}
			catch(Exception e)
			{
				System.out.println("Z2Sender.ReceiverThread.run: "+e);
			}
		}

	}


	public static void main(String[] args)
			throws Exception
			{
		Z2Sender sender=new Z2Sender( Integer.parseInt(args[0]),
				Integer.parseInt(args[1]));
		sender.sender.start();
		sender.receiver.start();
		sender.retransmissionControl.start();
			}



}

class Memory 
{
	public int nr; // numer wyslanego pakietu
	public long t; // czas w którym zosta³a wys³ana
	public Z2Packet pa;
	public Memory(int ord, long pt, Z2Packet mess)
	{
		this.nr = ord;
		this.t = pt;
		this.pa = mess;
	}
}
