package net.dataforte.infinispan.playground.embeddedhotrod;

import java.util.Arrays;

import org.infinispan.commons.equivalence.Equivalence;

/**
 * AnyServerEquivalence. Works for both objects and byte[]
 *
 * @author Tristan Tarrant
 * @since 5.3
 */
public class AnyServerEquivalence implements Equivalence<Object> {

   private static boolean isByteArray(Object obj) {
        return byte[].class == obj.getClass();
    }

    public int hashCode(Object obj) {
        if (isByteArray(obj)) {
            return 41 + Arrays.hashCode((byte[]) obj);
        } else {
            return obj.hashCode();
        }
    }

    public boolean equals(Object obj, Object otherObj) {
        if (obj == otherObj)
            return true;
        if (obj == null || otherObj == null)
            return false;
        if (isByteArray(obj) && isByteArray(otherObj))
            return Arrays.equals((byte[]) obj, (byte[]) otherObj);
        return obj.equals(otherObj);
    }

    public String toString(Object obj) {
        if (isByteArray(obj))
            return Arrays.toString((byte[]) obj);
        else
            return obj.toString();
    }

    public boolean isComparable(Object obj) {
        return obj instanceof Comparable;
    }

    public int compare(Object obj, Object otherObj) {
       return ((Comparable<Object>) obj).compareTo(otherObj);
    }

}
