package group35.droneDeliverySystem.Classes;

// import java.awt.geom.NoninvertibleTransformException;
// import java.util.Date;
import java.time.LocalDate;
import java.util.Objects;

public class Utility {
    private static int limitCount = 5;
    private static int limitPeriod = 30;
    private static int returningTimeFrame = 90;

    private Utility() {
    }

    public static LocalDate getCurrentTime() {
        return LocalDate.now();
    }

    public static int getLimitCount() {
        return Utility.limitCount;
    }

    public static void updateLimitCount(int Count) {
        Utility.limitCount = Count;

    }

    public static int getLimitPeriod() {
        return Utility.limitPeriod;
    }

    public static void updateLimitPeriod(int Days) {
        Utility.limitPeriod = Days;
    }

    public static int getReturnTimeFrame() {
        return Utility.returningTimeFrame;
    }

    public static void updateReturningTimeFrame(int Days) {
        Utility.returningTimeFrame = Days;
    }

    public static int getDaysDiff(LocalDate date1) {
        LocalDate currentDate = Utility.getCurrentTime();
        return (int) (currentDate.toEpochDay() - date1.toEpochDay());
    }

    public static Boolean validateReturnTimeFrame(LocalDate date) {
        int timeFrame = getDaysDiff(date);
        int returnTimeFrame = getReturnTimeFrame();
        if (timeFrame > returnTimeFrame) {
            return false;
        }
        return true;

    }

    public static Boolean validateReturnFrequency(int returnCount) {
        int limitCount = Utility.limitCount;
        if (returnCount >= limitCount) {
            return false;
        }
        return true;
    }

}
