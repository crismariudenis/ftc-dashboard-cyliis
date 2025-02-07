package org.firstinspires.ftc.teamcode.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SixthDegreePolynomialMagic {
    public static double[] getFirstValley(Polynomial polynomial, double leftBound, double rightBound) {

        Polynomial secondDerivative = polynomial.getDerivative().getDerivative();

        double[] inflectionPoints = solveQuartic(secondDerivative.getCoefficient(4),
                secondDerivative.getCoefficient(3), secondDerivative.getCoefficient(2),
                secondDerivative.getCoefficient(1), secondDerivative.getCoefficient(0));

        ArrayList<Double> inflectionPointsWithinBounds = new ArrayList<>();

        inflectionPointsWithinBounds.add(leftBound);

        for(int i = 0; i<inflectionPoints.length;i++){
            if(inflectionPoints[i] < leftBound || inflectionPoints[i] > rightBound) continue;
            inflectionPointsWithinBounds.add(inflectionPoints[i]);
        }

        inflectionPointsWithinBounds.add(rightBound);

        Collections.sort(inflectionPointsWithinBounds);

        for (int i = 0;i<inflectionPointsWithinBounds.size()-1;i++){

            double midPoint = (inflectionPointsWithinBounds.get(i) + inflectionPointsWithinBounds.get(i+1))/2.0;
            if(secondDerivative.evaluate(midPoint) <= 0) continue;

            double possibleValley = convexTernarySearch(inflectionPointsWithinBounds.get(i),
                    inflectionPointsWithinBounds.get(i+1), polynomial);

            if(secondDerivative.evaluate(possibleValley) <= 0) continue;

            return new double[]{possibleValley};
        }

        return null;
    }

    public static ArrayList<Double> getAllValleys(Polynomial polynomial, double leftBound, double rightBound) {

        Polynomial secondDerivative = polynomial.getDerivative().getDerivative();

        double[] inflectionPoints = solveQuartic(secondDerivative.getCoefficient(4),
                secondDerivative.getCoefficient(3), secondDerivative.getCoefficient(2),
                secondDerivative.getCoefficient(1), secondDerivative.getCoefficient(0));

        ArrayList<Double> inflectionPointsWithinBounds = new ArrayList<>();

        inflectionPointsWithinBounds.add(leftBound);

        ArrayList<Double> valleys = new ArrayList<>();

        if(inflectionPoints != null) {
            for (int i = 0; i < inflectionPoints.length; i++) {
                if (inflectionPoints[i] < leftBound || inflectionPoints[i] > rightBound) continue;
                inflectionPointsWithinBounds.add(inflectionPoints[i]);
            }
        }

        inflectionPointsWithinBounds.add(rightBound);

        Collections.sort(inflectionPointsWithinBounds);
//        System.out.println(inflectionPointsWithinBounds);

        for (int i = 0;i<inflectionPointsWithinBounds.size()-1;i++){

            double midPoint = (inflectionPointsWithinBounds.get(i) + inflectionPointsWithinBounds.get(i+1))/2.0;
            if(secondDerivative.evaluate(midPoint) <= 0) continue;

            double possibleValley = convexTernarySearch(inflectionPointsWithinBounds.get(i),
                    inflectionPointsWithinBounds.get(i+1), polynomial);

            if(secondDerivative.evaluate(possibleValley) <= 0) continue;

            valleys.add(possibleValley);
        }

        return valleys;
    }

    private static double[] solveQuartic(double a, double b, double c, double d, double e) {
        double inva = 1 / a;
        double c1 = b * inva;
        double c2 = c * inva;
        double c3 = d * inva;
        double c4 = e * inva;
        // cubic resolvant
        double c12 = c1 * c1;
        double p = -0.375 * c12 + c2;
        double q = 0.125 * c12 * c1 - 0.5 * c1 * c2 + c3;
        double r = -0.01171875 * c12 * c12 + 0.0625 * c12 * c2 - 0.25 * c1 * c3 + c4;
        double z = solveCubicForQuartic(-0.5 * p, -r, 0.5 * r * p - 0.125 * q * q);
        double d1 = 2.0 * z - p;
        if (d1 < 0) {
            if (d1 > 1.0e-10)
                d1 = 0;
            else
                return null;
        }
        double d2;
        if (d1 < 1.0e-10) {
            d2 = z * z - r;
            if (d2 < 0)
                return null;
            d2 = Math.sqrt(d2);
        } else {
            d1 = Math.sqrt(d1);
            d2 = 0.5 * q / d1;
        }
        // setup usefull values for the quadratic factors
        double q1 = d1 * d1;
        double q2 = -0.25 * c1;
        double pm = q1 - 4 * (z - d2);
        double pp = q1 - 4 * (z + d2);
        if (pm >= 0 && pp >= 0) {
            // 4 roots (!)
            pm = Math.sqrt(pm);
            pp = Math.sqrt(pp);
            double[] results = new double[4];
            results[0] = -0.5 * (d1 + pm) + q2;
            results[1] = -0.5 * (d1 - pm) + q2;
            results[2] = 0.5 * (d1 + pp) + q2;
            results[3] = 0.5 * (d1 - pp) + q2;
            // tiny insertion sort
            for (int i = 1; i < 4; i++) {
                for (int j = i; j > 0 && results[j - 1] > results[j]; j--) {
                    double t = results[j];
                    results[j] = results[j - 1];
                    results[j - 1] = t;
                }
            }
            return results;
        } else if (pm >= 0) {
            pm = Math.sqrt(pm);
            double[] results = new double[2];
            results[0] = -0.5 * (d1 + pm) + q2;
            results[1] = -0.5 * (d1 - pm) + q2;
            return results;
        } else if (pp >= 0) {
            pp = Math.sqrt(pp);
            double[] results = new double[2];
            results[0] = 0.5 * (d1 - pp) + q2;
            results[1] = 0.5 * (d1 + pp) + q2;
            return results;
        }
        return null;
    }

    private static final double solveCubicForQuartic(double p, double q, double r) {
        double A2 = p * p;
        double Q = (A2 - 3.0 * q) / 9.0;
        double R = (p * (A2 - 4.5 * q) + 13.5 * r) / 27.0;
        double Q3 = Q * Q * Q;
        double R2 = R * R;
        double d = Q3 - R2;
        double an = p / 3.0;
        if (d >= 0) {
            d = R / Math.sqrt(Q3);
            double theta = Math.acos(d) / 3.0;
            double sQ = -2.0 * Math.sqrt(Q);
            return sQ * Math.cos(theta) - an;
        } else {
            double sQ = Math.pow(Math.sqrt(R2 - Q3) + Math.abs(R), 1.0 / 3.0);
            if (R < 0)
                return (sQ + Q / sQ) - an;
            else
                return -(sQ + Q / sQ) - an;
        }
    }

    public static double acceptableError = 1e-5;

    private static final double convexTernarySearch(double l, double r, Polynomial p){
        double eps = acceptableError;              //set the error limit here
        int iterations = 0;
        while (r - l > eps) {
            iterations++;
            double m1 = l + (r - l) / 3;
            double m2 = r - (r - l) / 3;
            double f1 = p.evaluate(m1);      //evaluates the function at m1
            double f2 = p.evaluate(m2);      //evaluates the function at m2
            if (f1 > f2)
                l = m1;
            else
                r = m2;
        }
        return l;
    }
}
