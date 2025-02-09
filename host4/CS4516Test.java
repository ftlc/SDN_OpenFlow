package net.floodlightcontroller.CS4516;

/**
 * Created by ftlc on 4/15/17.
 */
/**
 *    Copyright 2011, Big Switch Networks, Inc.
 *    Originally created by David Erickson, Stanford University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

import net.floodlightcontroller.cs4516.CS4516;
import net.floodlightcontroller.test.FloodlightTestCase;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.SwitchDescription;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.test.MockThreadPoolService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.debugcounter.IDebugCounterService;
import net.floodlightcontroller.debugcounter.MockDebugCounterService;
import net.floodlightcontroller.devicemanager.internal.DefaultEntityClassifier;
import net.floodlightcontroller.devicemanager.test.MockDeviceManager;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.IEntityClassifierService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.internal.LinkDiscoveryManager;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.IPv6;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.routing.IRoutingDecision.RoutingAction;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Path;
import net.floodlightcontroller.routing.RoutingDecision;
import net.floodlightcontroller.test.FloodlightTestCase;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.OFMessageUtils;
import net.floodlightcontroller.forwarding.Forwarding;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.junit.Test;
import org.projectfloodlight.openflow.protocol.OFFeaturesReply;
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.protocol.OFDescStatsReply;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.Masked;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.U64;
import org.projectfloodlight.openflow.types.VlanVid;
import org.projectfloodlight.openflow.protocol.OFPacketInReason;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.sdnplatform.sync.ISyncService;
import org.sdnplatform.sync.test.MockSyncService;

import com.google.common.collect.ImmutableList;

public class CS4516Test extends FloodlightTestCase {

    protected FloodlightContext cntx;
    protected MockDeviceManager deviceManager;
    protected IRoutingService routingEngine;
    protected Forwarding forwarding;
    protected ITopologyService topology;
    protected LinkDiscoveryManager linkService;
    protected MockThreadPoolService threadPool;
    protected IOFSwitch sw1, sw2;
    protected OFFeaturesReply swFeatures;
    protected OFDescStatsReply swDescription;
    protected IDevice srcDevice, dstDevice1, dstDevice2; /* reuse for IPv4 and IPv6 */
    protected OFPacketIn packetIn;
    protected OFPacketIn packetInIPv6;
    protected OFPacketOut packetOut;
    protected OFPacketOut packetOutIPv6;
    protected OFPacketOut packetOutFlooded;
    protected OFPacketOut packetOutFloodedIPv6;
    protected IPacket testPacket;
    protected IPacket testPacketIPv6;
    protected byte[] testPacketSerialized;
    protected byte[] testPacketSerializedIPv6;
    protected int expected_wildcards;
    protected Date currentDate;
    private MockSyncService mockSyncService;
    private OFFactory factory = OFFactories.getFactory(OFVersion.OF_13);
    private CS4516 cs4516;


    @Override
    public void setUp() throws Exception {
        super.setUp();

        cntx = new FloodlightContext();

        // Module loader setup
        mockFloodlightProvider = getMockFloodlightProvider();
        forwarding = new Forwarding();
        threadPool = new MockThreadPoolService();
        deviceManager = new MockDeviceManager();
        routingEngine = createMock(IRoutingService.class);
        topology = createMock(ITopologyService.class);
        mockSyncService = new MockSyncService();
        linkService = new LinkDiscoveryManager();
        DefaultEntityClassifier entityClassifier = new DefaultEntityClassifier();

        FloodlightModuleContext fmc = new FloodlightModuleContext();
        fmc.addService(IFloodlightProviderService.class,
                mockFloodlightProvider);
        fmc.addService(IThreadPoolService.class, threadPool);
        fmc.addService(ITopologyService.class, topology);
        fmc.addService(IRoutingService.class, routingEngine);
        fmc.addService(IDeviceService.class, deviceManager);
        fmc.addService(IEntityClassifierService.class, entityClassifier);
        fmc.addService(ISyncService.class, mockSyncService);
        fmc.addService(IDebugCounterService.class, new MockDebugCounterService());
        fmc.addService(IOFSwitchService.class, getMockSwitchService());
        fmc.addService(ILinkDiscoveryService.class, linkService);

        topology.addListener(anyObject(ITopologyListener.class));
        expectLastCall().anyTimes();
        expect(topology.isBroadcastAllowed(anyObject(DatapathId.class), anyObject(OFPort.class))).andReturn(true).anyTimes();
        replay(topology);

        threadPool.init(fmc);
        mockSyncService.init(fmc);
        linkService.init(fmc);
        deviceManager.init(fmc);
        forwarding.init(fmc);
        entityClassifier.init(fmc);
        threadPool.startUp(fmc);
        mockSyncService.startUp(fmc);
        linkService.startUp(fmc);
        deviceManager.startUp(fmc);
        forwarding.startUp(fmc);
        entityClassifier.startUp(fmc);
        verify(topology);

        swDescription = factory.buildDescStatsReply().build();
        swFeatures = factory.buildFeaturesReply().setNBuffers(1000).build();
        // Mock switches
        sw1 = EasyMock.createMock(IOFSwitch.class);
        expect(sw1.getId()).andReturn(DatapathId.of(1L)).anyTimes();
        expect(sw1.getOFFactory()).andReturn(factory).anyTimes();
        expect(sw1.getBuffers()).andReturn(swFeatures.getNBuffers()).anyTimes();


        sw2 = EasyMock.createMock(IOFSwitch.class);
        expect(sw2.getId()).andReturn(DatapathId.of(2L)).anyTimes();
        expect(sw2.getOFFactory()).andReturn(factory).anyTimes();
        expect(sw2.getBuffers()).andReturn(swFeatures.getNBuffers()).anyTimes();

        expect(sw1.hasAttribute(IOFSwitch.PROP_SUPPORTS_OFPP_TABLE)).andReturn(true).anyTimes();

        expect(sw2.hasAttribute(IOFSwitch.PROP_SUPPORTS_OFPP_TABLE)).andReturn(true).anyTimes();

        expect(sw1.getSwitchDescription()).andReturn(new SwitchDescription(swDescription)).anyTimes();
        expect(sw2.getSwitchDescription()).andReturn(new SwitchDescription(swDescription)).anyTimes();

        expect(sw1.isActive()).andReturn(true).anyTimes();
        expect(sw2.isActive()).andReturn(true).anyTimes();

        // Load the switch map
        Map<DatapathId, IOFSwitch> switches = new HashMap<DatapathId, IOFSwitch>();
        switches.put(DatapathId.of(1L), sw1);
        switches.put(DatapathId.of(2L), sw2);
        getMockSwitchService().setSwitches(switches);

        // Build test packet
        testPacket = new Ethernet()
                .setDestinationMACAddress("00:11:22:33:44:55")
                .setSourceMACAddress("00:44:33:22:11:00")
                .setEtherType(EthType.IPv4)
                .setPayload(
                        new IPv4()
                                .setTtl((byte) 128)
                                .setSourceAddress("192.168.1.1")
                                .setDestinationAddress("192.168.1.2")
                                .setPayload(new UDP()
                                        .setSourcePort((short) 5000)
                                        .setDestinationPort((short) 5001)
                                        .setPayload(new Data(new byte[] {0x01}))));

        testPacketIPv6 = new Ethernet()
                .setDestinationMACAddress("00:11:22:33:44:55")
                .setSourceMACAddress("00:44:33:22:11:00")
                .setEtherType(EthType.IPv6)
                .setPayload(
                        new IPv6()
                                .setHopLimit((byte) 128)
                                .setSourceAddress(IPv6Address.of(1, 1))
                                .setDestinationAddress(IPv6Address.of(2, 2))
                                .setNextHeader(IpProtocol.UDP)
                                .setPayload(new UDP()
                                        .setSourcePort((short) 5000)
                                        .setDestinationPort((short) 5001)
                                        .setPayload(new Data(new byte[] {0x01}))));

        currentDate = new Date();

        // Mock Packet-in
        testPacketSerialized = testPacket.serialize();
        testPacketSerializedIPv6 = testPacketIPv6.serialize();

        packetIn = factory.buildPacketIn()
                .setMatch(factory.buildMatch()
                        .setExact(MatchField.IN_PORT, OFPort.of(1))
                        .setExact(MatchField.ETH_SRC, MacAddress.of("00:44:33:22:11:00"))
                        .setExact(MatchField.ETH_DST, MacAddress.of("00:11:22:33:44:55"))
                        .setExact(MatchField.ETH_TYPE, EthType.IPv4)
                        .setExact(MatchField.IPV4_SRC, IPv4Address.of("192.168.1.1"))
                        .setExact(MatchField.IPV4_DST, IPv4Address.of("192.168.1.2"))
                        .setExact(MatchField.IP_PROTO, IpProtocol.UDP)
                        .setExact(MatchField.UDP_SRC, TransportPort.of(5000))
                        .setExact(MatchField.UDP_DST, TransportPort.of(5001))
                        .build())
                .setBufferId(OFBufferId.NO_BUFFER)
                .setData(testPacketSerialized)
                .setReason(OFPacketInReason.NO_MATCH)
                .build();
        packetInIPv6 = factory.buildPacketIn()
                .setMatch(factory.buildMatch()
                        .setExact(MatchField.IN_PORT, OFPort.of(1))
                        .setExact(MatchField.ETH_SRC, MacAddress.of("00:44:33:22:11:00"))
                        .setExact(MatchField.ETH_DST, MacAddress.of("00:11:22:33:44:55"))
                        .setExact(MatchField.ETH_TYPE, EthType.IPv6)
                        .setExact(MatchField.IPV6_SRC, IPv6Address.of(1, 1))
                        .setExact(MatchField.IPV6_DST, IPv6Address.of(2, 2))
                        .setExact(MatchField.IP_PROTO, IpProtocol.UDP)
                        .setExact(MatchField.UDP_SRC, TransportPort.of(5000))
                        .setExact(MatchField.UDP_DST, TransportPort.of(5001))
                        .build())
                .setBufferId(OFBufferId.NO_BUFFER)
                .setData(testPacketSerializedIPv6)
                .setReason(OFPacketInReason.NO_MATCH)
                .build();

        // Mock Packet-out
        List<OFAction> poactions = new ArrayList<OFAction>();
        poactions.add(factory.actions().output(OFPort.of(3), Integer.MAX_VALUE));
        packetOut = factory.buildPacketOut()
                .setBufferId(this.packetIn.getBufferId())
                .setActions(poactions)
                .setInPort(OFPort.of(1))
                .setData(testPacketSerialized)
                .setXid(15)
                .build();
        packetOutIPv6 = factory.buildPacketOut()
                .setBufferId(this.packetInIPv6.getBufferId())
                .setActions(poactions)
                .setInPort(OFPort.of(1))
                .setData(testPacketSerializedIPv6)
                .setXid(15)
                .build();

        // Mock Packet-out with OFPP_FLOOD action (list of ports to flood)
        poactions = new ArrayList<OFAction>();
        poactions.add(factory.actions().output(OFPort.of(10), Integer.MAX_VALUE));
        packetOutFlooded = factory.buildPacketOut()
                .setBufferId(this.packetIn.getBufferId())
                .setInPort(packetIn.getMatch().get(MatchField.IN_PORT))
                .setXid(17)
                .setActions(poactions)
                .setData(testPacketSerialized)
                .build();
        packetOutFloodedIPv6 = factory.buildPacketOut()
                .setBufferId(this.packetInIPv6.getBufferId())
                .setInPort(packetInIPv6.getMatch().get(MatchField.IN_PORT))
                .setXid(17)
                .setActions(poactions)
                .setData(testPacketSerializedIPv6)
                .build();

        cs4516 = new CS4516();
    }

    void removeDeviceFromContext() {
        IFloodlightProviderService.bcStore.
                remove(cntx,
                        IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        IFloodlightProviderService.bcStore.
                remove(cntx,
                        IDeviceService.CONTEXT_SRC_DEVICE);
        IFloodlightProviderService.bcStore.
                remove(cntx,
                        IDeviceService.CONTEXT_DST_DEVICE);
    }




    @Test
    public void sshTest(){
        System.out.println(sw2.getInetAddress().toString());
        System.out.println(sw1.getInetAddress().toString());
    }
    @Test
    public void indexOfTest() {
        //byte[] pattern = []
        byte[] pattern = cs4516.IPtoByte("10.45.7.1");
        byte[] toSearch = {(byte) 12, (byte) 10, (byte) 45, (byte) 7, (byte) 1, (byte) 12};

        int index = cs4516.indexOf(toSearch, pattern);
        assertEquals(1, index);
    }

    @Test
    public void IPtoByteTest() {
        String IP = "127.0.0.1";
        int intIP = 2130706433; //Integer representation of localhsot
        byte[] byteIP = cs4516.IPtoByte(IP);

        String strToPrint = "";
        for (byte b : byteIP) {
            strToPrint = strToPrint + "." + Integer.toString(b);
        }
        strToPrint = strToPrint.replaceFirst(Pattern.quote("."), "");

        assertEquals(IP, strToPrint);

    }



}
