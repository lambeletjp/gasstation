/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gasstation;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.assessment.gasstation.GasPump;
import net.assessment.gasstation.GasType;
import net.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.assessment.gasstation.exceptions.NotEnoughGasException;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author JP
 */
public class newGasStationTest {
    
    public newGasStationTest() {
    }

    /**
     * Test of addGasPump method, of class newGasStation.
     */
    @org.junit.Test
    public void testAddAndGetGasPump() {
        System.out.println("addGasPump");
        GasPump pump = new GasPump(GasType.DIESEL,10);
        newGasStation instance = new newGasStation();
        instance.addGasPump(pump);
        List listGasPump = (List) instance.getGasPumps();
        if(listGasPump.size() != 1){
            fail("Failed to add a pump");
        }
        if(listGasPump.get(0) != pump){
            fail("Failed to get the same pump");
        }
    }

    /**
     * Test of buyGas method, of class newGasStation.
     */
    @org.junit.Test
    public void testBuyGas() throws Exception {
        System.out.println("buyGas");
        GasType type = GasType.DIESEL;
        double amountInLiters = 10.0;
        double maxPricePerLiter = 10.0;        
        newGasStation gasStation = new newGasStation();
        gasStation.setPrice(GasType.DIESEL, 10);
        GasPump pump = new GasPump(GasType.DIESEL,10);
        gasStation.addGasPump(pump);
        double expResult = 100.0;
        double result = gasStation.buyGas(type, amountInLiters, maxPricePerLiter);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getRevenue method, of class newGasStation.
     */
    @org.junit.Test
    public void testGetRevenue() {
        System.out.println("testGetRevenue");
        GasType type = GasType.DIESEL;
        double amountInLiters = 10.0;
        double maxPricePerLiter = 10.0;        
        newGasStation gasStation = new newGasStation();
        gasStation.setPrice(GasType.DIESEL, 10);
        GasPump pump = new GasPump(GasType.DIESEL,10);
        gasStation.addGasPump(pump);
        double expResult = 100.0;
        try {
            gasStation.buyGas(type, amountInLiters, maxPricePerLiter);
        } catch (NotEnoughGasException ex) {
            Logger.getLogger(newGasStationTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GasTooExpensiveException ex) {
            Logger.getLogger(newGasStationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double result = gasStation.getRevenue();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getNumberOfSales method, of class newGasStation.
     */
    @org.junit.Test
    public void testGetNumberOfSales() {
        System.out.println("testGetNumberOfSales");
        GasType type = GasType.DIESEL;
        double amountInLiters = 10.0;
        double maxPricePerLiter = 10.0;        
        newGasStation gasStation = new newGasStation();
        gasStation.setPrice(GasType.DIESEL, 10);
        GasPump pump = new GasPump(GasType.DIESEL,10);
        gasStation.addGasPump(pump);
        int expResult = 1;
        try {
            gasStation.buyGas(type, amountInLiters, maxPricePerLiter);
        } catch (NotEnoughGasException ex) {
            Logger.getLogger(newGasStationTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GasTooExpensiveException ex) {
            Logger.getLogger(newGasStationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double result = gasStation.getNumberOfSales();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getNumberOfCancellationsNoGas method, of class newGasStation.
     */
    @org.junit.Test
    public void testGetNumberOfCancellationsNoGas() {
        System.out.println("testGetNumberOfCancellationsNoGas");
        GasType type = GasType.DIESEL;
        double amountInLiters = 10.0;
        double maxPricePerLiter = 10.0;        
        newGasStation gasStation = new newGasStation();
        gasStation.setPrice(GasType.DIESEL, 10);
        GasPump pump = new GasPump(GasType.DIESEL,1);
        gasStation.addGasPump(pump);
        int expResult = 1;
        try {
            gasStation.buyGas(type, amountInLiters, maxPricePerLiter);
            System.out.println("+++++++++++++++++++++++++++++++");
        } catch (NotEnoughGasException ex) {
            //success
        } catch (GasTooExpensiveException ex) {
            Logger.getLogger(newGasStationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            double result = gasStation.getNumberOfCancellationsNoGas();
            assertEquals(expResult, result, 0.0);  
        }
        
    }

    /**
     * Test of getNumberOfCancellationsTooExpensive method, of class newGasStation.
     */
    @org.junit.Test
    public void testGetNumberOfCancellationsTooExpensive() {
        System.out.println("testGetNumberOfCancellationsTooExpensive");
        GasType type = GasType.DIESEL;
        double amountInLiters = 10.0;
        double maxPricePerLiter = 1.0;        
        newGasStation gasStation = new newGasStation();
        gasStation.setPrice(GasType.DIESEL, 10);
        GasPump pump = new GasPump(GasType.DIESEL,10);
        gasStation.addGasPump(pump);
        try {
            gasStation.buyGas(type, amountInLiters, maxPricePerLiter);
        } catch (NotEnoughGasException ex) {
            fail("The GasTooExpensiveException was not thrown");
        } catch (GasTooExpensiveException ex) {
            return;
        } 
        fail("The GasTooExpensiveException was not thrown");
        
    }

    /**
     * Test of getPrice method, of class newGasStation.
     */
    @org.junit.Test
    public void testSetGetPrice() {
        System.out.println("getPrice");
        GasType type = GasType.DIESEL;
        newGasStation gasStation = new newGasStation();
        gasStation.setPrice(type, 10);
        double expResult = 10.0;
        double result = gasStation.getPrice(type);
        assertEquals(expResult, result, 0.0);
    }
}