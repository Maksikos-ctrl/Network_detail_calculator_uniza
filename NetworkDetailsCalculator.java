/*
 * 
 * 
 * 0xFFFFFFFF is 11111111 11111111 11111111 11111111 in binary, (255.255.255.255) in decimal
 * 
 * 0xFF operation in claculating the network Adress - This operation ensures that only the least significant 8 bits of each result are preserved, effectively masking out the rest of the bits. 
 * 
 * Signed left and right shift operators
 * 0010 << 2 -> 1000 == 8
 * 1000 >> 2 -> 0010 == 2 
 * 
 * 
 * Unsigned right and left shift operators(works as signed but + shift negative decimals)
 * 
 * 0010 >>> 2 -> 0000 == 0
 * 1000 >>> 2 -> 0010 == 2
 * 
 * 
 * 
 * Subnet Mask
 * 
 * 11111111 11111111 11111111 11111111
 * 
 * (subnetMask >> 24 & 0xFF) ^ 0xFF) - bitwise XOR operation, tu put it briefly just invert the bits
 * 
 * [0100 0000]2 = 64, reverse it with XOR = [   ] = 191 is inverted last octet of the subnet mask, then we bitwise OR it with the network adress, invert "inverted octet" and will get 
 * 
 */






import java.net.InetAddress;
import java.util.Scanner;
import java.net.UnknownHostException;


public class NetworkDetailsCalculator {

    private static int calcSubnetMask(int prefix) {
        return 0xFFFFFFFF << (32 - prefix);
    } 


    private static String getDottedDecSubMask(int subnetMask) {
        StringBuilder dottedDecimalMask = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int octet = (subnetMask >> (24 - 8 * i)) & 0xFF;
            dottedDecimalMask.append(octet);

            if (i != 3) {
                dottedDecimalMask.append(".");
            }

        }

        return dottedDecimalMask.toString();

    }


    private static InetAddress calcNetworkAdress(InetAddress ipHost, int subnetMask) {
        try {
            byte[] ipBytes = ipHost.getAddress();
            byte[] subnetBytes = {(byte) (subnetMask >> 24 & 0xFF), (byte) (subnetMask >> 16 & 0xFF), (byte) (subnetMask >> 8 & 0xFF), (byte) (subnetMask & 0xFF)};

            byte[] networkBytes = new byte[4];

            for (int i = 0; i < 4; i++) {
                networkBytes[i] = (byte) (ipBytes[i] & subnetBytes[i]); // как я говорю своим украинкам, тупо умножаем биты адресса хоста на биты маски подсети, bitwise ANDим по умному
            }

        
            return InetAddress.getByAddress(networkBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }

    }


    private static InetAddress calcFirstAddress(InetAddress networkAddress) {

        byte[] networkBytes = networkAddress.getAddress();
        networkBytes[3] = (byte) (networkBytes[3] + 1);

        try {
            return InetAddress.getByAddress(networkBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static InetAddress calcBroadcastAddress(InetAddress networkAddress, int subnetMask) {

        byte[] networkBytes = networkAddress.getAddress();

        byte[] invertedSubnetBytes = new byte[4];

        invertedSubnetBytes[0] = (byte) ((subnetMask >> 24 & 0xFF) ^ 0xFF);
        invertedSubnetBytes[1] = (byte) ((subnetMask >> 16 & 0xFF) ^ 0xFF); // bitwise XORим
        invertedSubnetBytes[2] = (byte) ((subnetMask >> 8 & 0xFF) ^ 0xFF);
        invertedSubnetBytes[3] = (byte) ((subnetMask & 0xFF) ^ 0xFF);

        byte[] broadcastBytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            broadcastBytes[i] = (byte) (networkBytes[i] | invertedSubnetBytes[i]); // bitwise ORим
        }


        try {
            return InetAddress.getByAddress(broadcastBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }



    }

    private static InetAddress calcLastAdress(InetAddress broadcastAddress) {

        byte[] broadcastBytes = broadcastAddress.getAddress();
        broadcastBytes[3] = (byte) (broadcastBytes[3] - 1);

        try {
            return InetAddress.getByAddress(broadcastBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int calcHostQuantityAddress(int prefix) {

        return (int) Math.pow(2, 32 - prefix) - 2;

    }  
    
    
    private static int calcSubnetQuantity(int prefix) {

        return (int) Math.pow(2, prefix);

    }


    private static void allocateSubnets(int numSubnets, int subnetPrefix, String hosIP) {
        int subnetMask = calcSubnetMask(subnetPrefix);
        InetAddress networkAddress;
        try {
            networkAddress = calcNetworkAdress(InetAddress.getByName(hosIP), subnetMask);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
    
        for (int i = 0; i < numSubnets; i++) {
            int subnetSize = calcSubnetQuantity(subnetPrefix);
            int hostQuantity = calcHostQuantityAddress(subnetPrefix);

            InetAddress firstAddress = calcFirstAddress(networkAddress);
            InetAddress lastAddress = calcLastAdress(calcBroadcastAddress(networkAddress, subnetMask));
            InetAddress broadcastAddress = calcBroadcastAddress(networkAddress, subnetMask);
    
            try {
                
    
                System.out.println("Subnet " + i + ":");
                System.out.println("Subnet Mask: " + getDottedDecSubMask(subnetMask));
                System.out.println("Network Address: " + networkAddress.getHostAddress());
                System.out.println("First Usable Address: " + firstAddress.getHostAddress());
                System.out.println("Last Usable Address: " + lastAddress.getHostAddress());
                System.out.println("Broadcast Address: " + calcBroadcastAddress(networkAddress, subnetMask).getHostAddress());
                System.out.println("Number of Usable Host Addresses: " + (subnetSize - 2));
                System.out.println();
               

                // апдейтим адресс сети для следующего subneta
                networkAddress = InetAddress.getByAddress(calcLastAdress(broadcastAddress).getAddress());
                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    


    public static void main(String[] args) {

        // String ipHost = "192.168.62.120";
        // int subnetPrefix = 30;

        // 86.239.128.0


        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter host adress: ");

        String ipHost = scanner.nextLine();

        System.out.println("Enter subnet prefix: ");

        int subnetPrefix = scanner.nextInt();
        boolean validPrefix = false;


        while (!validPrefix) {
            int numSubnets = 0;
            if (subnetPrefix < 0 || subnetPrefix > 30) {
                System.out.println("Invalid prefix, try again: ");
                subnetPrefix = scanner.nextInt();
            } else {
                validPrefix = true;
            }
     
        }


        System.out.println("Do u want to subnet the network? (y/n): ");
        String answer = scanner.next();

        if (answer.equalsIgnoreCase("y")) {
            System.out.println("Enter the number of subnets:");
            int numSubnets = scanner.nextInt();
            allocateSubnets(numSubnets, subnetPrefix, ipHost);

            
        } else {
            

            int subnetMask = calcSubnetMask(subnetPrefix);
            InetAddress networkAddress = null;
            InetAddress hostAddress = null;
            InetAddress firstAddress = null;
            InetAddress broadcastAddress = null;
            InetAddress lastAddress = null;
            int hostQuantity = calcHostQuantityAddress(subnetPrefix);
            
            


            try {
                networkAddress = calcNetworkAdress(InetAddress.getByName(ipHost), subnetMask);
                hostAddress = InetAddress.getByName(ipHost);
                firstAddress = calcFirstAddress(networkAddress);
                broadcastAddress = calcBroadcastAddress(networkAddress, subnetMask);
                lastAddress = calcLastAdress(broadcastAddress);

            
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            
            System.out.println("Subnet mask: " + getDottedDecSubMask(subnetMask));
            System.out.println("Network adress: " + networkAddress.getHostAddress());
            System.out.println("First adress: " + firstAddress.getHostAddress());
            System.out.println("Broadcast adress: " + broadcastAddress.getHostAddress());
            System.out.println("Last adress: " + lastAddress.getHostAddress());
            System.out.println("Host quantity: " + hostQuantity);
            
            scanner.close();

        }

        
      
        

    }
   
}
