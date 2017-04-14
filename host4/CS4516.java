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

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

import net.floodlightcontroller.core.*;
import net.floodlightcontroller.packet.*;
import org.projectfloodlight.openflow.protocol.*;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActions;
import org.projectfloodlight.openflow.protocol.actionid.OFActionIdPushMpls;
import org.projectfloodlight.openflow.protocol.instruction.OFInstruction;
import org.projectfloodlight.openflow.protocol.instruction.OFInstructions;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.*;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;



import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

import net.floodlightcontroller.packet.Ethernet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CS4516 implements IOFMessageListener, IFloodlightModule {

    protected final MacAddress SWITCH_MAC = MacAddress.of("52:54:00:45:16:1A");
    protected IFloodlightProviderService floodlightProvider;
    protected static Logger logger;



    OFFactory myFactory = null;
    OFActions myActions = null;
    OFInstructions myInstructions = null;
	int robin = 0; //round robin bintch

    boolean hasrec = false;



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

	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
//	System.out.printf("Got dat BIZZIE \n\n\n\n\n\n\n BIZZZIE \n\nb\n\n\n\n\n\n");
        logger = LoggerFactory.getLogger(CS4516.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
    }

	//Hi mom its me

    public boolean ruleinit(IOFSwitch sw, FloodlightContext cntx){
        myFactory = sw.getOFFactory();
        if(myFactory == null) return false;
        myActions = myFactory.actions();
        myInstructions = myFactory.instructions();
        Match myMatch = myFactory.buildMatch()

                //.setExact(MatchField.IN_PORT, OFPort.of(1))
            //.setExact(MatchField.ETH_TYPE, EthType.IPv4)
    //.setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("192.168.0.1/24"))
    //.setExact(MatchField.IP_PROTO, IpProtocol.TCP)
    //.setExact(MatchField.TCP_DST, TransportPort.of(80))
    .build();





        ArrayList<OFAction> list1 = new ArrayList<OFAction>();
        list1.add(myActions.buildOutput().setPort(OFPort.NORMAL).build());

        OFFlowAdd flow1 = myFactory.buildFlowAdd()
                .setMatch(myMatch)
                .setActions(list1)
//		.setPriority(1)
                .build();

        sw.write(flow1);

                Match myMatch2 = myFactory.buildMatch()
            .setExact(MatchField.ETH_TYPE, EthType.IPv4)
//    .setExact(MatchField.IP_PROTO, IpProtocol.UDP)
//    .setExact(MatchField.UDP_SRC, TransportPort.of(53))
    .setExact(MatchField.IPV4_SRC, IPv4Address.of("10.45.7.2"))
    .build();
        ArrayList<OFAction> list2 = new ArrayList<OFAction>();
        list2.add(myActions.buildOutput().setPort(OFPort.CONTROLLER).build());
        OFFlowAdd flow2 = myFactory.buildFlowAdd()
                .setMatch(myMatch2)
                .setActions(list2)
                .build();
        sw.write(flow2);

	Match myMatch3 = myFactory.buildMatch()
            .setExact(MatchField.ETH_TYPE, EthType.IPv4)
   .setMasked(MatchField.IPV4_DST, IPv4AddressWithMask.of("10.45.7.128/25"))
    .build();
        ArrayList<OFAction> list3 = new ArrayList<OFAction>();
//        list2.add(myActions.setDlSrc(MacAddress.of("DE:AD:BE:EF:CA:FE")
//        list2.add(myActions.buildOutput().setPort(OFPort.NORMAL).build());
        OFFlowAdd flow3 = myFactory.buildFlowAdd()
                .setMatch(myMatch3)
                .setActions(list3)
//		.setPriority(2)
                .build();
        sw.write(flow3);


        return true;
    }

    public int indexOf(byte[] in, byte[] pattern){
        int i =0, j=0;
        for(i = 0; i < in.length && j < pattern.length; i++)
            for(j = 0; j < pattern.length && in[i+j] == pattern[j]; j++);
        return j == pattern.length ? i : -1;
    }

    public byte[] IPtoByte(String str) {

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(str);
        } catch (Exception e) {
            e.printStackTrace();

        }
        byte[] bytes = ip.getAddress();
        for (byte b : bytes) {
            System.out.println(Integer.toHexString(b));
        }
        return bytes;


    }
    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

//	System.out.printf("Got dat shit\n");
        if(msg.getType() == OFType.PACKET_IN) {

            if(!hasrec)
                hasrec = ruleinit(sw, cntx);
            Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

            MacAddress srcMac = eth.getSourceMACAddress();
            MacAddress dstMac = eth.getDestinationMACAddress();

            if(eth.getEtherType() != EthType.IPv4){
                return Command.CONTINUE;
            }

            IPv4 ipv4 = (IPv4) eth.getPayload();
            IPv4Address srcIP = ipv4.getSourceAddress();
            IPv4Address dstIP = ipv4.getDestinationAddress();

            System.out.println("Packet type: " +ipv4.getPayload().toString());
            if(!ipv4.getProtocol().equals(IpProtocol.UDP)) return Command.CONTINUE;
            System.out.printf("We got a dns packet?\n");
            System.out.println("Source IP: " +  srcIP.toString() + "Source mac: " + srcMac);
            System.out.println("Dest IP: " +  dstIP.toString() + "Dest mac: " + dstMac);

            UDP p = (UDP) ipv4.getPayload();
            System.out.println("Source port: " + p.getSourcePort());
            System.out.println("Dest port: " + p.getDestinationPort());


            //assume we are at DNS now

            byte[] data = p.serialize();


            System.out.println("Address "+srcIP +" was at " + indexOf(data, srcIP.getBytes()));


            robin++;
            if(robin > 254 || robin < 128) robin =128;
            IPv4Address newsc = dstIP;
            IPv4Address newds = IPv4Address.of("10.45.7." + robin);

            byte[] newIP = IPtoByte("10.45.7.3");
            //assume we can parse it
            if(indexOf(data, newIP) != -1 ){

            }
            //assume we can edit it

            //add new flow to table




            Match myMatch = myFactory.buildMatch()
                    .setExact(MatchField.ETH_TYPE, EthType.IPv4)
                    //.setMasked(MatchField.IPV4_SRC, IPv4AddressWithMask.of("192.168.0.1/24"))
//    .setExact(MatchField.IP_PROTO, IpProtocol.UDP)
//    .setExact(MatchField.UDP_SRC, TransportPort.of(53))
                    .setExact(MatchField.IPV4_SRC, newsc)
                    //Send

                    .setExact(MatchField.IPV4_DST, newds)
                    .build();
            ArrayList<OFAction> list = new ArrayList<OFAction>();
//        list.add(myActions.setDlSrc(MacAddress.of("DE:AD:BE:EF:CA:FE")
            list.add(myActions.buildOutput().setPort(OFPort.NORMAL).build());
            OFFlowAdd flow = myFactory.buildFlowAdd()
                    .setMatch(myMatch)
                    .setActions(list)
                    .setHardTimeout(10000) //todo less or something idk
//		.setPriority(2)
                    .build();
            sw.write(flow);




/*
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
            		OFPacketOut po = sw.getOFFactory().buildPacketOut() //mySwitch is some IOFSwitch object
                    		.setData(serializedData)
                    		.setActions(Collections.singletonList((OFAction) sw.getOFFactory().actions().output(OFPort.NORMAL, 1)))
                    		.setInPort(OFPort.CONTROLLER)
                    		.build();

            		sw.write(po);

            }

*/
        }


        return Command.CONTINUE;
    }

}
