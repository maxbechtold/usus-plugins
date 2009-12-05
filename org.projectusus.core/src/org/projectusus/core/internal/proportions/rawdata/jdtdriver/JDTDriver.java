// Copyright (c) 2009 by the projectusus.org contributors
// This software is released under the terms and conditions
// of the Eclipse Public License (EPL) 1.0.
// See http://www.eclipse.org/legal/epl-v10.html for details.
package org.projectusus.core.internal.proportions.rawdata.jdtdriver;

import static org.projectusus.core.internal.util.CoreTexts.jdtDriver_computing;
import static org.projectusus.core.internal.util.TracingOption.SQI;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.projectusus.core.internal.proportions.ICodeProportionComputationTarget;
import org.projectusus.core.internal.proportions.rawdata.WorkspaceRawData;

public class JDTDriver {

    private final ICodeProportionComputationTarget target;

    public JDTDriver( ICodeProportionComputationTarget target ) {
        this.target = target;
    }

    public void run( IProgressMonitor monitor ) throws CoreException {
        for( IProject removedProject : target.getRemovedProjects() ) {
            WorkspaceRawData.getInstance().dropRawData( removedProject );
        }
        monitor.beginTask( jdtDriver_computing, countTicks( target.getProjects() ) );
        for( IProject project : target.getProjects() ) {
            monitor.subTask( project.getName() );
            WorkspaceRawData.getInstance().setCurrentProject( project );
            for( IFile removedFile : target.getRemovedFiles( project ) ) {
                WorkspaceRawData.getInstance().dropRawData( removedFile );
            }
            runInternal( project, monitor );
        }
        monitor.done();
    }

    private int countTicks( Collection<IProject> projects ) throws CoreException {
        int result = 0;
        for( IProject project : projects ) {
            result += target.getJavaFiles( project ).size();
        }
        return result;
    }

    private void runInternal( IProject project, IProgressMonitor monitor ) throws CoreException {
        Collection<IFile> files = target.getJavaFiles( project );
        if( !files.isEmpty() ) {
            StatusCollector statusCollector = new StatusCollector();
            runDriver( project, files, statusCollector, monitor );
            statusCollector.finish();
        }
    }

    private void runDriver( IProject project, Collection<IFile> files, StatusCollector statusCollector, IProgressMonitor monitor ) {
        computationStarted( project );
        for( IFile file : files ) {
            fileStarted( file );
            runDriverOnFile( file, statusCollector, monitor );
            fileFinished();
        }
        computationFinished();
    }

    private void runDriverOnFile( IFile file, StatusCollector statusCollector, IProgressMonitor monitor ) {
        try {
            new FileDriver( file ).compute();
        } catch( Exception ex ) {
            statusCollector.add( ex );
        } finally {
            monitor.worked( 1 );
        }
    }

    private void computationStarted( IProject project ) {
        SQI.trace( "Computation started: " + project.toString() ); //$NON-NLS-1$
        WorkspaceRawData.getInstance().setCurrentProject( project );
    }

    private void fileStarted( IFile file ) {
        SQI.trace( "File started: " + file.getFullPath() ); //$NON-NLS-1$
        WorkspaceRawData.getInstance().dropRawData( file );
        WorkspaceRawData.getInstance().setCurrentFile( file );
    }

    private void fileFinished() {
        WorkspaceRawData.getInstance().setCurrentFile( null );
    }

    private void computationFinished() {
        WorkspaceRawData.getInstance().setCurrentProject( null );
    }
}