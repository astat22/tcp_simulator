import java.net.*;
import java.util.*;

public class Z2Receiver
{
static final int datagramSize=50;
InetAddress localHost;
int destinationPort;
DatagramSocket socket;
List <Z2Packet> received = new  ArrayList<Z2Packet>();
ReceiverThread receiver;
static int numerPakietu = 0;

public Z2Receiver(int myPort, int destPort)
throws Exception
    {
    localHost=InetAddress.getByName("127.0.0.1");
    destinationPort=destPort;
    socket=new DatagramSocket(myPort);
    receiver=new ReceiverThread();    
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
		    DatagramPacket packet = 	new DatagramPacket(data, datagramSize);
		    socket.receive(packet);
            Z2Packet p=new Z2Packet(packet.getData());
            received.add(p);		//dodaj do listy otrzymanych
					
			for (int i=0; i<received.size(); i++) 	//w kazdej iteracji while wypisz otrzymane
			{
				if(received.get(i).getIntAt(0) == numerPakietu) //wypisz aktualny pakiet
				{
					numerPakietu++;		//ok. mozesz czekac na kolejny pakiet
					System.out.println("R:"+received.get(i).getIntAt(0)+": "+(char) received.get(i).data[4]);
					received.remove(received.get(i));	
		            // WYSLANIE POTWIERDZENIA
				}
			}			
            packet.setPort(destinationPort);
            socket.send(packet);
		}
	}
    catch(Exception e)
	{
        System.out.println("Z2Receiver.ReceiverThread.run: "+e);
	}
    }

}

public static void main(String[] args)
throws Exception
    {
	Z2Receiver receiver=new Z2Receiver( Integer.parseInt(args[0]),
				   Integer.parseInt(args[1]));
	receiver.receiver.start();
    }


}
