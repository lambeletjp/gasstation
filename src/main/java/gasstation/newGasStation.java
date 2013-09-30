package gasstation;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import net.assessment.gasstation.GasPump;
import net.assessment.gasstation.GasStation;
import net.assessment.gasstation.GasType;
import net.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.assessment.gasstation.exceptions.NotEnoughGasException;

/**
 * Implementation of a GasStation. It's thread safe.
 * @author jp
 * @version 0.1
 * @date 2013-09-12
 */
public class newGasStation implements GasStation {

    private List<GasPump> arrayGasPumps = new ArrayList(); //the array where alle the gas pump will be saved
    private List<Integer> arrayFreeGasPump = new ArrayList(); //the array to set which pump is not used
    private double revenue = 0; //the revenue total of the gas station
    private int numberOfSales = 0; //the number of sales
    private int numberOfCancellationsNoGas = 0; //the number of cancellation if no Gas
    private int numberOfCancellationsTooExpensive = 0; //the number of cancellation if too expensive
    private Map hTablePriceOfGas = Collections.synchronizedMap(new HashMap()); //the price of the gaz we had
    private final int numberMaxWaitPump = 50; //how many time are we going to try to get a pump

    //default constructeur
    public newGasStation() {
        //set price of all type of gas of max int
        for (GasType gasType : GasType.values()) {
            this.setPrice(gasType, Double.MAX_VALUE);
        }
    }

    /**
     * Add a gas pump to this station.
     * This is used to set up this station.
     * 
     * @param pump
     *            the gas pump
     */
    public void addGasPump(GasPump pump) {
        synchronized (this.arrayGasPumps) {
            arrayGasPumps.add(pump);
            arrayFreeGasPump.add(1);
        }
    }

    /**
     * Get all gas pumps that are currently associated with this gas station.
     * 
     * Modifying the resulting collection should not affect this gas station.
     * 
     * @return A collection of all gas pumps.
     */
    public Collection<GasPump> getGasPumps() {
        return new ArrayList(this.arrayGasPumps);
    }

    /**
     * Simulates a customer wanting to buy a specific amount of gas.
     * 
     * @param type
     *            The type of gas the customer wants to buy
     * @param amountInLiters
     *            The amount of gas the customer wants to buy. Nothing less than this amount is acceptable!
     * @param maxPricePerLiter
     *            The maximum price the customer is willing to pay per liter
     * @return the price the customer has to pay for this transaction
     * @throws NotEnoughGasException
     *             Should be thrown in case not enough gas of this type can be provided
     *             by any single {@link GasPump}.
     * @throws GasTooExpensiveException
     *             Should be thrown if gas is not sold at the requested price (or any lower price)
     */
    public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter) throws NotEnoughGasException, GasTooExpensiveException {
        //is the price low enough?
        double price = this.getPrice(type) * amountInLiters;
        if (this.getPrice(type) > maxPricePerLiter) {
            this.addNumberOfCancellationsTooExpensive();
            throw new GasTooExpensiveException();
        }

        //find the right pump
        List arrayChecked = new ArrayList(); //list to add which pump has been checked
        int numberWaitPump = 0; //how many time we will wait to get a Pump
        GasPump gasPump = null; //will be the right pump
        int i = -1;
        while (this.arrayFreeGasPump.size() != arrayChecked.size()) {
            i++;

            //if we already checked this pump we don't want to do it again immediately, so we wait to check again
            if (Arrays.asList(arrayChecked).contains(i)) {
                //check if we already waited too many time
                numberWaitPump++;
                if (numberWaitPump > this.numberMaxWaitPump) {
                    break;
                }
                try {
                    Thread.sleep((long) (100));
                } catch (InterruptedException ex) {
                    //ignored
                }
                continue;
            }

            //is the gasPump free?
            synchronized (this.arrayFreeGasPump.get(i)) {
                if (this.arrayFreeGasPump.get(i) == 0) {
                    continue;
                }
            }
            synchronized (this.arrayGasPumps.get(i)) {
                gasPump = this.arrayGasPumps.get(i);
                arrayChecked.add(i);

                //has the gasPump the right gasType?
                if (gasPump.getGasType() != type) {
                    gasPump = null;
                    continue;
                }

                //is there enough gas?
                if (gasPump.getRemainingAmount() < amountInLiters) {
                    gasPump = null;
                    continue;
                }

                //we take this one!
                synchronized (this.arrayFreeGasPump) {
                    this.arrayFreeGasPump.set(i, 0);
                }
                break;
            }
        }

        /* 
         * If we didn't find a gasPump it's because:
         * 1) no pump with this type of gas
         * 2) there's no enough Gas
         * 3) we wait too long to get the gas
         */
        if (gasPump == null) {
            this.addNumberOfCancellationsNoGas();
            throw new NotEnoughGasException();
        }

        //pump the gas
        synchronized (gasPump) {
            this.addRevenue(price);
            this.addNumberOfSales();
            gasPump.pumpGas(amountInLiters);
        }

        //set the pump free again
        synchronized (this.arrayFreeGasPump) {
            this.arrayFreeGasPump.set(i, 1);
        }

        return price;
    }
    /**
     * Used internal to add a a Revenue
     */
    private synchronized void addRevenue(double revenue) {
        this.revenue += revenue;
    }
    /**
     * @return the total revenue generated
     */
    public double getRevenue() {
        return this.revenue;
    }

    /**
     * Used internal to add a a Sale
     */
    private synchronized void addNumberOfSales() {
        this.numberOfSales++;
    }

    /**
     * Returns the number of successful sales. This should not include cancelled sales.
     * 
     * @return the number of sales that were successful
     */
    public int getNumberOfSales() {
        return this.numberOfSales;
    }

    /**
     * Used internal to add a Cancellation because there were no gas anymore
     */
    private synchronized void addNumberOfCancellationsNoGas() {
        this.numberOfCancellationsNoGas++;
    }

    /**
     * @return the number of cancelled transactions due to not enough gas being available
     */
    public int getNumberOfCancellationsNoGas() {
        return this.numberOfCancellationsNoGas;
    }

    /**
     * Used internal to add a Cancellation because it was too expensive
     */
    private synchronized void addNumberOfCancellationsTooExpensive() {
        this.numberOfCancellationsTooExpensive++;
    }

    /**
     * Returns the number of cancelled transactions due to the gas being more expensive than what the customer wanted to pay
     * 
     * @return the number of cancelled transactions
     */
    public int getNumberOfCancellationsTooExpensive() {
        return this.numberOfCancellationsTooExpensive;
    }

    /**
     * Get the price for a specific type of gas
     * 
     * @param type
     *            the type of gas
     * @return the price per liter for this type of gas
     */
    public double getPrice(GasType type) {
        return (Double) this.hTablePriceOfGas.get(type);
    }

    /**
     * Set a new price for a specific type of gas
     * 
     * @param type
     *            the type of gas
     * @param price
     *            the new price per liter for this type of gas
     */
    public void setPrice(GasType type, double price) {
        this.hTablePriceOfGas.put(type, price);
    }
}
