package com.csm.rover.simulator.wrapper;

import java.util.*;

public class SerialBuffers {

    private Map<String, Queue<Byte>> serialBuffers; // the buffer for messages

    private Globals global;
    private HumanInterfaceAbstraction hi;

    public SerialBuffers(ArrayList<String> IDs, HumanInterfaceAbstraction hi){
        serialBuffers = new HashMap<String, Queue<Byte>>();
        initializeLists(IDs);
        global = Globals.getInstance();
        this.hi = hi;
    }

    private void initializeLists(ArrayList<String> IDs){
        for (String id : IDs){
            serialBuffers.put(id, new LinkedList<Byte>());
        }
    }

    public void writeToSerial(byte write, String from){ // writes the character to the other 2 buffers
        for (String id : serialBuffers.keySet()){
            if (!id.equals(from)){
                if (serialBuffers.get(id).size() < 64){
                    serialBuffers.get(id).add(write);
                }
                else {
                    global.writeToLogFile(id, "Failed to receive: " + (char) write + ", full buffer.");
                }
            }
        }
        hi.updateSerialBuffers();
    }

    public void writeToSerial(char write, String from){
        writeToSerial((byte) write, from);
    }

    public int RFAvailable(String which){ // Returns the number of chars waiting
        for (String id : serialBuffers.keySet()){
            if (id.equals(which)){
                return serialBuffers.get(id).size();
            }
        }
        return -1;
    }

    public byte ReadSerial(String which){ // Returns the first waiting character
        byte out = '\0';
        for (String id : serialBuffers.keySet()){
            if (id.equals(which)){
                out = serialBuffers.get(id).poll();
                break;
            }
        }
        hi.updateSerialBuffers();
        return out;
    }

    public byte PeekSerial(String which){  // get first waiting character without changing availability
        byte out = '\0';
        for (String id : serialBuffers.keySet()){
            if (id.equals(which)){
                out = serialBuffers.get(id).peek();
                break;
            }
        }
        return out;
    }

    public int size(){
        return this.serialBuffers.size();
    }

    @SuppressWarnings("unused")
    private void printBuffers(){
        for (Map.Entry<String, Queue<Byte>> entry : serialBuffers.entrySet()){
            global.writeToLogFile("Timing", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    ArrayList<Queue<Byte>> getSerialQueues(){
        try {
            ArrayList<Queue<Byte>> out = new ArrayList<Queue<Byte>>(serialBuffers.size());
            for (Map.Entry<String, Queue<Byte>> entry : serialBuffers.entrySet()){
                Queue<Byte> q = new LinkedList<Byte>();
                for (Byte b : entry.getValue()){
                    q.add(b);
                }
                out.add(q);
            }
            return out;
        }
        catch (Exception e){
            global.reportError("Globals", "getSerialQueues", e);
            e.printStackTrace();
            return null;
        }
    }

}