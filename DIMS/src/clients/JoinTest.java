package clients;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class JoinTest {

	public static void main(String[] args) {
		
		MultiClient a = new MultiClient("A", "239.1.2.3", 9090);
		MultiClient b = new MultiClient("B","239.1.2.3", 9090);
		
		a.sendMessage("Hi I'm A");
		
	}

}
