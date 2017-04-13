/* GAME PLAN
	On init.
	Clear current rules
	add ***** * ** * * * * rule to just DEFAULT
	add destip 10.45.7.128+ rule to DROP DAT SHIT
	add srcip 10.45.7.2, UDP, port 53 to FORWARD TO FLOODLIGHT
	Parse it in floodlight
	IF srcip already has a capability, just increace the timeout and return the existing IP
	If we need to add a capability, we add srcip SOMETHING destip 10.45.7.SOMETHING to DEFAULT
	Add a java timer to timeout that rule

*/



package net.floodlightcontroller.cs4516;

/**
 * Created by ftlc on 4/2/17.
 */

import java.util.*;

import net.floodlightcontroller.core.*;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.*;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;



import java.util.concurrent.ConcurrentSkipListSet;

import net.floodlightcontroller.packet.Ethernet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CS4516 implements IOFMessageListener, IFloodlightModule {

    protected final MacAddress SWITCH_MAC = MacAddress.of("52:54:00:45:16:1A");
    protected IFloodlightProviderService floodlightProvider;
    protected static Logger logger;
    protected HashMap<IPv4Address, MacAddress> ipTable;
    protected static HashMap<String , Long> validConnections;

    @Override
    public String getName() {
        return CS4516.class.getSimpleName();
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context)
            throws FloodlightModuleException {
        validConnections = new HashMap<>();
	validConnections.put("10.45.7.1:10.45.7.128" , System.currentTimeMillis() + 60000);
        ipTable = new HashMap<>();
        ipTable.put(IPv4Address.of("10.45.7.1"), MacAddress.of("52:54:00:45:16:19"));
        ipTable.put(IPv4Address.of("10.45.7.2"), MacAddress.of("52:54:00:45:16:1A"));
        ipTable.put(IPv4Address.of("10.45.7.3"), MacAddress.of("52:54:00:45:16:1B"));
        ipTable.put(IPv4Address.of("10.45.7.4"), MacAddress.of("52:54:00:45:16:1C"));

        //Host 2 Aliases
	for(int i = 128 ; i < 255 ; i++) {
		ipTable.put(IPv4Address.of("10.45.7." + i) , MacAddress.of("52:54:00:45:16:1B"));
	}
        ipTable.put(IPv4Address.of("10.45.7.34"), MacAddress.of("52:54:00:45:16:1A"));
        ipTable.put(IPv4Address.of("10.45.7.65"), MacAddress.of("52:54:00:45:16:1A"));
        ipTable.put(IPv4Address.of("10.45.7.97"), MacAddress.of("52:54:00:45:16:1A"));
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);


	OFFactory myFactory = floodlightProvider.getOFFactory();
	Match myMatch = myFactory.buildMatch()
    .setExact(MatchField.IN_PORT, OFPort.of(1))
    .setExact(MatchField.ETH_TYPE, EthType.IPv4)
    .setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("192.168.0.1/24"))
    .setExact(MatchField.IP_PROTO, IpProtocol.TCP)
    .setExact(MatchField.TCP_DST, TransportPort.of(80))
    .build();



        logger = LoggerFactory.getLogger(CS4516.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
    }

	//Hi mom its me

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

        if(msg.getType() == OFType.PACKET_IN) {
            Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

            MacAddress srcMac = eth.getSourceMACAddress();
            MacAddress dstMac = eth.getDestinationMACAddress();

            if(eth.getEtherType() != EthType.IPv4){
                return Command.CONTINUE;
            }

            IPv4 ipv4 = (IPv4) eth.getPayload();
            IPv4Address srcIP = ipv4.getSourceAddress();
            IPv4Address dstIP = ipv4.getDestinationAddress();

            System.out.println("Source IP: " +  srcIP.toString());
            System.out.println("Dest IP: " +  dstIP.toString());

            String key = srcIP.toString() + ":" + dstIP.toString();
            if ((dstIP.getInt() & 255) > 127) {
		System.out.printf("got packet in range %s wlll\n", key);
                Long timeout = validConnections.get(key);
                if(timeout == null){
			System.out.printf("Dropped packet key %s, no rule\n", key);
			return null; //DROP TOP POP
		}
                else if( System.currentTimeMillis() > timeout) {
			System.out.printf("Dropped packet key %s, timeout fucked\n", key);
                    validConnections.remove(key);
                    return null; //DROP THE PACKET
                }


            		System.out.println("Source MAC: " +  srcMac.toString());
            		System.out.println("Dest MAC: " +  dstMac.toString());
            		dstMac = ipTable.get(dstIP);
            		srcMac = SWITCH_MAC;

            		//System.out.println("TCP Payload: " + tcp.toString());

            		System.out.println("New Source MAC: " +  srcMac.toString());
            		if(!dstMac.equals(null)){
                		System.out.println("New Dest MAC: " +  dstMac.toString());
            		}

            		byte[] serializedData = eth.serialize();
            		OFPacketOut po = sw.getOFFactory().buildPacketOut() /* mySwitch is some IOFSwitch object */
                    		.setData(serializedData)
                    		.setActions(Collections.singletonList((OFAction) sw.getOFFactory().actions().output(OFPort.NORMAL, 1)))
                    		.setInPort(OFPort.CONTROLLER)
                    		.build();

            		sw.write(po);

            }


        }


        return Command.CONTINUE;
    }

}
