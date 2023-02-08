package nl.tudelft.sem.template.order.domain;

import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;

import static java.time.format.ResolverStyle.STRICT;

/**
 * Date validator class that checks the validity of a date
 */
public class DateValidator{
    /**
     * Method to check validity of date that is given as a string - must be in format 'uuuu-MM-dd'
     * @param date String representing date
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String date){
        try{
            LocalDate.parse(date, DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withChronology(IsoChronology.INSTANCE)
                    .withResolverStyle(STRICT));
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
