/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.segmentation;

import java.util.ArrayList;
import ur_os.system.SystemOS;
import java.util.Random;
import ur_os.memory.MemoryAddress;
import ur_os.memory.freememorymagament.MemorySlot;

/**
 *
 * @author super
 */
public class SegmentTable {
    
    ArrayList<SegmentTableEntry> segmentTable;
    public static final int SAMPLE_PROGRAM_SIZE = 100;
    public static final int SAMPLE_SEGMENT_NUMBER = 5;
    int programSize; //Size of the program in bytes
    int segmentNumber; //Size of the program in bytes
    Random r;
    
    public SegmentTable(){
        this(SAMPLE_PROGRAM_SIZE, SAMPLE_SEGMENT_NUMBER);
    }
    
    public SegmentTable(int programSize){
        this(programSize, SAMPLE_SEGMENT_NUMBER);
    }
       
    public SegmentTable(int programSize, int segmentNumber){
        this(programSize, segmentNumber, true);
    }
    
    public SegmentTable(int programSize, int segmentNumber, boolean auto){
        this.programSize = programSize;
        this.segmentNumber = segmentNumber;
        segmentTable = new ArrayList(segmentNumber); 
        //r = new Random(SystemOS.SEED_SEGMENTS);
        r = new Random();
        if(auto)
            createSegments();
    }
    
    public void createSegments(){
        int[] vals = new int[segmentNumber];
        float total = 0;
        float total2 = 0;
        int base = 0;
        //Generate random numbers from 1 to 99
        for (int i = 0; i < segmentNumber; i++) {
            do{
                vals[i] = r.nextInt(100);
            }while(vals[i] == 0);
            total += vals[i];
        }
        
        for (int i = 0; i < segmentNumber; i++) {
            //The segment size is the percentage of the random value agains the total sum times de program size
            vals[i] = java.lang.Math.round((vals[i]/total)*this.programSize);
            total2 += vals[i];
        }
        
        //Any difference produced by the rounding will be addedd to the final segment
        vals[segmentNumber-1] += this.programSize - total2;
        
        //All bases are 0 because they will be set when the memory slot is assigned to the segment
        for (int i = 0; i < segmentNumber; i++) {
            this.addSegment(base, vals[i]);
        }
        
    }
    
    public SegmentTable(SegmentTable pt){
        this(pt.getProgramSize(), pt.getSize());
        segmentTable = new ArrayList(pt.getTable());
    }
    
    public ArrayList<SegmentTableEntry> getTable(){
        return segmentTable;
    }
    
    public MemoryAddress getSegmentMemoryAddressFromLocalAddress(int locAdd, boolean store){
        int segment = -1;
        int offset = -1;
        int baseLogicaAcumulada = 0;

        for (int i = 0; i < segmentTable.size(); i++) {
            SegmentTableEntry entry = segmentTable.get(i);
            int limit = entry.getLimit();
            
            // 1. Comprueba si locAdd cae en el rango lógico [baseLogicaAcumulada, baseLogicaAcumulada + limit - 1]
            if (locAdd >= baseLogicaAcumulada && locAdd < baseLogicaAcumulada + limit) {
                segment = i;
                offset = locAdd - baseLogicaAcumulada; // 2. Calcula el desplazamiento
                
                // 3. Validación de límites 
                if (offset < 0 || offset >= limit) {
                    System.out.println("Error: Offset fuera de los límites del segmento (Fallo de Segmentación).");
                    return new MemoryAddress(-1, -1);
                }
                
                // 4. Marcar como 'dirty' si es una operación de escritura (STORE)
                if(store){
                    entry.markDirty();
                }
                      
                System.out.println("Accessing Segment "+segment+" and offset "+offset);
                return new MemoryAddress(segment, offset); // Retorna inmediatamente en caso de éxito
            }
            
            baseLogicaAcumulada += limit; // Avanza al inicio lógico del siguiente segmento
        }
        
        // 5. Si el bucle termina, la dirección es ilegal (Segmentation Fault)
        System.out.println("Error: Dirección Lógica (" + locAdd + ") fuera de los límites del programa. Segmentation Fault.");
        
        //Para Memoria Virtual (
        if(store){
            if(segment != -1) // Protección, aunque no debería ejecutarse si se retorna arriba
                this.segmentTable.get(segment).setDirty();
        }
            
        System.out.println("Accessing Segment "+segment+" and offset "+offset);
        return new MemoryAddress(-1, -1); // Retorno de error para el caso de no encontrar el segmento
    }
    
    public MemoryAddress getPhysicalMemoryAddressFromLogicalMemoryAddress(MemoryAddress m){
        
        // 1. Obtiene el ID del segmento y el offset
        int segmentID = m.getDivision(); 
        int offset = m.getOffset(); 
        int physicalAddress = -1; // Inicializa la dirección física
    
        // 2. Comprobación de validez del ID del segmento
        if (segmentID < 0 || segmentID >= segmentTable.size()) {
            System.out.println("Error: Número de segmento inválido (" + segmentID + ") en el acceso físico.");
            return new MemoryAddress(-1, -1);
        }
        
        SegmentTableEntry entry = segmentTable.get(segmentID);
        MemorySlot slot = entry.getMemorySlot();
        
        // 3. Check si el segmento está cargado en RAM (Manejo de Memoria Virtual)
        if (slot == null) {
            System.out.println("Error: Segmento " + segmentID + " no está cargado en Memoria Física. Segment Fault.");
            return new MemoryAddress(-1, -1);
        }
        
        int physicalBase = slot.getBase(); // Dirección base física
        int limit = slot.getSize(); // Tamaño (límite) del segmento
        
        // 4. Comprobación de límites del offset
        if (offset < 0 || offset >= limit) {
             System.out.println("Error: Offset (" + offset + ") fuera de los límites del segmento (" + limit + ") para acceso físico.");
             return new MemoryAddress(-1, -1);
        }

        // 5. Cálculo de la dirección física: Base Física + Offset
        physicalAddress = physicalBase + offset;

        // 6. Retorna la dirección física calculada
        return new MemoryAddress(physicalAddress, offset); 
    }
    
    public SegmentTableEntry getSegment(int i){
        return segmentTable.get(i);
    }
    
    public void addSegment(int base, int limit){
        segmentTable.add(new SegmentTableEntry(base, limit));
    }
    
    public void setFrameID(int segment, int base, int limit){
        if(segment == segmentTable.size()){
            segmentTable.add(new SegmentTableEntry(base, limit)); //If it is a new segment
        }else if(segment < segmentTable.size()){
            segmentTable.get(segment).setSegment(base, limit); //Update base and limit for an existing segment
        }else{
            System.out.println("Error - Including erroneous segment number");
        }
        
    }

    public int getSize() {
        return segmentNumber;
    }

    public int getProgramSize() {
        return programSize;
    }
    
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (SegmentTableEntry segmentTableEntry : segmentTable) {
            sb.append("Segment: ");
            sb.append(count++);
            sb.append(" ");
            sb.append(segmentTableEntry.toString());
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
