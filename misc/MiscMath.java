package misc;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Random;

public class MiscMath {

    private static Random r;
    
    /**
     * Randomly select an element from the specified array.
     * @param a The array to select from.
     * @return An Object that must be cast to the expected output type.
     */
    public static Object randomElement(Object[] a) { return a[MiscMath.randomInt(0, a.length-1)]; }
    
    /**
     * Generate a random integer between the range [min, max] (inclusive)
     * @param min The minimum value.
     * @param max The maximum value.
     * @return An integer within the specifie range.
     */
    public static int randomInt(int min, int max) {
        if (r == null) r = new Random();
        if (max < Integer.MAX_VALUE) max += 1;
        return min + r.nextInt(Math.abs(min-max));
    }

    /**
     * Calculates distance between two points. sqrt()
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The distance between (x1, y1) and (x2, y2), as a double.
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Determines if point (x,y) intersects rectangle (rx, ry, rw, rh).
     * @param x  The x value of the point.
     * @param y  The y value of the point.
     * @param rx The x value of the rectangle.
     * @param ry The y value of the rectangle.
     * @param rw The width of the rectangle.
     * @param rh The height of the rectangle.
     * @return A boolean indicating whether the point intersects.
     */
    public static boolean pointIntersectsRect(double x, double y,
                                              double rx, double ry, double rw, double rh) {
        return x > rx && x < rx + rw && y > ry && y < ry + rh;
    }

    /**
     * Determines if two rectangles intersect each other. Checks each point to see if they
     * intersect the other rectangle. Calls on pointIntersects().
     *
     * @param x  The x of the first rectangle.
     * @param y  The y of the first rectangle.
     * @param w  The width of the first rectangle.
     * @param h  The height of the first rectangle.
     * @param x2 The x of the second rectangle.
     * @param y2 The y of the second rectangle.
     * @param w2 The width of the second rectangle.
     * @param h2 The height of the second rectangle.
     * @return A boolean indicating intersection.
     */
    public static boolean rectanglesIntersect(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
        if (MiscMath.pointIntersectsRect(x, y, x2, y2, w2, h2) || MiscMath.pointIntersectsRect(x + w, y, x2, y2, w2, h2)
                || MiscMath.pointIntersectsRect(x + w, y + h, x2, y2, w2, h2) || MiscMath.pointIntersectsRect(x, y + h, x2, y2, w2, h2)) {
            return true;
        }
        if (MiscMath.pointIntersectsRect(x2, y2, x, y, w, h) || MiscMath.pointIntersectsRect(x2 + w2, y2, x, y, w, h)
                || MiscMath.pointIntersectsRect(x2 + w2, y2 + h2, x, y, w, h) || MiscMath.pointIntersectsRect(x2, y2 + h2, x, y, w, h)) {
            return true;
        }
        return false;
    }

    /**
     * Check if a rectangle intersects a circle.
     *
     * @param x The rectangle's x-coordinate.
     * @param y The rectangle's y-coordinate.
     * @param w The rectangle's width.
     * @param h The rectangle's height.
     * @param cx The origin x of the circle.
     * @param cy The origin y of the circle.
     * @param r  The radius of the circle.
     */
    public static boolean rectangleIntersectsCircle(double x, double y, int w, int h, double cx, double cy, int r) {
        double r_x = x + (w / 2), r_y = y + (h / 2);
        double min_width = (w / 2) + r, min_height = (h / 2) + r;
        return MiscMath.distance(r_x, 0, cx, 0) < min_width
                && MiscMath.distance(r_y, 0, cy, 0) < min_height;
    }

    /**
     * Get the smallest of the two values.
     * @param a The first value.
     * @param b The second value.
     * @return The smallest value, as a double.
     */
    public static double min(double a, double b) {
        return a <= b ? a : b;
    }

    /**
     * Get the largest of the two values.
     * @param a The first value.
     * @param b The second value.
     * @return The largest value, as a double.
     */
    public static double max(double a, double b) {
        return a >= b ? a : b;
    }

    /**
     * Clamp the specified value so that it is within the specified range. If it is already
     * within the range, return the value. For example, clamping 5 to [2, 3] would return 3.
     * @param x The value to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The clamped value, as a double.
     */
    public static double clamp(double x, double min, double max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    /**
     * Rounds <i>a</i> to the nearest <i>b</i>.
     * @return The value of <i>a</i> after rounding.
     */
    public static double round(double a, double b) {
        return Math.round(a / b) * b;
    }
    
    /**
     * Rounds <i>a</i> to the smallest <i>b</i>.
     * @return The value of <i>a</i> after rounding.
     */
    public static double floor(double a, double b) {
        return Math.floor(a / b) * b;
    }

}