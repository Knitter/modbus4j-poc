package eu.sergiolopes;

import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;

import java.nio.ByteBuffer;

public class Main {

    public static void main(String[] args) {
        //Demo default: "192.168.1.74"
		if(args.length == 0 || "".equals(args[0])) {
			System.out.println("Missing HOST address");
			System.exit(1);
		}

        var transport = NettyTcpClientTransport.create(cfg -> {
            cfg.hostname = args[0];
            cfg.port = 502;
        });

        var client = ModbusTcpClient.create(transport);
        try {
            client.connect();
            ReadHoldingRegistersResponse response = null;

            int loopCount = 100;
			if(args.length == 2) {
			    try {
                    loopCount = Integer.parseInt(args[1]);
			    } catch (NumberFormatException e) {
				    System.err.println("Invalid loop count parameter, not a parsable Integer. Defaulting to 100 iterations.");
			   }
			}
			
            while (loopCount-- > 0) {
                response = client.readHoldingRegisters(1, new ReadHoldingRegistersRequest(0, 1));
                System.out.println("FIRE S1:" + ByteBuffer.wrap(response.registers()).getShort());

                response = client.readHoldingRegisters(1, new ReadHoldingRegistersRequest(1, 1));
                System.out.println("FIRE S2:" + ByteBuffer.wrap(response.registers()).getShort());

                response = client.readHoldingRegisters(1, new ReadHoldingRegistersRequest(2, 1));
                System.out.println("OVERLOAD:" + ByteBuffer.wrap(response.registers()).getShort());

                Thread.sleep(500);
            }
            client.disconnect();
        } catch (ModbusExecutionException e) {
            throw new RuntimeException(e);
        } catch (ModbusTimeoutException e) {
            throw new RuntimeException(e);
        } catch (ModbusResponseException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
