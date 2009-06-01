/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.exceptions.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StackTraceFilter {
    
    public boolean isBad(StackTraceElement e) {
        boolean fromMockObject = e.getClassName().contains("$$EnhancerByMockitoWithCGLIB$$");
        boolean fromOrgMockito = e.getClassName().startsWith("org.mockito.");
        boolean isRunner = e.getClassName().startsWith("org.mockito.runners.");
        boolean isInternalRunner = e.getClassName().startsWith("org.mockito.internal.runners.");
        return (fromMockObject || fromOrgMockito) && !isRunner && !isInternalRunner;
    }

    /**
     * Example how the filter works (+/- means good/bad):
     * [a+, b+, c-, d+, e+, f-, g+] -> [a+, b+, g+]
     * Basically removes all bad from the middle. If any good are in the middle of bad those are also removed. 
     */
    public StackTraceElement[] filter(StackTraceElement[] target, int startWith) {
        List<StackTraceElement> unfilteredStackTrace = Arrays.asList(target);
        
        int lastBad = -1;
        int firstBad = -1;
        for (int i = startWith; i < unfilteredStackTrace.size(); i++) {
            if (!this.isBad(unfilteredStackTrace.get(i))) {
                continue;
            }
            lastBad = i;
            if (firstBad == -1) {
                firstBad = i;
            }
        }
        
        List<StackTraceElement> top;
        if (firstBad != -1) {
            top = unfilteredStackTrace.subList(startWith, firstBad);
        } else {
            top = new LinkedList<StackTraceElement>();
        }
        
        List<StackTraceElement> bottom = unfilteredStackTrace.subList(lastBad + 1, unfilteredStackTrace.size());
        List<StackTraceElement> filtered = new ArrayList<StackTraceElement>(top);
        filtered.addAll(bottom);
        return filtered.toArray(new StackTraceElement[]{});
    }
}