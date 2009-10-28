package org.projectusus.core.internal.proportions.model;

import org.eclipse.core.resources.IFile;

public class MetricCCHotspot implements IMetricCCHotspot {

    private final String className;
    private final String methodName;
    private final int cyclomaticComplexity;
    private IFile file;

    public MetricCCHotspot( String className, String methodName, int cyclomaticComplexity ) {
        super();
        this.className = className;
        this.methodName = methodName;
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public void setFile( IFile file ) {
        this.file = file;
    }

    public String getClassName() {
        return className;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public IFile getFile() {
        return file;
    }

    public String getMethodName() {
        return methodName;
    }

}
