package spike;

import info.openrocket.core.models.atmosphere.AtmosphericConditions;
import info.openrocket.core.models.atmosphere.ExtendedISAModel;
import info.openrocket.core.util.Coordinate;
import info.openrocket.core.util.Quaternion;

/**
 * Spike B smoke test: exercise REAL OpenRocket kernel code (ISA atmosphere,
 * Coordinate, Quaternion) compiled to JavaScript by TeaVM.
 *
 * Expected reference values (from OpenRocket's ISA implementation):
 *  - ISA @ 0 m:    T = 288.15 K, P = 101325 Pa
 *  - ISA @ 5000 m: T ≈ 255.65 K, P ≈ 54019 Pa (accept ±1%)
 *  - Mach 1 @ 0 m ≈ 340.3 m/s
 */
public final class Main {
    public static void main(String[] args) {
        ExtendedISAModel isa = new ExtendedISAModel();

        AtmosphericConditions c0 = isa.getConditions(0);
        AtmosphericConditions c5k = isa.getConditions(5000);

        System.out.println("ISA@0m    T=" + c0.getTemperature() + " K, P=" + c0.getPressure()
                + " Pa, rho=" + c0.getDensity() + " kg/m3, a=" + c0.getMachSpeed() + " m/s");
        // NaN-print anomaly check: is the value wrong, or just its toString?
        System.out.println("P0 rounded = " + Math.round(c0.getPressure())
                + ", isNaN = " + Double.isNaN(c0.getPressure()));
        System.out.println("ISA@5000m T=" + c5k.getTemperature() + " K, P=" + c5k.getPressure()
                + " Pa, rho=" + c5k.getDensity() + " kg/m3");

        // Quaternion: rotate the unit X vector 90° about Z — expect ~ (0, 1, 0).
        Quaternion q = Quaternion.rotation(new Coordinate(0, 0, Math.PI / 2));
        Coordinate rotated = q.rotate(new Coordinate(1, 0, 0));
        System.out.println("rot90z(X) = " + rotated);

        boolean ok =approx(c0.getTemperature(), 288.15, 0.01)
                && approx(c0.getPressure(), 101325, 1)
                && approx(c5k.getTemperature(), 255.65, 0.1)
                && approx(c5k.getPressure(), 54019, 600)
                && approx(rotated.y, 1.0, 1e-9);
        System.out.println(ok ? "SPIKE-B: PASS" : "SPIKE-B: FAIL");
    }

    private static boolean approx(double actual, double expected, double tol) {
        return Math.abs(actual - expected) <= tol;
    }
}
